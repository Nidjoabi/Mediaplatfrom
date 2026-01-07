package handler;

import Controller.LeaderboardController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.http.Method;
import restserver.server.Response;
import restserver.server.SessionManager;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class LeaderboardHandler implements HttpHandler {

    private LeaderboardController leaderboardController;

    public LeaderboardHandler(LeaderboardController leaderboardController){
        this.leaderboardController = leaderboardController;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
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

            if (method.equals(Method.GET.name()) && path.matches("^/api/leaderboard/?$")) {
                response = leaderboardController.getLeaderboard();
                response.send(httpExchange);
                return;
            }

            response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Endpoint not found\"}");
            response.send(httpExchange);

        } catch (Exception e) {
            e.printStackTrace();
            response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\":\"Error processing request\"}");
            response.send(httpExchange);
        }
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
