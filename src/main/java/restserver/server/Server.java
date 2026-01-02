package restserver.server;


import Controller.MediaController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import database.UnitOfWork;
import handler.EchoHandler;
import handler.IMediaHandler;
import handler.MediaHandler;
import handler.Userhandler;
import persistence.*;
import service.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class Server {
    public void start() throws IOException {
        UnitOfWork unitOfWork = new UnitOfWork();
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        IUserRepository repository = UserSqlRepository.getInstance(unitOfWork);
        IUserService userService = UserService.getInstance(repository);
        ISeriesRepository seriesRepository = SeriesSqlRepository.getInstance(unitOfWork);
        ISeriesService seriesService = SeriesService.getInstance(seriesRepository);
        IMovieRepository movieRepository = MovieSqlRepository.getInstance(unitOfWork);
        IMovieService movieService = MovieService.getInstance(movieRepository);
        IGameRepository gameRepository = GameSqlRepository.getInstance(unitOfWork);
        IGameService gameService = GameService.getInstance(gameRepository);
        MediaController mediaController = new MediaController(seriesService, movieService, gameService);



        server.createContext("/", new EchoHandler());
        server.createContext("/api/users/register", new Userhandler(userService));
        server.createContext("/api/users/login", new Userhandler(userService));
        server.createContext("/api/media", new MediaHandler(mediaController));


        server.setExecutor(null);
        server.start();
        System.out.println("Echo Server läuft auf http://localhost:8080");
    }
}
