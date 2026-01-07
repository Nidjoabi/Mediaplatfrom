package service;

import Modules.Game;
import Modules.Movie;

public interface IMovieService {
    Movie addMovie(Movie movie, long userId);
    boolean deleteMovie(int mediaId, long userId);
    Movie updateMovie(int mediaId, Movie movie, long userId);
    Movie getMovieById(int mediaId);

}
