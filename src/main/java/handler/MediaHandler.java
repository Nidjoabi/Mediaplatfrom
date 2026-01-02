package handler;

import Controller.MediaController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.http.Method;
import restserver.server.Response;
import restserver.server.SessionManager;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class MediaHandler implements HttpHandler {

    private final MediaController mediaController;

    public MediaHandler(MediaController mediaController) {
        this.mediaController = mediaController;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Response response;

        try {
            if (!httpExchange.getRequestMethod().equals(Method.POST.name())) {
                response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Method not allowed\"}");
                response.send(httpExchange);
                return;
            }

            // ✅ sessionId aus Query holen
            String query = httpExchange.getRequestURI().getQuery(); // z.B. "sessionId=abc"
            String sessionId = getQueryParam(query, "sessionId");

            var session = SessionManager.getInstance().getValidSession(sessionId);
            if (session == null) {
                response = new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\":\"Not logged in\"}");
                response.send(httpExchange);
                return;
            }

            long userId = session.userId; // ✅ Ersteller

            String requestBody = IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8);

            // ✅ jetzt musst du userId weitergeben (siehe Schritt 2)
            response = mediaController.addMedia(requestBody, userId );

        } catch (Exception e) {
            e.printStackTrace();
            response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\":\"Error processing request\"}");
        }

        response.send(httpExchange);
    }

    private String getQueryParam(String query, String key) {
        if (query == null || query.isBlank()) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
