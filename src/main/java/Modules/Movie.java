package Modules;

import java.util.List;

public class Movie extends Media {

    private String director;
    private int movieLength;

    public Movie() { super();}

    public Movie(String title, String description, String mediaType, int releaseYear, List<String> genres, int ageRestriction, String director, int movieLength){
        super(title, description, mediaType, releaseYear, genres, ageRestriction);
        this.director = director;
        this.movieLength = movieLength;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getMovieLength() {
        return movieLength;
    }

    public void setMovieLength(int movieLength) {
        this.movieLength = movieLength;
    }
}
