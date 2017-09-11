package com.dataart.edu.server.dao;

import com.dataart.edu.message.dto.BirdDto;
import com.dataart.edu.message.dto.BirdSightDto;
import com.dataart.edu.message.format.util.FileUtil;
import com.dataart.edu.server.ConfigurationCreator.ServerConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import static com.dataart.edu.server.ServerMessageProcessor.WAIT_TERMINATION_PERIOD_SECONDS;
import lombok.extern.slf4j.Slf4j;

/**
 * Wrapper of DAO, which will periodically store data to disk.
 *
 * @author alitvinov
 * @version 1.0.0
 */
@Slf4j
public class BirdsDaoWrapper {

    /**
     * File name with information about sightings.
     */
    private final static String BSIGHTS_FILE_NAME = "sights";
    /**
     * File name with information about birds.
     */
    private final static String BIRD_FILE_NAME = "birds";
    /**
     * Used for converting objects to String
     */
    private final static ObjectMapper jsonConverter = new ObjectMapper();
    /**
     * How often save process will arise.
     */
    private final static int DEFAULT_SAVE_PERIOD_IN_SECONDS = 10;
    /**
     * Flush size, to save data from memory to file.
     */
    private final static int DEFAULT_FLUSH_SIZE = 100;
    /**
     * Executor, which will periodically save data from disk to memory.
     */
    private final ScheduledExecutorService backgroundSaveSheduler = Executors.newScheduledThreadPool(1);
    /**
     * DAO.
     */
    @Autowired
    private IBirdsDao birdsDao;
    /**
     * Configuration.
     */
    @Autowired
    private ServerConfiguration config;
    /**
     * ReadWriteLock to synchronize DAO and DAO wrapper.
     */
    @Autowired
    private ReadWriteLock readWriteLock;
    /**
     * Path to birds file.
     */
    private Path pathToBirds;
    /**
     * Path to sightings file.
     */
    private Path pathToSightings;

    /**
     * Starting of periodically saving to disk, starting of shutdown hook.
     *
     * @throws IOException if problem with file access arise.
     */
    @PostConstruct
    private void postConstruct() throws IOException {
        this.pathToBirds = Paths.get(config.getDataDirectory() + File.separator + BIRD_FILE_NAME);
        this.pathToSightings = Paths.get(config.getDataDirectory() + File.separator + BSIGHTS_FILE_NAME);
        createFilesIfNecesary();
        this.initDaoByValuesFromFile();
        this.registerShutdownHook();
        this.startPeriodicSaveToDisk();
    }

    /**
     * Load values from files to DAO.
     *
     * @throws IOException if problem with file access arise.
     */
    private void initDaoByValuesFromFile() throws IOException {
        this.populateDaoWithBirds();
        this.populateDaoWithSightings();
        ((BirdAndSightDaoImpl) birdsDao).setReadWriteLock(readWriteLock);
    }

    /**
     * Load Birds from file to DAO.
     *
     * @throws IOException
     */
    private void populateDaoWithBirds() throws IOException {
        this.populateDaoWithOject(pathToBirds, BirdDto.class, (bird) -> {
            birdsDao.addBird(bird);
        });
    }

    /**
     * Load sightings from file to DAO.
     *
     * @throws IOException
     */
    private void populateDaoWithSightings() throws IOException {
        this.populateDaoWithOject(pathToSightings, BirdSightDto.class, (sighting) -> {
            birdsDao.addSight(sighting);
        });
    }

