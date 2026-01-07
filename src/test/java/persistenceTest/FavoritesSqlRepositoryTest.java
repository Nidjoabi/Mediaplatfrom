package persistenceTest;

import Modules.FavoriteMediaDto;
import database.UnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.FavoritesSqlRepository;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoritesSqlRepositoryTest {

    private UnitOfWork uow;
    private PreparedStatement ps;
    private ResultSet rs;

    private FavoritesSqlRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();

        uow = mock(UnitOfWork.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);

        when(uow.prepareStatement(anyString())).thenReturn(ps);

        repo = FavoritesSqlRepository.getInstance(uow);
    }

    private static void resetSingleton() throws Exception {
        Field f = FavoritesSqlRepository.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    void addFavorite_returnsTrue_andCommits_whenInsertChangedOneRow() throws Exception {
        when(ps.executeUpdate()).thenReturn(1);

        boolean created = repo.addFavorite(7L, 10);

        assertTrue(created);
        verify(ps).setLong(1, 7L);
        verify(ps).setInt(2, 10);
        verify(ps).executeUpdate();
        verify(uow).commitTransaction();
        verify(uow, never()).rollbackTransaction();
    }

    @Test
    void removeFavorite_returnsFalse_andCommits_whenNothingDeleted() throws Exception {
        when(ps.executeUpdate()).thenReturn(0);

        boolean removed = repo.removeFavorite(7L, 10);

        assertFalse(removed);
        verify(ps).setLong(1, 7L);
        verify(ps).setInt(2, 10);
        verify(ps).executeUpdate();
        verify(uow).commitTransaction();
        verify(uow, never()).rollbackTransaction();
    }

    @Test
    void getFavorites_mapsRows_toDtos() throws Exception {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);

        when(rs.getInt("media_id")).thenReturn(1);
        when(rs.getString("title")).thenReturn("Matrix");
        when(rs.getString("media_type")).thenReturn("movie");
        when(rs.getInt("release_year")).thenReturn(1999);

        List<FavoriteMediaDto> out = repo.getFavorites(5L);

        assertEquals(1, out.size());
        FavoriteMediaDto dto = out.get(0);


        assertNotNull(dto);

        verify(ps).setLong(1, 5L);
        verify(ps).executeQuery();
    }
}

