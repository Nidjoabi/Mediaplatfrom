package Controller;

import Modules.Movie;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.IMovieService;

public class MovieStrategy implements MediaStrategy {

    private final IMovieService movieService;

    public MovieStrategy(IMovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public String type() {
        return "movie";
    }

    @Override
    public Response add(JsonNode node, long userId, ObjectMapper mapper) throws Exception {
        Movie movie = mapper.treeToValue(node, Movie.class);
        Movie created = movieService.addMovie(movie, userId);
        return new Response(HttpStatus.CREATED, ContentType.JSON, mapper.writeValueAsString(created));
    }

    @Override
    public Response update(int mediaId, JsonNode node, long userId, ObjectMapper mapper) throws Exception {
        Movie movie = mapper.treeToValue(node, Movie.class);
        Movie updated = movieService.updateMovie(mediaId, movie, userId);

        if (updated == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"message\":\"Not found or not owner\"}");
        }

        return new Response(HttpStatus.OK, ContentType.JSON, mapper.writeValueAsString(updated));
    }

    @Override
    public Response getById(int mediaId, ObjectMapper mapper) throws Exception {
        Movie movie = movieService.getMovieById(mediaId);

        if (movie == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"message\":\"Movie not found\"}");
        }

        return new Response(HttpStatus.OK, ContentType.JSON, mapper.writeValueAsString(movie));
    }
}

