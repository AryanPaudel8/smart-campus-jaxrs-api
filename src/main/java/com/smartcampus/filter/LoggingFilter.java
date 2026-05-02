/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Cross-cutting filter that logs every inbound request and outbound response.
 *
 * <p>Implementing both {@link ContainerRequestFilter} and {@link ContainerResponseFilter}
 * in a single class centralises observability logic in one place. The alternative-
 * adding Logger.info() calls inside every resource method- violates the
 * Single Responsibility Principle and risks missing coverage as new endpoints are added.
 * Filters execute for every matched route automatically, guaranteeing 100% coverage.</p>
 *
 * @author aryanpaudel
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    /** Logs the HTTP method and URI before the request reaches any resource method. */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getRequestUri().getPath();

        // Ignore browser noise — favicon requests are not API calls
        if (path.contains("favicon.ico")) return;

        LOGGER.info(String.format(
                "[REQUEST]  Method: %-7s | URI: %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri()
        ));
    }

    /** Logs the HTTP method, URI, and final status code after the response is built. */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        String path = requestContext.getUriInfo().getRequestUri().getPath();

        // Ignore browser noise — favicon requests are not API calls
        if (path.contains("favicon.ico")) return;

        LOGGER.info(String.format(
                "[RESPONSE] Method: %-7s | URI: %s | Status: %d %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri(),
                responseContext.getStatus(),
                responseContext.getStatusInfo().getReasonPhrase()
        ));
    }
}
