package handler;

import com.sun.net.httpserver.HttpExchange;
import result.Result;
import service.GetDataService;

import java.io.IOException;

public abstract class GetDataHandler extends Handler {
    protected GetDataService service;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if(hasCorrectRequestMethod(exchange, "get")) {
                String[] splitPath = getSplitPath(exchange);
                String authToken = getAuthToken(exchange);

                Result result;
                if(splitPath.length > 2) {
                    String ID = splitPath[2];
                    initializeSingleModelService();

                    result = service.getData(authToken, ID);
                }
                else {
                    initializeAllModelsService();
                    result = service.getData(authToken, null);
                }

                sendResponse(exchange, result);

                success = result.isSuccess();
            }

            if(!success) {
                sendBadRequestResponse(exchange);
            }
        }
        catch (IOException ex) {
            handleIOException(ex, exchange);
        }
        exchange.getResponseBody().close();
    }

    protected abstract void initializeAllModelsService();

    protected abstract void initializeSingleModelService();
}
