
import handler.Userhandler;
import persistence.IUserRepository;
import persistence.UserSqlRepository;
import service.IUserService;

import service.TokenService;
import service.UserService;

public class Main {
    public static void main(String[] args) {

        IUserRepository repository = UserSqlRepository.getInstance();
        TokenService tokenService = new TokenService();
        IUserService userService = UserService.getInstance(repository, tokenService);
        String username = "admin";
        String password = "password";
        String email = "email";

        Userhandler userHandler = new Userhandler(userService);
        boolean registered = userHandler.register(username, password, email);
        boolean loggedIn = userHandler.login(username, password);

        System.out.println("registered? " + registered);
        System.out.println("Login erfolgreich? " + loggedIn);



    }

}