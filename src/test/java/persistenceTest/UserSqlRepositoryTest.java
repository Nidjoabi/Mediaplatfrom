package persistenceTest;

import Modules.User;
import Modules.UserProfileDto;
import database.UnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.UserSqlRepository;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSqlRepositoryTest {

    private UnitOfWork uow;
    private PreparedStatement ps;
    private ResultSet rs;

    private UserSqlRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();

        uow = mock(UnitOfWork.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);

        when(uow.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        repo = UserSqlRepository.getInstance(uow);
    }

    private static void resetSingleton() throws Exception {
        Field f = UserSqlRepository.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    void getUserByUsername_returnsNull_whenNoRow() throws Exception {
        when(rs.next()).thenReturn(false);

        User u = repo.getUserByUsername("niko");

        assertNull(u);
        verify(ps).setString(1, "niko");
        verify(ps).executeQuery();
    }

    @Test
    void createUser_executesInsert_andCommits() throws Exception {

        when(rs.next()).thenReturn(true);

        repo.createUser("niko", "pw", "niko@mail.com");

        verify(ps).setString(1, "niko");
        verify(ps).setString(2, "pw");
        verify(ps).setString(3, "niko@mail.com");

        verify(ps).executeQuery();
        verify(uow).commitTransaction();
        verify(uow, never()).rollbackTransaction();
    }

    @Test
    void getProfile_returnsDto_whenRowExists() throws Exception {
        when(rs.next()).thenReturn(true);

        when(rs.getLong("userId")).thenReturn(5L);
        when(rs.getString("userName")).thenReturn("niko");
        when(rs.getString("userEmail")).thenReturn("niko@mail.com");
        when(rs.getInt("totalRatings")).thenReturn(12);
        when(rs.getDouble("averageRating")).thenReturn(4.25);

        UserProfileDto dto = repo.getProfile(5L);

        assertNotNull(dto);
        assertEquals(5L, dto.getUserId());
        assertEquals("niko", dto.getUserName());
        assertEquals("niko@mail.com", dto.getUserEmail());
        assertEquals(12, dto.getTotalRatings());
        assertEquals(4.25, dto.getAverageRating(), 0.0001);

        verify(ps).setLong(1, 5L);
        verify(ps).executeQuery();
    }
}
