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
            // ✅ Session prüfen (für POST + DELETE)
            String query = httpExchange.getRequestURI().getQuery();
            String sessionId = getQueryParam(query, "sessionId");

            var session = SessionManager.getInstance().getValidSession(sessionId);
            if (session == null) {
                response = new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\":\"Not logged in\"}");
                response.send(httpExchange);
                return;
            }
            long userId = session.userId;


            String method = httpExchange.getRequestMethod();

            if (method.equals(Method.POST.name())) {
                String requestBody = IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8);
                response = mediaController.addMedia(requestBody, userId);

            } else if (method.equals(Method.DELETE.name())) {

                String path = httpExchange.getRequestURI().getPath(); // z.B. /api/media/1
                int mediaId = extractLastPathInt(path);

                if (mediaId <= 0) {
                    response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"mediaId is required\"}");
                } else {
                    response = mediaController.deleteMedia(mediaId, userId);
                }

            } else {
                response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Method not allowed\"}");
            }

        } catch (NumberFormatException e) {
            response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Invalid mediaId\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\":\"Error processing request\"}");
        }

        response.send(httpExchange);
    }

    private int extractLastPathInt(String path) {
        if (path == null || path.isBlank()) return -1;
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
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
