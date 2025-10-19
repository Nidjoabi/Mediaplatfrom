package Controller;

import Modules.User;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import persistence.UserSqlRepository;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.IUserService;

import java.util.List;

public class UserController extends Controller {
    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    public Response loginUser(String requestBody) {
        try {
            User user = this.getObjectMapper().readValue(requestBody, User.class);


            if (this.userService.login(user.getUsername(), user.getPassword())) {

                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{\"message\":\"Login successful\", \"username\":\"" + user.getUsername() + "\"}"
                );
            } else {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{\"message\":\"Invalid credentials\"}"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{\"message\":\"Error processing request\"}"
            );
        }
    }

    public Response registerUser(String requestBody) {
        try {
            User user = this.getObjectMapper().readValue(requestBody, User.class);


            if (this.userService.register(user.getUsername(), user.getPassword(), user.getEmail())) {
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{\"message\":\"User registered successfully\"}"
                );
            } else {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"message\":\"User already exists\"}"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{\"message\":\"Error processing request\"}"
            );
        }
    }
}


