package serviceTest;

import Modules.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.IGameRepository;
import service.GameService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private IGameRepository repo;
    private GameService service;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();
        repo = mock(IGameRepository.class);
        service = GameService.getInstance(repo);
    }

    private static void resetSingleton() throws Exception {
        Field f = GameService.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    void deleteGame_throwsIllegalArgumentException_whenMediaIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteGame(0, 1L));
        assertThrows(IllegalArgumentException.class, () -> service.deleteGame(-1, 1L));
    }

    @Test
    void updateGame_throwsIllegalArgumentException_whenGameNull() {
        assertThrows(IllegalArgumentException.class, () -> service.updateGame(1, null, 1L));
    }

    @Test
    void getGameById_delegatesToRepository() {
        Game g = new Game();
        when(repo.getGameById(5)).thenReturn(g);

        Game out = service.getGameById(5);

        assertSame(g, out);
        verify(repo).getGameById(5);
    }
}

