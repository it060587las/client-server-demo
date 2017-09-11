package com.dataart.edu.message.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BirdSighting DTO(Data Transfer Object).
 *
 * @author alitvinov
 * @vesrion 1.0.0
 * @since 2017-09-06
 */
@Data
@NoArgsConstructor
public class BirdSightDto {

    private String name;

    private String location;

    private long start;

    private long end;

    /**
     * Create BirdSightDto.
     *
     * @param name name of bird.
     * @param location location of sightings.
     * @param sightingDate sighting date as long.
     */
    public BirdSightDto(String name, String location, long sightingDate) {
        this.name = name;
        this.start = sightingDate;
        this.location = location;
    }

    /**
     * Create BirdSightDto to use it as query parameter to find list of
     * sightings.
     *
     * @param name name of bird.
     * @param location location.
     * @param start start of search period.
     * @param end end of search period.
     */
    public BirdSightDto(String name, String location, long start, long end) {
        this.name = name;
        this.location = location;
        this.start = start;
        this.end = end;
    }
}
