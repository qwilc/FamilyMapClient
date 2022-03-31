package server;

import com.sun.net.httpserver.HttpServer;
import handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Server {
    private static final int MAX_WAITING_CONNECTIONS = 12;
    private HttpServer server;
    private static final Logger logger = Logger.getLogger("Server");

    private void run(String portNumber) {
        logger.info("Initializing HTTP Server");

        try {
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(portNumber)),
                    MAX_WAITING_CONNECTIONS);
        }
        catch(IOException ex) {
            ex.printStackTrace();
            return;
        }

        server.setExecutor(null);

        logger.info("Creating contexts");

        server.createContext("/user/register", new RegisterHandler());
        server.createContext("/user/login", new LoginHandler());
        server.createContext("/clear", new ClearHandler());
        server.createContext("/fill/", new FillHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/person/", new PersonHandler());
        server.createContext("/person", new PersonHandler());
        server.createContext("/event/", new EventHandler());
        server.createContext("/event", new EventHandler());
        server.createContext("/", new FileHandler());

        logger.info("Starting server");

        server.start();

        logger.info("Server started");
    }

    public static void main(String[] args) {
        String portNumber = args[0];
        logger.fine(portNumber);
        new Server().run(portNumber);
    }
}
