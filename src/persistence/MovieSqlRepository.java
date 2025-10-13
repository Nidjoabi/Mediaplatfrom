package persistence;

import Modules.Game;
import Modules.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieSqlRepository implements IMovieRepository{

    private static final MovieSqlRepository instance = new MovieSqlRepository();
    public static MovieSqlRepository getInstance(){return instance;}

    private final List<Movie> movies;
    public MovieSqlRepository() { movies = new ArrayList<>();}


    @Override
    public Movie addMovie(Movie movie) {
        if (movie == null) {
            System.out.println("Movie is null");
        }
        movies.add(movie);
        return movie;
    }
}
