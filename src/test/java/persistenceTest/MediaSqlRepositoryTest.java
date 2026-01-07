package persistenceTest;

import Modules.MediaDto;
import database.UnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.MediaSqlRepository;

import java.lang.reflect.Field;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MediaSqlRepositoryTest {

    private UnitOfWork uow;
    private PreparedStatement ps;
    private ResultSet rs;

    private MediaSqlRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();

        uow = mock(UnitOfWork.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);

        when(uow.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        repo = MediaSqlRepository.getInstance(uow);
    }

    private static void resetSingleton() throws Exception {
        Field f = MediaSqlRepository.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }


    @Test
    void getMediaType_throwsIllegalArgumentException_whenMediaIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> repo.getMediaType(0));
        assertThrows(IllegalArgumentException.class, () -> repo.getMediaType(-1));
    }


    @Test
    void getMediaTypeIfOwned_returnsNull_whenNoRow() throws Exception {
        when(rs.next()).thenReturn(false);

        String type = repo.getMediaTypeIfOwned(10, 99L);

        assertNull(type);
        verify(ps).setInt(1, 10);
        verify(ps).setLong(2, 99L);
        verify(ps).executeQuery();
    }


    @Test
    void searchMedia_withGenre_bindsArray_andMapsMovieRow() throws Exception {

        Connection conn = mock(Connection.class);
        when(ps.getConnection()).thenReturn(conn);

        Array genreSqlArray = mock(Array.class);
        when(conn.createArrayOf(eq("text"), any())).thenReturn(genreSqlArray);


        when(rs.next()).thenReturn(true, false);


        when(rs.getInt("media_id")).thenReturn(1);
        when(rs.getString("title")).thenReturn("The Matrix");
        when(rs.getString("description")).thenReturn("desc");
        when(rs.getString("media_type")).thenReturn("movie");
        when(rs.getInt("release_year")).thenReturn(1999);
        when(rs.getInt("age_restriction")).thenReturn(16);


        Array genresFromDb = mock(Array.class);
        when(genresFromDb.getArray()).thenReturn(new String[]{"sci-fi", "thriller"});
        when(rs.getArray("genres")).thenReturn(genresFromDb);


        when(rs.getDouble("avg_score")).thenReturn(4.2);
        when(rs.getInt("rating_count")).thenReturn(10);


        when(rs.getString("movie_director")).thenReturn("Wachowski");
        when(rs.getInt("movie_length")).thenReturn(136);
        when(rs.wasNull()).thenReturn(false);

        List<MediaDto> out = repo.searchMedia(
                "matrix",
                "sci-fi, thriller",
                "movie",
                1999,
                16,
                "score"
        );


        verify(ps).setString(1, "%matrix%");
        verify(ps).setString(2, "movie");
        verify(ps).setInt(3, 1999);
        verify(ps).setInt(4, 16);
        verify(ps).setArray(5, genreSqlArray);

        assertEquals(1, out.size());
        MediaDto dto = out.get(0);

        assertEquals(1, dto.getMediaId());
        assertEquals("The Matrix", dto.getTitle());
        assertEquals("movie", dto.getMediaType());
        assertEquals(List.of("sci-fi", "thriller"), dto.getGenres());

        assertEquals(4.2, dto.getScore(), 0.0001);
        assertEquals(10, dto.getRatingCount());

        assertEquals("Wachowski", dto.getDirector());
        assertEquals(136, dto.getMovieLength());
    }
}

