package com.dataart.edu.server.dao;

import com.dataart.edu.message.dto.BirdSightDto;
import com.dataart.edu.message.dto.BirdDto;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Data Access Object, which stores all data in memory.
 *
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-06
 */
@Slf4j
public class BirdAndSightDaoImpl implements IBirdsDao {

    private final ConcurrentMap<String, BirdDto> birdsMemoryStore = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<BirdSightDto>> sightingMemoryStore = new ConcurrentHashMap<>();
    /**
     * ReadWriteLock that used to synchronize DAO and DAO wrapper.
     */
    private ReadWriteLock readWriteLock;

    /**
     * ReadWriteLock that is used to synchronize DAO and DAO wrapper component.
     *
     * @param readWriteLock ReentrantReadWriteLock
     */    
    public void setReadWriteLock(ReadWriteLock readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    @Override
    public List<BirdDto> findAllBirds() {
        return birdsMemoryStore
                .values()
                .stream()
                .sorted(Comparator.comparing((BirdDto b) -> b.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public BirdDto addBird(final BirdDto elementToAdd) throws IllegalArgumentException {
        lock();
        try {
            BirdDto bird = birdsMemoryStore.computeIfAbsent(elementToAdd.getName(), (a) -> {
                elementToAdd.setStored(true);
                return elementToAdd;
            });
            if (!bird.equals(elementToAdd)) {
                throw new IllegalArgumentException("try duplicate key bird with name");
            }
            return elementToAdd;
        } finally {
            unlock();
        }
    }

    @Override
    public void removeBird(String birdToRemoveName) throws IllegalArgumentException {
        lock();
        try {
            birdsMemoryStore.compute(birdToRemoveName, (birdName, bird) -> {
                if (bird == null) {
                    throw new IllegalArgumentException("Bird with such name not exists.");
                }
                sightingMemoryStore.remove(birdName);
                return null;
            });
        } finally {
            unlock();
        }
    }

    @Override
    public BirdSightDto addSight(BirdSightDto elementToAdd) throws IllegalArgumentException {
        lock();
        try {
            BirdDto birdEntry = birdsMemoryStore.computeIfPresent(elementToAdd.getName(), (birdname, bird) -> {
                if (!sightingMemoryStore.containsKey(birdname)) {
                    sightingMemoryStore.put(birdname, new HashSet<>());
                }
                if (!sightingMemoryStore.get(birdname).add(elementToAdd)) {
                    throw new IllegalArgumentException("bird sight with such location, date and time already exists");
                }
                return bird;
            });
            if (birdEntry == null) {
                throw new IllegalArgumentException("bird not found");
            }
            return elementToAdd;
        } finally {
            unlock();
        }
    }

    @Override
    public List<BirdSightDto> findSight(BirdSightDto elementToFind) {
        Pattern pattern = Pattern.compile(elementToFind.getName());
        final List<BirdSightDto> returnList = new ArrayList<>();
        sightingMemoryStore.keySet().stream().filter((bsight) -> {
            Matcher matcher = pattern.matcher(bsight);
            return matcher.matches();
        }).forEach((bsight) -> {
            Set<BirdSightDto> set = sightingMemoryStore.get(bsight);
            if (set != null) {
                set
                        .stream()
                        .filter((bsightItem) -> {
                            return bsightItem.getStart() >= elementToFind.getStart() && bsightItem.getStart() <= elementToFind.getEnd();
                        })
                        .sorted(Comparator
                                .comparing((BirdSightDto b) -> b.getName())
                                .thenComparing(b -> b.getStart()))
                        .forEach((bsightItem) -> {
                            returnList.add(bsightItem);
                        });
            }
        });
        return returnList;
    }

    @Override
    public Set<BirdSightDto> findSightings(String birdName) {
        return sightingMemoryStore.get(birdName);
    }

    private void lock() {
        if (readWriteLock != null) {
            readWriteLock.readLock().lock();
        }
    }

    private void unlock() {
        if (readWriteLock != null) {
            readWriteLock.readLock().unlock();
        }
    }
}
