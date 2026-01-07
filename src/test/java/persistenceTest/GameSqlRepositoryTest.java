package persistenceTest;

import Modules.Game;
import database.UnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.GameSqlRepository;

import java.lang.reflect.Field;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameSqlRepositoryTest {

    private UnitOfWork uow;

    private PreparedStatement ps1;
    private PreparedStatement ps2;
    private ResultSet rs;

    private GameSqlRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();

        uow = mock(UnitOfWork.class);
        ps1 = mock(PreparedStatement.class);
        ps2 = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);

        repo = GameSqlRepository.getInstance(uow);
    }

    private static void resetSingleton() throws Exception {
        Field f = GameSqlRepository.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    void addGame_commits_andReturnsGame_whenInsertReturningHasRow() throws Exception {
        // addGame macht 2 prepareStatement Aufrufe (media + details)
        when(uow.prepareStatement(anyString())).thenReturn(ps1, ps2);

        Connection conn = mock(Connection.class);
        when(ps1.getConnection()).thenReturn(conn);
        Array sqlArr = mock(Array.class);
        when(conn.createArrayOf(eq("text"), any())).thenReturn(sqlArr);

        when(ps1.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getLong("media_id")).thenReturn(123L);

        Game g = new Game();
        g.setTitle("t");
        g.setDescription("d");
        g.setReleaseYear(2020);
        g.setGenres(List.of("action"));
        g.setAgeRestriction(16);
        g.setDeveloperStudio("StudioX");

        Game out = repo.addGame(g, 9L);

        assertSame(g, out);

        verify(ps1).setString(1, "t");
        verify(ps1).setString(2, "d");
        verify(ps1).setString(3, "game");
        verify(ps1).setInt(4, 2020);
        verify(ps1).setArray(5, sqlArr);
        verify(ps1).setInt(6, 16);
        verify(ps1).setLong(7, 9L);

        verify(ps2).setLong(1, 123L);
        verify(ps2).setString(2, "StudioX");
        verify(ps2).executeUpdate();

        verify(uow).commitTransaction();
    }

    @Test
    void deleteGame_returnsTrue_andCommits_whenRowReturned() throws Exception {
        when(uow.prepareStatement(anyString())).thenReturn(ps1);
        when(ps1.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        boolean deleted = repo.deleteGame(10, 7L);

        assertTrue(deleted);
        verify(ps1).setLong(1, 10);
        verify(ps1).setLong(2, 7L);
        verify(uow).commitTransaction();
    }

    @Test
    void getGameById_mapsGenresAndStudio_whenRowExists() throws Exception {
        when(uow.prepareStatement(anyString())).thenReturn(ps1);
        when(ps1.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getString("title")).thenReturn("t");
        when(rs.getString("description")).thenReturn("d");
        when(rs.getString("media_type")).thenReturn("game");
        when(rs.getInt("release_year")).thenReturn(2020);
        when(rs.getInt("age_restriction")).thenReturn(16);

        Array genresArr = mock(Array.class);
        when(genresArr.getArray()).thenReturn(new String[]{"action", "rpg"});
        when(rs.getArray("genres")).thenReturn(genresArr);

        when(rs.getString("studio")).thenReturn("StudioX");

        Game g = repo.getGameById(5);

        assertNotNull(g);
        assertEquals("t", g.getTitle());
        assertEquals(List.of("action", "rpg"), g.getGenres());
        assertEquals("StudioX", g.getDeveloperStudio());

        verify(ps1).setInt(1, 5);
    }
}
