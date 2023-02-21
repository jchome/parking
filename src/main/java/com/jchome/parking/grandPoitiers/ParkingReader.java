package com.jchome.parking.grandPoitiers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jchome.parking.generic.InvalidJsonException;
import com.jchome.parking.generic.Parking;

public class ParkingReader {

    private static final Logger log = LoggerFactory.getLogger(ParkingReader.class);

    public List<Parking> getParkingsAvailability(String serverUrl) throws InvalidJsonException {
        JSONObject json = new JSONObject();
		try {
			json = getJson(new URL(serverUrl));
		} catch (MalformedURLException e) {
            String message = "Error while converting the URL: " + e.getMessage();
            log.error(message);
            throw new InvalidJsonException(message);
        }
        return convertAsParkings(json);

    }

    /**
     * Retreive data in the JSONObject to build a Parking instance. 
     * 
     * @param fields The JSON object containing data for one Parking
     * @return The instance of Parking is success.
     * @throws InvalidJsonException Thrown in case on missing required data
     */
    public Parking readFromAvailability(JSONObject fields) throws InvalidJsonException{
        // Check the required field "nom"
        if(! fields.has("nom")){
            String message = "The key <nom> was not found in JSON.";
            log.error(message);
            throw new InvalidJsonException(message);
        }

        String name = fields.getString("nom");
        log.debug("Reading the name of the parking: " + name);

        // Check the required field geo_point_2d
        if(! fields.has("geo_point_2d")){
            String message = "The key <geo_point_2d> was not found in JSON.";
            log.error(message);
            throw new InvalidJsonException(message);
            
        }
        JSONArray geoPoint = fields.getJSONArray("geo_point_2d");

        if (geoPoint.length() != 2){
            String message = "The key <geo_point_2d> does NOT contain 2 children nodes.";
            log.error(message);
            throw new InvalidJsonException(message);
        }
        Double lat = geoPoint.getDouble(0);
        log.debug("Reading the lat of the parking: " + lat);

        Double lon = geoPoint.getDouble(1);
        log.debug("Reading the lon of the parking: " + lon);

        Integer capacity = fields.getInt("capacite");
        log.debug("Reading the capacity of the parking: " + capacity);

        Integer vacancy = fields.getInt("places");
        log.debug("Reading the vacancy of the parking: " + vacancy);

        Parking result = new Parking(name, lat, lon, capacity, vacancy);
        log.debug("Parking found: " + result.toString());

        return result;
    }
    
    /**
     * Convert the JSON object as Parking instances.
     * 
     * When a json node cannot be conveted (because of missing data on required fields), 
     * the instance is not created. This will not stop the process.
     * 
     * @param json The JSON object containing data
     * @return The list of Parking instances.
     */
    public List<Parking> convertAsParkings(JSONObject json){
		List<Parking> result = new ArrayList<>();

        if(!json.has("records")){
            log.error("The json data does not contain the <records> node as expected.");
            return result;
        }
		JSONArray records = json.getJSONArray("records");

        if(records.length() == 0){
            log.error("The json data has no children in the <records> node.");
            return result;
        }
        // Loop on each nodes to get the Parking instance
		for (int i = 0; i<records.length(); i++) {
			JSONObject record = records.getJSONObject(i);
            if(!record.has("fields")){
                // There is no "fields"
                log.error("The json data has no node named <fields> in the child of <records> node.");
                continue;
            }
			JSONObject fields = record.getJSONObject("fields");
			
            Parking parking;
            try {

                log.debug("Reading json object <fields>.");
                parking = readFromAvailability(fields);

                log.debug("Conversion as Parking...");

                result.add(parking);
                log.debug("Parking added.");

            } catch (InvalidJsonException e) {
                log.error("The JSON is not as excepted on node #" + i);
                log.error(fields.toString(2));
                // In case of any exception, continue with the next json item 
            }

		}
		return result;
	}

    /**
     * Call the URL and create the JSON object.
     * 
     * @param url The url of the server
     * @return the JSON Object parsed with the result
     * @throws InvalidJsonException In case of error in the URL
     */
	public JSONObject getJson(URL url) throws InvalidJsonException {
		String json = "";
        try {
            // Use the org.apache IOUtils to ease the coding 
            json = IOUtils.toString(url, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new InvalidJsonException("Error with the URL of the server:" + e.getMessage());
        }
		return new JSONObject(json);
	}


}
