package Controller;

import Modules.User;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import persistence.UserSqlRepository;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;

import java.util.List;

public class UserController extends Controller {

    UserSqlRepository userSqlRepository;

    public UserController() {
        this.userSqlRepository = UserSqlRepository.getInstance();
    }

    public Response registerUser(String requestBody){
        try {
            User user = this.getObjectMapper().readValue(requestBody, User.class);
            this.userSqlRepository.createUser(user.getUsername(), user.getPassword(), user.getEmail());

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{message:\"User registered successfully\"}"
            );
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{message:\"User already exists\"}"
        );
    }

    public Response loginUser(String requestBody) {
        try {
            User user = this.getObjectMapper().readValue(requestBody, User.class);

            User dbUser = this.userSqlRepository.getUserByUsername(user.getUsername());

            if (dbUser == null ) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{\"message\":\"Account doesn't exist\"}"
                );
            }


            if (dbUser.getPassword().equals(user.getPassword())) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{\"message\":\"Login successful\", \"username\":\"" + dbUser.getUsername() + "\"}"
                );
            } else {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{\"message\":\"Invalid credentials\"}"
                );
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{\"message\":\"Error processing request\"}"
            );
        }
    }


}


