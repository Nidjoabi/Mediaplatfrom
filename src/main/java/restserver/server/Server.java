package restserver.server;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class Server {
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new EchoHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Echo Server läuft auf http://localhost:8080");
    }

    static class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String requestLine = exchange.getRequestMethod() + " " + exchange.getRequestURI() + " " + exchange.getProtocol();


            System.out.println("\n--- Eingehende Anfrage ---");
            System.out.println(requestLine);
            for (Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
                System.out.println(header.getKey() + ": " + String.join(", ", header.getValue()));
            }


            String response = "ECHO: " + requestLine;
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

}
