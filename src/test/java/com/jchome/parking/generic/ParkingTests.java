package com.jchome.parking.generic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
/**
 * This class will only test the Parking class
 */
class ParkingTests {

	@Test
	/**
	 * Check that the distance is correctly calculated
	 */
	void testDistanceOnParking(){
		double lat = 46.58435129072599;
		double lon = 0.34529622589644204;
		Parking parkingNotreDame = new Parking("Parking Notre-Dame-Marché", lat, lon, 200, 100);
		// The object is created
		assertNotNull(parkingNotreDame);

		// The distance to the same point is 0
		int distanceZero = parkingNotreDame.distanceTo(lat, lon);
		assertEquals(0, distanceZero);

		// Go to Google maps to find the bus stop, and get the path to the parking.
		// It's at 170 meters away.
		// Bus Stop : 46.585142709146254 @ 0.3465447292159096
		double latBusStop = 46.585142709146254;
		double lonBusStop = 0.3465447292159096;
		int distanceToBus = parkingNotreDame.distanceTo(latBusStop, lonBusStop);
		
		// The distance to the bus should be between 100m and 200m
		assertTrue( distanceToBus > 100 && distanceToBus < 200);

	}


}
