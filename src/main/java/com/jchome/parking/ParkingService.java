package com.jchome.parking;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jchome.parking.generic.InvalidJsonException;
import com.jchome.parking.generic.Parking;
import com.jchome.parking.grandPoitiers.ParkingReader;

@RestController
@RequestMapping("/parkings")
/**
 * This class implement the REST service for parking accessibility. 
 */
public class ParkingService {

	@Value("${source.url.availability}")
	private String serverUrlAvailability;

	/**
	 * Logger for this service
	 */
    private static final Logger log = LoggerFactory.getLogger(ParkingService.class);


    /**
     * Get all parkings, no order
     * 
     * @return The list of parkings
     */
    @GetMapping("/all")
	public List<Parking> getParkings() {
        log.debug("Request for /parkings.");

		List<Parking> parkings = new ArrayList<>();
        ParkingReader reader = new ParkingReader();
		try {
			parkings = reader.getParkingsAvailability(serverUrlAvailability);
		} catch (InvalidJsonException e) {
            log.error("Error while getting parkings on URL " + serverUrlAvailability);
		}

		return parkings;
	}

    /**
     * Get all parkings, ordered by the distance to the point
     * @param lat Latitude of the point
     * @param lon Longitude of the point
     * @param limit (optional) The maximum numbers of item to return
     * @return The list parkings, order by the distance to the point
     */
    @GetMapping("/nearOfPoint")
	public List<Parking> getParkingsNearOfPoint(
            @RequestParam(value="lat", required=true) Double lat, 
            @RequestParam(value="lon", required=true) Double lon, 
            @RequestParam(value="limit", required=false) Integer limit) {
        
        // Get all parkings
        List<Parking> parkings = getParkings();

        // Sort them with the distance of the point
        parkings.sort(new Comparator<Parking>() {

            @Override
            public int compare(Parking p1, Parking p2) {
                Integer distanceToP1 = p1.distanceTo(lat, lon);
                Integer distanceToP2 = p2.distanceTo(lat, lon);
                return distanceToP1.compareTo(distanceToP2);
            }
            
        });

        // Cut the last part of the full list of parkings, unsing the "limit" parameter
        if(limit != null && limit < parkings.size()){
            parkings = parkings.subList(0, limit);
        }

        return parkings;
    }

}