    /**
     * Register shutdown hook, which will start during shutdown.
     * <p>
     * <b>Important: </b> in some cases shutdown hook will not start. Read more
     * details
     * <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/lang/hook-design.html">
     * here</a>
     */
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutdown occured. Try save all data to files.");
                flushDaoToDisk(true);
                System.out.println("Shutdown hook successfully saved data.");
            }
        });
        log.info("Shutdown hook successfully registered.");
    }

    /**
     * Starting periodically save data from DAO to disk.
     */
    private void startPeriodicSaveToDisk() {
        backgroundSaveSheduler.scheduleAtFixedRate(() -> {
            flushDaoToDisk(false);
        }, 0, DEFAULT_SAVE_PERIOD_IN_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Stopping of periodical saving data from DAO to disk.
     *
     * @return true - if successfully stopped.
     */
    public boolean stopPeriodicSaveToDisk() {
        boolean isShedullerStopped;
        this.backgroundSaveSheduler.shutdownNow();
        try {
            isShedullerStopped = this.backgroundSaveSheduler.awaitTermination(WAIT_TERMINATION_PERIOD_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.info("Fail to stop DAO wrapper executor. {}", e.getMessage());
            isShedullerStopped = false;
        }
        return isShedullerStopped;
    }

    /**
     * Load data from file into DAO.
     *
     * @param <T> type of object to load from DAO.
     * @param fileToRead file to read.
     * @param objectTypeToRead object type, that must be read.
     * @param consumer consumer, which accept read object.
     * @throws IOException
     */
    private <T> void populateDaoWithOject(
            Path fileToRead,
            Class<T> objectTypeToRead,
            Consumer<T> consumer)
            throws IOException {
        try (
                InputStreamReader isr = new InputStreamReader(new FileInputStream(fileToRead.toFile()), StandardCharsets.UTF_8.name());
                BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                T deserialized = jsonConverter.readValue(line, objectTypeToRead);
                consumer.accept(deserialized);
            }
        }
    }

    /**
     * Create index files, if they not exist.
     *
     * @throws IOException
     */
    private void createFilesIfNecesary() throws IOException {
        FileUtil.createIfNotExists(pathToBirds);
        FileUtil.createIfNotExists(pathToSightings);
    }

    /**
     * Clear files with data, before writing new data to disk.
     *
     * @throws IOException
     */
    private void recreateFiles() throws IOException {
        FileUtil.recreateIfExists(pathToBirds);
        FileUtil.recreateIfExists(pathToSightings);
    }

    /**
     * Saving data to disk.
     *
     * @param isShutdown true - save must be immediately, without
     * synchronization by readWriteLock
     */
    private void flushDaoToDisk(boolean isShutdown) {
        if (!isShutdown) {
            if (!readWriteLock.writeLock().tryLock()) {
                return;
            }
        }
        try {
            recreateFiles();
            List<String> birds = new ArrayList<>(DEFAULT_FLUSH_SIZE);
            List<String> sightings = new ArrayList<>(DEFAULT_FLUSH_SIZE);
            birdsDao.findAllBirds().stream().forEach((bird) -> {
                addStringToPortion(birds, bird, pathToBirds);
                Set<BirdSightDto> birdSightings = birdsDao.findSightings(bird.getName());
                if (birdSightings != null) {
                    birdSightings.stream().forEach((sighting) -> {
                        addStringToPortion(sightings, sighting, pathToSightings);
                    });
                }
            });
            if (!birds.isEmpty()) {
                flushPortionToDisk(birds, pathToBirds);
            }
            if (!sightings.isEmpty()) {
                flushPortionToDisk(sightings, pathToSightings);
            }
        } catch (IOException | IllegalArgumentException ex) {
            log.error("Error during saving dao data to file.", ex);
        } finally {
            if (!isShutdown) {
                readWriteLock.writeLock().unlock();
            }
        }
    }

    /**
     * Portion write data from DAO to disk.
     *
     * @param portionToFlush portion of data.
     * @param data object that must be added to portion.
     * @param filePath file path, where data will be wrote.
     * @throws IllegalArgumentException
     */
    private void addStringToPortion(List<String> portionToFlush, Object data, Path filePath) throws IllegalArgumentException {
        try {
            portionToFlush.add(jsonConverter.writeValueAsString(data));
            if (portionToFlush.size() >= DEFAULT_FLUSH_SIZE) {
                flushPortionToDisk(portionToFlush, filePath);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem during saving dao data to file", e);
        }
    }

    /**
     * Flushing portion of data to disk.
     *
     * @param portionToFlush portion of strings to be wrote.
     * @param filePath file path, where data will be wrote.
     * @throws IOException
     */
    private void flushPortionToDisk(List<String> portionToFlush, Path filePath) throws IOException {
        Files.write(filePath, portionToFlush);
        portionToFlush.clear();
    }
}
