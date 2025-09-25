package persistence;

import Modules.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserSqlRepository implements IUserRepository {

    private static final UserSqlRepository instance = new UserSqlRepository();
    public static UserSqlRepository getInstance(){return instance;}

    private final List<User> users; //simulates DB

    private UserSqlRepository() {
        users = new ArrayList<>();
    }

    @Override
    public User createUser(String username, String password, String email) {
        User user = new User(username, password, email);
        users.add(user); //change to DB code
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        //demo: search for user
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
