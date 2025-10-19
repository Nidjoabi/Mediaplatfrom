
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

        try {
            new Server().start();
            System.out.println("Server started");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}