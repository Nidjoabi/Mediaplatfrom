package handler;

import Controller.UserController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.http.Method;
import restserver.server.Request;
import restserver.server.Response;
import service.IUserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
//macht validierungen

public class Userhandler implements HttpHandler {


    private final IUserService userService;
    private final UserController userController;

    public Userhandler(IUserService userService) {
        this.userController = new UserController(userService);
        this.userService = userService;
    }



    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestBody = IOUtils.toString(httpExchange.getRequestBody(), StandardCharsets.UTF_8);
            Response response = null;

            if (httpExchange.getRequestMethod().equals(Method.POST.name())) {
                if (path.contains("register")) {
                    response = this.userController.registerUser(requestBody);
                } else if (path.contains("login")) {
                    response = this.userController.loginUser(requestBody);
                } else {
                    response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Endpoint not found\"}");
                }
            } else {
                response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Method not allowed\"}");
            }
            response.send(httpExchange);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
