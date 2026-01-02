package service;

import Modules.User;

public interface IUserService   {

    boolean register(String username, String password, String email);
    User login(String username, String password);
}
