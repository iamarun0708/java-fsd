package com.cognizant.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExample {
    private static final Logger logger = LoggerFactory.getLogger(LoggingExample.class);

    public static void main(String[] args) {
        logger.info("Starting Logging Example application...");
        logger.error("This is an error message");
        logger.warn("This is a warning message");
        logger.info("Parameterized logging example: user={} logged in from IP={}", "JohnDoe", "127.0.0.1");
        logger.debug("Debugging details: cache initialized successfully.");
    }
}
