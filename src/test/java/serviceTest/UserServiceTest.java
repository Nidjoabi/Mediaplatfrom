package serviceTest;

import Modules.User;
import Modules.UserProfileDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.IUserRepository;
import service.UserService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private IUserRepository userRepo;
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();
        userRepo = mock(IUserRepository.class);
        userService = UserService.getInstance(userRepo);
    }

    private static void resetSingleton() throws Exception {
        Field f = UserService.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    void login_returnsUser_whenPasswordMatches() {
        User u = new User();
        u.setUsername("niko");
        u.setPassword("123");

        when(userRepo.getUserByUsername("niko")).thenReturn(u);

        User result = userService.login("niko", "123");

        assertNotNull(result);
        assertEquals("niko", result.getUsername());
    }

    @Test
    void login_returnsNull_whenUserNotFound_orPasswordWrong() {
        when(userRepo.getUserByUsername("x")).thenReturn(null);
        assertNull(userService.login("x", "pw"));

        User u = new User();
        u.setUsername("niko");
        u.setPassword("correct");
        when(userRepo.getUserByUsername("niko")).thenReturn(u);

        assertNull(userService.login("niko", "wrong"));
    }

    @Test
    void register_returnsTrue_andCallsCreateUser_whenUsernameFree_otherwiseFalse() {
        when(userRepo.getUserByUsername("niko")).thenReturn(null);

        boolean ok = userService.register("niko", "pw", "niko@mail.com");

        assertTrue(ok);
        verify(userRepo).createUser("niko", "pw", "niko@mail.com");

        reset(userRepo);
        when(userRepo.getUserByUsername("niko")).thenReturn(new User());

        boolean ok2 = userService.register("niko", "pw", "niko@mail.com");

        assertFalse(ok2);
        verify(userRepo, never()).createUser(anyString(), anyString(), anyString());
    }
}
