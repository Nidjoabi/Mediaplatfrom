package Controller;

import Modules.Movie;
import Modules.Series;
import Modules.Game;
import Modules.User;
import com.fasterxml.jackson.databind.JsonNode;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.IGameService;
import service.IMovieService;
import service.ISeriesService;

import java.util.Locale;

public class MediaController extends Controller{

    private ISeriesService seriesService;
    private IMovieService movieService;
    private IGameService gameService;

    public MediaController(ISeriesService seriesService, IMovieService movieService, IGameService gameService) {
        this.seriesService = seriesService;
        this.movieService = movieService;
        this.gameService = gameService;
    }

    public Response addMedia(String requestBody, long userId) {
        try {
                JsonNode node = this.getObjectMapper().readTree(requestBody);
                String type = node.path("mediaType").asText(null);

            if (type == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"mediaType is required\"}");
            }

            return switch (type.toLowerCase()) {
                    case "movie" -> {
                        Movie movie = this.getObjectMapper().treeToValue(node, Movie.class);
                        Movie created = movieService.addMovie(movie, userId);
                        yield new Response(HttpStatus.CREATED, ContentType.JSON,
                                getObjectMapper().writeValueAsString(created));
                    }
                    case "series" -> {
                        Series series = this.getObjectMapper().treeToValue(node, Series.class);
                        Series created = seriesService.addSeries(series, userId);
                        yield new Response(HttpStatus.CREATED, ContentType.JSON,
                                getObjectMapper().writeValueAsString(created));
                    }
                    case "game" -> {
                        Game game = this.getObjectMapper().treeToValue(node, Game.class);
                        Game created = gameService.addGame(game, userId);
                        yield new Response(HttpStatus.CREATED, ContentType.JSON,
                                getObjectMapper().writeValueAsString(created));
                    }
                default -> new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"Unknown mediaType\"}");
                };
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{\"message\":\"Error processing request\"}"
            );
        }
    };
}
