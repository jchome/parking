package com.jchome.parking.generic;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

// All fields are private
@FieldDefaults(level=AccessLevel.PRIVATE)

// Define the costructor with all args
@AllArgsConstructor

// Define all getters and setters
@Getter
@Setter

// Define the equals and hashcode using the key composed of "name" + "lat" + "lon"
@EqualsAndHashCode(of= {"name","lat","lon"})

// Define the toString method
@ToString(of= {"name", "lat", "lon", "capacity", "vacancy"})

/**
 * This class represents the Parking generic entity.
 */
public class Parking implements Serializable{

    @NonNull
    /**
     * Name of the parking
     */
    String name;

    /**
     * Geographic position / latitude
     */
    @NonNull
    Double lat;

    /**
     * Geographic position / longitude
     */
    @NonNull
    Double lon;

    /**
     * Capacity of the parking
     */
    @NonNull
    Integer capacity;

    /**
     * Number of free places
     */
    Integer vacancy;

    
    public int distanceTo(double lat2, double lon2) {
        double lat1 = this.getLat();
        double lon1 = this.getLon();
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}
		else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
            dist = dist * 1609.344; // as Meter
			return Double.valueOf(dist).intValue();
		}
    }

}
