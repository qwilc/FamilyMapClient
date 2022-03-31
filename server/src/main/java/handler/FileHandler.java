package handler;

import com.sun.net.httpserver.HttpExchange;
import logger.LoggerConfig;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileHandler extends Handler {
    private static final Logger logger = Logger.getLogger("FileHandler");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        LoggerConfig.configureLogger(logger, Level.FINE);
        logger.fine("Handling file request");
        try {
            if(hasCorrectRequestMethod(exchange, "get")) {
                String urlPath = exchange.getRequestURI().toString();

                if(urlPath == null || urlPath.equals("/")) {
                    urlPath = "/index.html";
                }
                logger.fine(urlPath);

                String filePath = "web" + urlPath;
                logger.fine(filePath);

                File file = new File(filePath);

                if(file.exists()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    WriteToResponseBody(exchange, file);
                    success = true;
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                    file = new File("web/HTML/404.html");
                    WriteToResponseBody(exchange, file);
                }
            }
            else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
                exchange.getResponseBody().close();
            }

            if(!success && exchange.getResponseCode() == -1) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                exchange.getResponseBody().close();
            }
        }
        catch(IOException ex) {
            handleIOException(ex, exchange);
        }
    }

    private void WriteToResponseBody(HttpExchange exchange, File file) throws IOException {
        OutputStream responseBody = exchange.getResponseBody();
        Files.copy(file.toPath(), responseBody);
        responseBody.close();
    }
}
