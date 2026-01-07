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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaHandler implements HttpHandler {

    private final MediaController mediaController;

    public MediaHandler(MediaController mediaController) {
        this.mediaController = mediaController;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Response response;

        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();


            String query = httpExchange.getRequestURI().getQuery();
            String sessionId = getQueryParam(query, "sessionId");

            var session = SessionManager.getInstance().getValidSession(sessionId);
            if (session == null) {
                response = new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\":\"Not logged in\"}");
                response.send(httpExchange);
                return;
            }
            long userId = session.userId;


            if (method.equals(Method.POST.name()) && path.matches("^/api/media/\\d+/favorite/?$")) {
                int mediaId = extractMediaId(path);
                if (mediaId <= 0) {
                    response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Invalid mediaId\"}");
                } else {
                    response = mediaController.addFavorite(userId, mediaId);
                }
                response.send(httpExchange);
                return;
            }


            if (method.equals(Method.DELETE.name()) && path.matches("^/api/media/\\d+/favorite/?$")) {
                int mediaId = extractMediaId(path);
                if (mediaId <= 0) {
                    response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Invalid mediaId\"}");
                } else {
                    response = mediaController.removeFavorite(userId, mediaId);
                }
                response.send(httpExchange);
                return;
            }


            if (method.equals(Method.POST.name()) && path.matches("^/api/media/?$")) {
                String requestBody = IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8);
                response = mediaController.addMedia(requestBody, userId);
                response.send(httpExchange);
                return;
            }

            if (method.equals(Method.GET.name()) && path.matches("^/api/media/?$")) {
                String title = getQueryParam(query, "title");
                String genre = getQueryParam(query, "genre");
                String mediaType = getQueryParam(query, "mediaType");
                String sortBy = getQueryParam(query, "sortBy");

                Integer releaseYear = parseIntOrNull(getQueryParam(query, "releaseYear"));
                Integer ageRestriction = parseIntOrNull(getQueryParam(query, "ageRestriction"));
                response = mediaController.searchMedia(title, genre, mediaType, releaseYear, ageRestriction, sortBy);
                response.send(httpExchange);
                return;
            }

            if (!path.matches("^/api/media/\\d+/?$")) {
                response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Endpoint not found\"}");
                response.send(httpExchange);
                return;
            }

            int mediaId = extractMediaId(path);
            if (mediaId <= 0) {
                response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Invalid mediaId\"}");
                response.send(httpExchange);
                return;
            }


            if (method.equals(Method.DELETE.name())) {
                response = mediaController.deleteMedia(mediaId, userId);
                response.send(httpExchange);
                return;
            }


            if (method.equals(Method.PUT.name())) {
                String requestBody = IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8);
                response = mediaController.updateMedia(mediaId, requestBody, userId);
                response.send(httpExchange);
                return;
            }


            if (method.equals(Method.GET.name())) {
                response = mediaController.getMediaById(mediaId);
                response.send(httpExchange);
                return;
            }

            response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Method not allowed\"}");
            response.send(httpExchange);

        } catch (Exception e) {
            e.printStackTrace();
            response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\":\"Error processing request\"}");
            response.send(httpExchange);
        }
    }


    private int extractMediaId(String path) {
        Pattern p = Pattern.compile("^/api/media/(\\d+)(?:/.*)?/?$");
        Matcher m = p.matcher(path);
        if (!m.matches()) return -1;
        return Integer.parseInt(m.group(1));
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

    private Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        return Integer.parseInt(s.trim());
    }

}
