package service;

import java.util.ArrayList;
import java.util.List;
import persistence.IUserRepository;
import Modules.User;

//regelt die Logik
public class UserService implements IUserService {


    private static UserService instance = null;

    private final IUserRepository userRepository;


    private UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static UserService getInstance(IUserRepository userRepository) {
        if (instance == null) {
            instance = new UserService(userRepository);
        }
        return instance;
    }

    @Override
    public User login(String username, String password) {
        User found = userRepository.getUserByUsername(username);
        if (found == null) return null;

        if (password.equals(found.getPassword())) {
            return found;
        }
        return null;
    }


    public boolean register(String username, String password, String email) {
        User found = userRepository.getUserByUsername(username);
        if (found != null) {
            return false;
        }else {
            userRepository.createUser(username, password, email);
            return true;
        }
    }
}
