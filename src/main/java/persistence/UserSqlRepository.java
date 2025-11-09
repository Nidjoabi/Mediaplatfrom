package persistence;

import Modules.User;
import database.UnitOfWork;

import java.util.ArrayList;
import java.util.List;

public class UserSqlRepository implements IUserRepository {
    private UnitOfWork unitOfWork;
    private static UserSqlRepository instance = null;
    public static UserSqlRepository getInstance(UnitOfWork unitOfWork){
        if(instance == null){
            instance = new UserSqlRepository(unitOfWork);
        }
        return instance;}

    private final List<User> userList; //simulates DB

    private UserSqlRepository(UnitOfWork unitOfWork) {
        userList = new ArrayList<>();
        this.unitOfWork = unitOfWork;
    }

    @Override
    public User createUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        userList.add(user); //change to DB code
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        //demo: search for user
        return userList.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public List<User> getUserList() {
        return userList;
    }
}
