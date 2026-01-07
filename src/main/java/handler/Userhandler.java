package handler;

import Controller.UserController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.http.Method;
import restserver.server.Response;
import restserver.server.SessionManager;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Userhandler implements HttpHandler {

    private final UserController userController;

    public Userhandler(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        Response response;

        try {
            String path = httpExchange.getRequestURI().getPath();
            String method = httpExchange.getRequestMethod();

            // ✅ 1) PUBLIC: register/login (kein Session-Check!)
            if (method.equals(Method.POST.name())) {
                String requestBody = IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8);

                if (path.contains("register")) {
                    response = userController.registerUser(requestBody);
                } else if (path.contains("login")) {
                    response = userController.loginUser(requestBody);
                } else {
                    response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Endpoint not found\"}");
                }

                response.send(httpExchange);
                return;
            }


            String query = httpExchange.getRequestURI().getQuery();
            String sessionId = getQueryParam(query, "sessionId");

            var session = SessionManager.getInstance().getValidSession(sessionId);
            if (session == null) {
                response = new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\":\"Not logged in\"}");
                response.send(httpExchange);
                return;
            }

            long loggedInUserId = session.userId;


            if (method.equals(Method.GET.name()) && path.matches("^/api/users/\\d+/profile/?$")) {
                long profileUserId = Long.parseLong(path.split("/")[3]);

                if (profileUserId != loggedInUserId) {
                    response = new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{\"message\":\"Not allowed\"}");
                } else {
                    response = userController.getProfile(profileUserId);
                }

                response.send(httpExchange);
                return;
            }

            if (method.equals(Method.GET.name()) && path.matches("^/api/users/\\d+/favorites/?$")) {
                long profileUserId = Long.parseLong(path.split("/")[3]);

                if (profileUserId != loggedInUserId) {
                    response = new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{\"message\":\"Not allowed\"}");
                } else {
                    response = userController.getFavorites(loggedInUserId);
                }

                response.send(httpExchange);
                return;
            }

            if (method.equals(Method.GET.name()) && path.matches("^/api/users/\\d+/ratings/?$")) {
                long profileUserId = Long.parseLong(path.split("/")[3]);

                if (profileUserId != loggedInUserId) {
                    response = new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{\"message\":\"Not allowed\"}");
                } else {
                    response = userController.getRatingIfOwned(loggedInUserId);
                }

                response.send(httpExchange);
                return;
            }




            if (method.equals(Method.PUT.name()) && path.matches("^/api/users/\\d+/profile/?$")) {
                long profileUserId = Long.parseLong(path.split("/")[3]);

                if (profileUserId != loggedInUserId) {
                    response = new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{\"message\":\"Not allowed\"}");
                    response.send(httpExchange);
                    return;
                }

                String requestBody = IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8);
                response = userController.updateProfile(profileUserId, requestBody);

                response.send(httpExchange);
                return;
            }


            response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Method/Endpoint not allowed\"}");
            response.send(httpExchange);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\":\"Error processing request\"}")
                        .send(httpExchange);
            } catch (Exception ignored) {}
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
