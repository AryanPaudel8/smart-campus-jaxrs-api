/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bootstrap class for the Smart Campus REST API.
 *
 * <p>Uses an embedded Grizzly server so the application runs as a standalone JAR
 * without needing a full servlet container. The base URI includes /api/v1 so every
 * resource is automatically versioned under that prefix.</p>
 *
 * @author aryanpaudel
 */
public class SmartCampusApplication {

    private static final Logger LOGGER = Logger.getLogger(SmartCampusApplication.class.getName());

    // /api/v1/ is the versioned root - all @Path annotations are relative to this
    private static final String BASE_URI = "http://0.0.0.0:8080/api/v1/";

    public static void main(String[] args) throws IOException {
        final ResourceConfig config = new ResourceConfig()
                .packages("com.smartcampus")
                .register(JacksonFeature.class);

        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI), config);

        LOGGER.info("Smart Campus API started at: http://localhost:8080/api/v1/");
        LOGGER.info("Press CTRL+C to stop the server.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down Smart Campus API...");
            server.shutdown();
        }));

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.SEVERE, "Server interrupted", e);
        }
    }
}
 
