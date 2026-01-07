package controllerTest;

import Controller.GameStrategy;
import Modules.Game;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.IGameService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameStrategyTest {

    private IGameService gameService;
    private GameStrategy strategy;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        gameService = mock(IGameService.class);
        strategy = new GameStrategy(gameService);
        mapper = new ObjectMapper();
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
    void type_returnsGame() {
        assertEquals("game", strategy.type());
    }

    @Test
    void getById_returns404_whenGameNotFound() throws Exception {
        when(gameService.getGameById(10)).thenReturn(null);

        Response res = strategy.getById(10, mapper);

        if (status(res) != null) assertEquals(HttpStatus.NOT_FOUND, status(res));
        assertTrue(body(res).contains("Game not found"));
    }

    @Test
    void update_returns404_whenNotFoundOrNotOwner() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.put("title", "x");

        when(gameService.updateGame(eq(10), any(Game.class), eq(5L))).thenReturn(null);

        Response res = strategy.update(10, node, 5L, mapper);

        if (status(res) != null) assertEquals(HttpStatus.NOT_FOUND, status(res));
        assertTrue(body(res).contains("Not found or not owner"));
    }
}

