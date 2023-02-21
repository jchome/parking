package com.jchome.parking.generic;

/**
 * This class will represent any exceptions about the JSON input data
 */
public class InvalidJsonException extends Exception {

    /**
     * Create a new exception with the message
     * @param message
     */
    public InvalidJsonException(String message) {
        // Call the parent constructor
        super(message);
    }

    
}
