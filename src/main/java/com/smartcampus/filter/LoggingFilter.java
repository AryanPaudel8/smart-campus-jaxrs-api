/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.filter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.vrs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;
/**
 *
 * @author aryanpaudel
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
 
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
 
    /**
     * Executed before the request is dispatched to a resource method.
     * Logs the HTTP method and full request URI.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info(String.format(
                "[REQUEST]  Method: %-7s | URI: %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri()
        ));
    }
 
    /**
     * Executed after the resource method returns a response.
     * Logs the HTTP status code of the outgoing response.
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOGGER.info(String.format(
                "[RESPONSE] Method: %-7s | URI: %s | Status: %d %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri(),
                responseContext.getStatus(),
                responseContext.getStatusInfo().getReasonPhrase()
        ));
    }
