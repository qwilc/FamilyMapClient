package com.example.familymap.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerConfig {
    private static final boolean blanketConfig = false; //TODO: Maybe I should just do this the right way XD
    private static final Level blanketLevel = Level.INFO;

    public static void configureLogger(Logger logger, Level level) {
        if(blanketConfig) {
            level = blanketLevel;
        }
        logger.setLevel(level);
        logger.setUseParentHandlers(false);
        java.util.logging.Handler handler = new ConsoleHandler();
        handler.setLevel(level);
        logger.addHandler(handler);
    }
}
