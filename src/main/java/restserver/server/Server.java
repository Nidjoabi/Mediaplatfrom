package restserver.server;


import Controller.*;
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
        IRatingRepository ratingRepo = RatingSqlRepository.getInstance(unitOfWork);
        IRatingService ratingService = RatingService.getInstance(ratingRepo);
        RatingController ratingController = new RatingController(ratingService);
        IFavoritesRepository favoritesRepository = FavoritesSqlRepository.getInstance(unitOfWork);
        IFavoritesService favoritesService = FavoritesService.getInstance(favoritesRepository);
        ILeaderboardRepository leaderboardRepository = LeaderboardSqlRepository.getInstance(unitOfWork);
        ILeaderboardService leaderboardService = LeaderboardService.getInstance(leaderboardRepository);
        LeaderboardController leaderboardController = new LeaderboardController(leaderboardService);

        List<MediaStrategy> strategies = List.of(
                new MovieStrategy(movieService),
                new SeriesStrategy(seriesService),
                new GameStrategy(gameService)
        );
        UserController userController = new UserController(userService, favoritesService, ratingService);
        RatingHandler ratingHandler = new RatingHandler(ratingController);
        LeaderboardHandler leaderboardHandler = new LeaderboardHandler(leaderboardController);

        MediaController mediaController = new MediaController(mediaService, favoritesService, strategies );

        MediaHandler mediaHandler = new MediaHandler(mediaController);




        server.createContext("/", new EchoHandler());
        server.createContext("/api/users", new Userhandler(userController));
        server.createContext("/api/media", mediaHandler);
        server.createContext("/api/ratings", ratingHandler);
        server.createContext("/api/leaderboard", leaderboardHandler);


        server.setExecutor(null);
        server.start();
        System.out.println("Echo Server läuft auf http://localhost:8080");
    }
}
