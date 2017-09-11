package com.dataart.edu.server.dao;

import com.dataart.edu.message.dto.BirdSightDto;
import com.dataart.edu.message.dto.BirdDto;
import java.util.List;
import java.util.Set;

/**
 * Data Access Object interface, which declares methods to access birds and
 * sightings.
 *
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-06
 */
public interface IBirdsDao {

    /**
     * Get list of all birds. Result is sorted by bird name.
     *
     * @return List of BirdDto
     * @see BirdDto
     */
    public List<BirdDto> findAllBirds();

    /**
     * Add new bird.
     *
     * @param elementToAdd bird which must be added.
     * @return new bird.
     * @throws IllegalArgumentException if bird already exists.
     * @see BirdDto
     */
    public BirdDto addBird(final BirdDto elementToAdd) throws IllegalArgumentException;

    /**
     * remove bird by name.
     *
     * @param birdToRemoveName name of bird to remove. If bird exists - remove
     * it.
     * @throws IllegalArgumentException throws if bird is not exists.
     */
    public void removeBird(String birdToRemoveName) throws IllegalArgumentException;

    /**
     * Add new sighting.
     *
     * @param elementToAdd element which must be added.
     * @return new element.
     * @throws IllegalArgumentException if bird not exists, or sighting with
     * such name, location and date exists.
     * @see BirdSightDto
     */
    public BirdSightDto addSight(BirdSightDto elementToAdd) throws IllegalArgumentException;

    /**
     * Find sightings
     *
     * @param elementToFind find sightings which name are match as Regular
     * Expression to elementToFind.name and date are between elementToFind.start
     * and elementToFind.end
     * @return List of BirdSightDto
     * @see BirdSightDto
     */
    public List<BirdSightDto> findSight(BirdSightDto elementToFind);

    /**
     * Find all sightings for bird.
     *
     * @param birdName name of bird.
     * @return Set of BirdSightDto
     * @see BirdSightDto
     */
    public Set<BirdSightDto> findSightings(String birdName);
}
