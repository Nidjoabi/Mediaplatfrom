package service;

import Modules.Game;
import Modules.Movie;
import persistence.IGameRepository;
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
    public Movie addMovie(Movie movie) {
        if (movie == null) {
            System.out.println("Movie is null");
        }
        return movieRepository.addMovie(movie);
    }
}
