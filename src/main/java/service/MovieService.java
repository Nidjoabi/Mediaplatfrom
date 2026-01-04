package service;

import Modules.Movie;
import persistence.IMovieRepository;

public class MovieService implements IMovieService{
    private static MovieService instance = null;
    private final IMovieRepository movieRepository;

    public MovieService(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public static MovieService getInstance(IMovieRepository movieRepository){
        if (instance == null) {
            instance = new MovieService(movieRepository);
        }
        return instance;
    }
    @Override
    public Movie addMovie(Movie movie,  long userId) {
        return movieRepository.addMovie(movie, userId);
    }

    @Override
    public boolean deleteMovie(int mediaId, long userId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");
        return movieRepository.deleteMovie(mediaId, userId);
    }
}
