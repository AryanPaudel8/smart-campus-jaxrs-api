/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Root discovery endpoint implementing HATEOAS principles.
 *
 * <p>Exposes {@code GET /api/v1} as a self-describing entry point to the API.
 * Rather than relying entirely on external documentation, clients can interrogate
 * this endpoint to discover available resources, contact details, and usage examples
 * dynamically. This is the hallmark of a mature REST design (Richardson Maturity
 * Level 3): responses contain the links needed to navigate the API rather than
 * clients hardcoding every URL.</p>
 *
 * @author aryanpaudel
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {
 
    private static final String API_VERSION = "1.0.0";
    private static final String BASE_PATH   = "/api/v1";
 
    /**
     * Returns API metadata, HATEOAS navigation links, and usage examples in a single response.
     * {@link LinkedHashMap} is used throughout to preserve insertion order in the JSON output,
     * making the response easier to read in documentation and Postman.
     */
    @GET
    public Response discover() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("apiName",     "Smart Campus Sensor & Room Management API");
        response.put("version",     API_VERSION);
        response.put("status",      "RUNNING");
        response.put("description", "A JAX-RS RESTful API for managing campus rooms and IoT sensors.");
 
        Map<String, String> contact = new LinkedHashMap<>();
        contact.put("name",       "Smart Campus Infrastructure Team");
        contact.put("email",      "smartcampus-admin@university.ac.uk");
        contact.put("department", "School of Computer Science and Engineering");
        response.put("contact", contact);
 
        // HATEOAS links — clients follow these rather than constructing URLs themselves
        Map<String, String> links = new LinkedHashMap<>();
        links.put("self",    BASE_PATH);
        links.put("rooms",   BASE_PATH + "/rooms");
        links.put("sensors", BASE_PATH + "/sensors");
        response.put("resources", links);
 
        Map<String, String> examples = new LinkedHashMap<>();
        examples.put("listRooms",          "GET  " + BASE_PATH + "/rooms");
        examples.put("createRoom",         "POST " + BASE_PATH + "/rooms");
        examples.put("getRoom",            "GET  " + BASE_PATH + "/rooms/{roomId}");
        examples.put("deleteRoom",         "DEL  " + BASE_PATH + "/rooms/{roomId}");
        examples.put("listSensors",        "GET  " + BASE_PATH + "/sensors");
        examples.put("filterSensorByType", "GET  " + BASE_PATH + "/sensors?type=CO2");
        examples.put("createSensor",       "POST " + BASE_PATH + "/sensors");
        examples.put("getSensorReadings",  "GET  " + BASE_PATH + "/sensors/{sensorId}/readings");
        examples.put("postReading",        "POST " + BASE_PATH + "/sensors/{sensorId}/readings");
        response.put("usageExamples", examples);
 
        return Response.ok(response).build();
    }
}



