package com.dataart.edu.message.dto.request;

/**
 * Avaliable commands, that client can send to server.
 *
 * @author alitvinov
 */
public enum ClientAction {
    /**
     * Add new bird.
     */
    ADD, 
    /**
     * Remove bird.
     */
    REMOVE, 
    /**
     * Add new sighting.
     */
    ADD_SIGHT, 
    /**
     * Stop server and quite.
     */
    QUIT, 
    /**
     * List of all avaliable birds.
     */
    LIST, 
    /**
     * List of sightings.
     */
    LIST_SIGHTS;
}
