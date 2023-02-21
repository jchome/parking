package com.jchome.parking.grandPoitiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.jchome.parking.generic.InvalidJsonException;
import com.jchome.parking.generic.Parking;



/**
 * The test suite will only test the ParkingReader class
 */
@SpringBootTest
@RunWith(PowerMockRunner.class)
class ParkingReaderTests {

	@Test
	void testGetParkingsAvailability(){
		// Prepare the class to mock
		ParkingReader reader = spy(ParkingReader.class);

		// The call to the real server will be mocked.
		// Use a sample file.
		String sampleFile = "./src/test/resources/sample.json";
		Path pathOfSample = Path.of(sampleFile);
		JSONObject mockJson;
		// Prepare the mock
		try {
			String sampleJsonData = Files.readString(pathOfSample);
			mockJson = new JSONObject(sampleJsonData);
			doReturn(mockJson).when(reader).getJson(any());
		} catch (IOException | JSONException | InvalidJsonException e) {
			fail("Unable to read file " + sampleFile);
			return;
		}
		
		// Let's call the real method
		List<Parking> parkings = null;
		try {
			// Give any serverUrl, but it shout be URL-compliant
			parkings = reader.getParkingsAvailability("http://nothing");
		} catch (InvalidJsonException e) {
			fail("Something goes wrong...");
		}

		// Do checks of the result
		assertNotNull(parkings);
		assertEquals(7, parkings.size());

	}

	// More tests to do with the same philosophy...


}
