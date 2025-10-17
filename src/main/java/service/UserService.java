package service;

import java.util.ArrayList;
import java.util.List;
import persistence.IUserRepository;
import Modules.User;

//regelt die Logik
public class UserService implements IUserService {


    private static UserService instance = null;

    private final IUserRepository userRepository;

    private final TokenService tokenService;

    private final List<User> loggedInUsers;

    private UserService(IUserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.loggedInUsers = new ArrayList<>();
    }

    public static UserService getInstance(IUserRepository userRepository, TokenService tokenService) {
        if (instance == null) {
            instance = new UserService(userRepository,  tokenService);
        }
        return instance;
    }

    @Override
    public boolean login(String username, String password) {
        if (checkPassword(username, password)) {
            User found = userRepository.getUserByUsername(username);

            String token = tokenService.createToken(username);
            found.setToken(token);
            loggedInUsers.add(found);
            return true;
        }

        return false;
    }


    public boolean register(String username, String password, String email) {

        User user = userRepository.createUser(username, password, email);
        return true;
    }

    public boolean checkPassword(String username, String password) {
        User found = userRepository.getUserByUsername(username);
        if (found != null) {
            return found.getPassword().equals(password);
        } else {
            return false;
        }
    }
}
