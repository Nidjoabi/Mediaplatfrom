package restserver.server;


import Controller.MediaController;
import Modules.Game;
import Modules.Movie;
import Modules.Series;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import database.UnitOfWork;
import handler.*;
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
        IMediaRepository mediaRepository = MediaSqlRepository.getInstance(unitOfWork);
        IMediaService mediaService = MediaService.getInstance(mediaRepository, gameService, seriesService, movieService);


        MediaController mediaController = new MediaController(movieService, seriesService, gameService, mediaService );

        MediaHandler mediaHandler = new MediaHandler(mediaController);



        server.createContext("/", new EchoHandler());
        server.createContext("/api/users/register", new Userhandler(userService));
        server.createContext("/api/users/login", new Userhandler(userService));
        server.createContext("/api/media", mediaHandler);


        server.setExecutor(null);
        server.start();
        System.out.println("Echo Server läuft auf http://localhost:8080");
    }
}
