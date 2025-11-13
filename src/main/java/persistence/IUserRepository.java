package persistence;

import Modules.User;

import java.util.List;

public interface IUserRepository {
    User createUser(String username, String password, String email);

    User getUserByUsername(String username);

}
