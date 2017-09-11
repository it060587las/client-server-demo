package com.dataart.edu.message.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Birds DTO (Data Transfer Object).
 *
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-06
 */
@Data
@NoArgsConstructor
public class BirdDto {

    private String name;

    private String color;

    private double height;

    private double weight;

    private boolean stored = false;

    /**
     * Create bird instance.
     *
     * @param name name of bird
     * @param color color
     * @param height height
     * @param weight weight
     */
    public BirdDto(String name, String color, double height, double weight) {
        this.name = name;
        this.color = color;
        this.height = height;
        this.weight = weight;
    }

}
