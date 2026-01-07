package serviceTest;

import Modules.FavoriteMediaDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.IFavoritesRepository;
import service.FavoritesService;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoritesServiceTest {

    private IFavoritesRepository repo;
    private FavoritesService service;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();
        repo = mock(IFavoritesRepository.class);
        service = FavoritesService.getInstance(repo);
    }

    private static void resetSingleton() throws Exception {
        Field f = FavoritesService.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    void addFavorite_throwsIllegalArgumentException_whenIdsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> service.addFavorite(1L, 0));
        assertThrows(IllegalArgumentException.class, () -> service.addFavorite(0L, 1));
    }

    @Test
    void removeFavorite_delegatesToRepository_whenIdsValid() {
        when(repo.removeFavorite(7L, 10)).thenReturn(true);

        boolean out = service.removeFavorite(7L, 10);

        assertTrue(out);
        verify(repo).removeFavorite(7L, 10);
    }

    @Test
    void getFavorites_delegatesToRepository_whenUserIdValid() {
        List<FavoriteMediaDto> list = List.of();
        when(repo.getFavorites(5L)).thenReturn(list);

        List<FavoriteMediaDto> out = service.getFavorites(5L);

        assertSame(list, out);
        verify(repo).getFavorites(5L);
    }
}

