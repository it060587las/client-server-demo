package com.dataart.edu.message.format.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author alitvinov
 * @version 1.0.0
 */
@Slf4j
public class FileUtil {

    /**
     * Check permissions to file or directory.
     *
     * @param filePath path to file or directory.
     * @throws IOException
     */
    public static void checkFilePermission(String filePath) throws IOException {
        log.info("Start checking file  {}.", filePath);
        Path path = Paths.get(filePath);
        if (!path.isAbsolute()) {
            throw new IOException("Path muts be absolute path. Please, provide absolute path, or use default.");
        } else if (!Files.exists(path)) {
            if (!Files.notExists(path)) {
                throw new IOException("Probably you don't have permissions");
            }
            Files.createDirectory(path);
            log.info("Successfully created {}", filePath);
        } else {
            if (!Files.isReadable(path) || !Files.isWritable(path)) {
                throw new IOException("Probably you don't have permissions");
            }
        }
        log.info("Checking file {} successfull.", filePath);
    }

    /**
     * Create file if it is not exists.
     *
     * @param pathToFile path to file.
     * @return true if file was created, false if not.
     * @throws IOException
     */
    public static boolean createIfNotExists(Path pathToFile) throws IOException {
        if (!Files.exists(pathToFile)) {
            Files.createFile(pathToFile);
            return true;
        }
        return false;
    }

    /**
     * Re-create file, if it exists.
     *
     * @param pathToFile path to file
     * @return true - if file was re-created.
     * @throws IOException
     */
    public static boolean recreateIfExists(Path pathToFile) throws IOException {
        if (Files.exists(pathToFile)) {
            Files.delete(pathToFile);
            Files.createFile(pathToFile);
            return true;
        }
        return false;
    }
}
