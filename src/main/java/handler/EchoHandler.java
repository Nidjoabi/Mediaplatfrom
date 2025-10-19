package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import restserver.http.ContentType;
import restserver.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EchoHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        httpExchange.getResponseHeaders().add("Cache-Control", "nocache");
        httpExchange.getResponseHeaders().add("Content-Type", ContentType.PLAIN_TEXT.type);
        System.out.println("Incoming: " + httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI());
        try (httpExchange) {
            byte[] responseBody = ("Echo-" + IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(HttpStatus.OK.code, responseBody.length);
            httpExchange.getResponseBody().write(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        };
    }
}