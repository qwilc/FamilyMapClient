package handler;

import com.sun.net.httpserver.HttpExchange;
import result.Result;
import service.ClearService;

import java.io.IOException;

public class ClearHandler extends Handler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if(hasCorrectRequestMethod(exchange, "post")) {
                ClearService service = new ClearService();
                Result result = service.clear();

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
