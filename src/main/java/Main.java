
import com.sun.net.httpserver.HttpServer;
import handler.Userhandler;
import persistence.IUserRepository;
import persistence.UserSqlRepository;
import restserver.server.Server;
import service.IUserService;

import service.TokenService;
import service.UserService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {

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


        try{
            new Server().start();
            System.out.println("Server started");
        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }

}