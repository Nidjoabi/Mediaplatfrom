package handler;

import Controller.RatingController;
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

public class RatingHandler implements HttpHandler {

    private final RatingController ratingController;

    public RatingHandler(RatingController ratingController) {
        this.ratingController = ratingController;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Response response;

        try {
            String path = httpExchange.getRequestURI().getPath();
            String method = httpExchange.getRequestMethod();


            String query = httpExchange.getRequestURI().getQuery();
            String sessionId = getQueryParam(query, "sessionId");

            var session = SessionManager.getInstance().getValidSession(sessionId);
            if (session == null) {
                response = new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\":\"Not logged in\"}");
                response.send(httpExchange);
                return;
            }
            long userId = session.userId;
            if (method.equals(Method.POST.name()) && path.matches("^/api/ratings/media/\\d+/?$")) {
                String[] parts = path.split("/");
                int mediaId = Integer.parseInt(parts[4]);

                System.out.println("RATE mediaId=" + mediaId); // Debug

                String body = IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8);
                response = ratingController.addRating(mediaId, userId, body);
                response.send(httpExchange);
                return;
            }else if (method.equals(Method.POST.name()) && path.matches("^/api/ratings/\\d+/confirm/?$")) {

                long ratingId = extractRatingId(path);

                response = ratingController.confirmRating(userId, ratingId);

            } else if (method.equals(Method.POST.name()) && path.matches("^/api/ratings/\\d+/like/?$")) {
                long ratingId = extractRatingId(path);
                response = ratingController.likeRating(userId, ratingId);
            } else if (method.equals((Method.PUT.name()))){
                long ratingId = extractRatingId(path);
                String body = IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8);
                response = ratingController.updateRating(userId,ratingId, body);

            }else {
                response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"Endpoint not found\"}");
            }

        } catch (NumberFormatException e) {
            response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"message\":\"Invalid ratingId\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }

        response.send(httpExchange);
    }

    private long extractRatingId(String path) {
        String[] parts = path.split("/");
        return Long.parseLong(parts[3]);
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
