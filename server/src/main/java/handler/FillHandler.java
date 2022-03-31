package handler;

import com.sun.net.httpserver.HttpExchange;
import logger.LoggerConfig;
import result.Result;
import service.FillService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FillHandler extends Handler {
    private final Logger logger = Logger.getLogger("FillHandler");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        LoggerConfig.configureLogger(logger, Level.FINER);

        try {
            if(hasCorrectRequestMethod(exchange, "post")) {
                String[] splitPath = getSplitPath(exchange);

                FillService service = new FillService();
                String username = splitPath[2];
                logger.fine("Username: " + username);

                Result result;
                if(splitPath.length > 3) {
                    int numGenerations = Integer.parseInt(splitPath[3]);
                    logger.fine("numGenerations: " + numGenerations);
                    result = service.fill(username, numGenerations);
                }
                else {
                    result = service.fill(username, 4);
                }

                sendResponse(exchange, result);

                success = result.isSuccess();
            }

            if (!success) {
                sendBadRequestResponse(exchange);
            }
        }
        catch(IOException ex) {
            handleIOException(ex, exchange);
        }
        exchange.getResponseBody().close();
    }
}
