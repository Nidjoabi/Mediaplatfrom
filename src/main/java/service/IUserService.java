package service;

import Modules.User;
import Modules.UserProfileDto;

public interface IUserService   {

    boolean register(String username, String password, String email);
    User login(String username, String password);
    UserProfileDto getProfile(long userId);
    User updateProfile(long userId, User user);
}
