package handler;

import Modules.User;
import service.IUserService;
//macht validierungen

public class Userhandler {


    private final IUserService userService;

    public Userhandler(IUserService userService) {
        this.userService = userService;
    }

    public boolean validate(String username, String password) {

        if (password == null || username == null) {
            return false;
        }
        return password.isBlank();
    }


    public boolean login(String username, String password) {
        if (validate(username, password)) return false;
        return userService.login(username, password);
    }

    public boolean register(String username, String password, String email) {
        if (validate(username, password)) return false;
        return userService.register(username, password, email);
    }
}
