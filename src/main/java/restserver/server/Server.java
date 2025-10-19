package restserver.server;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import handler.EchoHandler;
import handler.Userhandler;
import persistence.IUserRepository;
import persistence.UserSqlRepository;
import service.IUserService;
import service.TokenService;
import service.UserService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class Server {
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        IUserRepository repository = UserSqlRepository.getInstance();
        TokenService tokenService = new TokenService();
        IUserService userService = UserService.getInstance(repository, tokenService);

        server.createContext("/", new EchoHandler());
        server.createContext("/api/users/register", new Userhandler(userService));
        server.createContext("/api/users/login", new Userhandler(userService));

        server.setExecutor(null);
        server.start();
        System.out.println("Echo Server läuft auf http://localhost:8080");
    }
}
