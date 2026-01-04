package service;

import Modules.Movie;

public interface IMovieService {
    Movie addMovie(Movie movie, long userId);
    boolean deleteMovie(int mediaId, long userId);

}
