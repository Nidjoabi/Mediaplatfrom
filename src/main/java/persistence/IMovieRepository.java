package persistence;

import Modules.Movie;

public interface IMovieRepository {
    Movie addMovie(Movie movie, long userId);
    boolean deleteMovie(int mediaId, long userId);
}
