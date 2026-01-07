package persistence;

import Modules.User;
import Modules.UserProfileDto;

import java.util.List;

public interface IUserRepository {
    void createUser(String username, String password, String email);
    User getUserByUsername(String username);
    UserProfileDto getProfile(long userId);
    User updateProfile(long userId, User user);
    //List<String> getUserFavorites(long userId);

}
