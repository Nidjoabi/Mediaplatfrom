package controllerTest;

import Controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.IFavoritesService;
import service.IRatingService;
import service.IUserService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private IUserService userService;
    private IFavoritesService favoritesService;
    private IRatingService ratingService;

    private UserController controller;

    @BeforeEach
    void setUp() {
        userService = mock(IUserService.class);
        favoritesService = mock(IFavoritesService.class);
        ratingService = mock(IRatingService.class);

        controller = new UserController(userService, favoritesService, ratingService);

    }

    private static Object readField(Object obj, String... names) {
        for (String n : names) {
            Class<?> c = obj.getClass();
            while (c != null) {
                try {
                    Field f = c.getDeclaredField(n);
                    f.setAccessible(true);
                    return f.get(obj);
                } catch (NoSuchFieldException e) {
                    c = c.getSuperclass();
                } catch (Exception e) {
                    break;
                }
            }
        }
        return null;
    }

    private static HttpStatus status(Response r) {
        Object st = readField(r, "status", "httpStatus", "statusCode");
        return (st instanceof HttpStatus hs) ? hs : null;
    }

    private static String body(Response r) {
        Object b = readField(r, "body", "payload", "content", "data");
        return b == null ? r.toString() : String.valueOf(b);
    }


    @Test
    void loginUser_returns401_whenInvalidCredentials() {
        when(userService.login("niko", "pw")).thenReturn(null);

        Response res = controller.loginUser("{\"username\":\"niko\",\"password\":\"pw\"}");

        if (status(res) != null) assertEquals(HttpStatus.UNAUTHORIZED, status(res));
        assertTrue(body(res).contains("Invalid credentials"));
    }


    @Test
    void registerUser_returns400_whenUserAlreadyExists() {
        when(userService.register("niko", "pw", "niko@mail.com")).thenReturn(false);

        Response res = controller.registerUser("{\"username\":\"niko\",\"password\":\"pw\",\"email\":\"niko@mail.com\"}");

        if (status(res) != null) assertEquals(HttpStatus.BAD_REQUEST, status(res));
        assertTrue(body(res).contains("User already exists"));
    }


    @Test
    void getProfile_returns400_whenUserIdInvalid() {
        Response res = controller.getProfile(0);

        if (status(res) != null) assertEquals(HttpStatus.BAD_REQUEST, status(res));
        assertTrue(body(res).contains("userId is required"));
    }
}
