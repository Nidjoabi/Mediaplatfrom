package Controller;

import Modules.User;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import restserver.server.SessionManager;
import service.IFavoritesService;
import service.IRatingService;
import service.IUserService;

public class UserController extends Controller {
    private IUserService userService;
    private IFavoritesService favoritesService;
    private IRatingService ratingService;

    public UserController(IUserService userService, IFavoritesService favoritesService, IRatingService ratingService) {
        this.userService = userService;
        this.favoritesService = favoritesService;
        this.ratingService = ratingService;

    }

    public Response loginUser(String requestBody) {
        try {
            User user = this.getObjectMapper().readValue(requestBody, User.class);

            User found = userService.login(user.getUsername(), user.getPassword());
            if (found == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{\"message\":\"Invalid credentials\"}"
                );
            }


            String sessionId = SessionManager.getInstance()
                    .createSession(found.getUser_id()).id;

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{\"message\":\"Login successful\", \"username\":\"" + found.getUsername() + "\", \"token\":\"" + sessionId + "\"}"
            );

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

    public Response getProfile(long userId) {
        try {
            if (userId <= 0) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"message\":\"userId is required\"}"
                );
            }

            User profile = userService.getProfile(userId);

            if (profile == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{\"message\":\"User not found\"}"
                );
            }

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    getObjectMapper().writeValueAsString(profile)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{\"message\":\"Error processing request\"}"
            );
        }
    }

    public Response updateProfile(long userId, String requestBody) {
        try {
            if (userId <= 0) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"message\":\"Not logged in\"}"
                );
            }

            if (requestBody == null || requestBody.isBlank()) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"message\":\"Body is required\"}"
                );
            }

            User in = getObjectMapper().readValue(requestBody, User.class);

            if (in == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{\"message\":\"User is required\"}"
                );
            }

            User updated = userService.updateProfile(userId, in);

            if (updated == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{\"message\":\"User not found\"}"
                );
            }

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    getObjectMapper().writeValueAsString(updated)
            );

        } catch (IllegalArgumentException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{\"message\":\"" + e.getMessage() + "\"}"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{\"message\":\"Error processing request\"}"
            );
        }
    }

    public Response getFavorites(long userId) {
        try {
            if (userId <= 0) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"message\":\"Not logged in\"}"
                );
            }

            var favorites = favoritesService.getFavorites(userId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    getObjectMapper().writeValueAsString(favorites)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{\"message\":\"Error processing request\"}"
            );
        }

    }

    public Response getRatingIfOwned(long userId){
        try {
            if (userId <= 0) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"message\":\"Not logged in\"}"
                );
            }


            var ratings = ratingService.getRatingIfOwned(userId);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    getObjectMapper().writeValueAsString(ratings)
            );

        }catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{\"message\":\"Error processing request\"}"
            );
        }
    }


}


