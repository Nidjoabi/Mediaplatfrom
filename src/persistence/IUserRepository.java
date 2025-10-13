package persistence;

import Modules.User;

public interface IUserRepository {
    User createUser(String username, String password, String email);

    User getUserByUsername(String username);

}
