package Controller;

import Modules.Movie;
import Modules.Series;
import Modules.Game;
import com.fasterxml.jackson.databind.JsonNode;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.IGameService;
import service.IMediaService;
import service.IMovieService;
import service.ISeriesService;

public class MediaController extends Controller{

    private final IMovieService movieService;
    private final ISeriesService seriesService;
    private final IGameService gameService;
    private final IMediaService mediaService;


    public MediaController(IMovieService movieService,
                           ISeriesService seriesService,
                           IGameService gameService,
                           IMediaService mediaService) {
        this.movieService = movieService;
        this.seriesService = seriesService;
        this.gameService = gameService;
        this.mediaService = mediaService;

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

    public Response deleteMedia(int mediaId, long userId) {
        try {
            if (mediaId <= 0) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"mediaId is required\"}");
            }

            boolean deleted = mediaService.deleteMedia(mediaId, userId);

            if (deleted) {
                return new Response(HttpStatus.OK, ContentType.JSON,
                        "{\"message\":\"Deleted\"}");
            } else {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                        "{\"message\":\"Not found or not owner\"}");
            }

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }
    }

}
