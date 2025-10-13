package Modules;

public class Movie extends Media {

    private String director;
    private int movieLength;

    public Movie(String title, String description, String mediaType, int releaseYear,  String genre, Boolean isAgeRestricted, String director, int movieLength){
        super(title, description, mediaType, releaseYear, genre, isAgeRestricted);
        this.director = director;
        this.movieLength = movieLength;
    }
}
