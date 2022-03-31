package logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerConfig {
    public static void configureLogger(Logger logger, Level level) {
        logger.setLevel(level);
        logger.setUseParentHandlers(false);
        java.util.logging.Handler handler = new ConsoleHandler();
        handler.setLevel(level);
        logger.addHandler(handler);
    }
}
