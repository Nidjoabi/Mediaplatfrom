package persistence;

import Modules.User;

public interface IUserRepository {
    void createUser(String username, String password, String email);
    User getUserByUsername(String username);

}
