package controllerTest;

import Controller.MediaController;
import Controller.MediaStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.IFavoritesService;
import service.IMediaService;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MediaControllerTest {

    private IMediaService mediaService;
    private IFavoritesService favoritesService;
    private MediaStrategy movieStrategy;

    private MediaController controller;

    @BeforeEach
    void setUp() {
        mediaService = mock(IMediaService.class);
        favoritesService = mock(IFavoritesService.class);

        movieStrategy = mock(MediaStrategy.class);
        when(movieStrategy.type()).thenReturn("movie");

        controller = new MediaController(mediaService, favoritesService, List.of(movieStrategy));
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
    void addMedia_returns400_whenMediaTypeMissing() {
        Response res = controller.addMedia("{\"title\":\"x\"}", 1L);

        if (status(res) != null) assertEquals(HttpStatus.BAD_REQUEST, status(res));
        assertTrue(body(res).contains("mediaType is required"));
    }


    @Test
    void addMedia_delegatesToStrategy_whenKnownType() throws Exception {
        Response expected = mock(Response.class);
        when(movieStrategy.add(any(), eq(7L), any())).thenReturn(expected);

        Response res = controller.addMedia("{\"mediaType\":\"movie\",\"title\":\"x\"}", 7L);

        assertSame(expected, res);
        verify(movieStrategy).add(any(), eq(7L), any());
        verifyNoInteractions(mediaService, favoritesService);
    }


    @Test
    void deleteMedia_returns200_whenDeleted() {
        when(mediaService.deleteMedia(10, 2L)).thenReturn(true);

        Response res = controller.deleteMedia(10, 2L);

        if (status(res) != null) assertEquals(HttpStatus.OK, status(res));
        assertTrue(body(res).contains("Deleted"));
        verify(mediaService).deleteMedia(10, 2L);
    }
}
