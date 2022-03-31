package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import request.LoadRequest;
import result.Result;
import service.LoadService;

import java.io.IOException;
import java.util.logging.Logger;

public class LoadHandler extends Handler {
    private final Logger logger = Logger.getLogger("LoadHandler");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if(hasCorrectRequestMethod(exchange, "post")) {
                String requestData = getRequestData(exchange);

                logger.fine(requestData);

                Gson gson = new Gson();
                LoadRequest request = gson.fromJson(requestData, LoadRequest.class);

                LoadService service = new LoadService();
                Result result = service.load(request);

                sendResponse(exchange, result);

                logger.info(result.getMessage());

                success = result.isSuccess();
            }

            if (!success) {
                sendBadRequestResponse(exchange);
            }
        }
        catch (IOException ex) {
            handleIOException(ex, exchange);
        }
        logger.info("Rcode: " + exchange.getResponseCode());
        exchange.getResponseBody().close();
    }
}
