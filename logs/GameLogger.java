package logs;

import java.io.File;
import java.util.logging.*;

public class GameLogger {
    private static final Logger logger = Logger.getLogger("GameLogger");
    private static final String LOG_FORMAT = "[%1$tF %1$tT] [%4$-7s] %5$s %n";
    private static final String logsDirectory = "laba_1/game_logs";
    static {
        try {
            File dir = new File(logsDirectory);
            if (!dir.exists()) dir.mkdirs();
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }
            FileHandler fileHandler = new FileHandler(logsDirectory+ "/game_log.log", true);
            logger.addHandler(fileHandler);
            fileHandler.setFormatter(new SimpleFormatter());
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format(LOG_FORMAT,
                            record.getMillis(),
                            record.getSourceClassName(),
                            record.getSourceMethodName(),
                            record.getLevel().getLocalizedName(),
                            record.getMessage());
                }
            });
            logger.setLevel(Level.ALL);
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);
        } catch (Exception e) {
            System.err.println("Не удалось настроить логгер: " + e.getMessage());
        }
    }

    public static void logInfo(String message) {
        logger.log(Level.INFO, message);
    }

    public static void logWarning(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void logError(String message) {
        logger.log(Level.SEVERE, message);
    }
}
