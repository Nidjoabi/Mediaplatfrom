public class Series extends Media{

    private String director;
    private int seasons;
    private int episodes;


    public Series(String title, String description, String mediaType, int releaseYear,  String genre, Boolean isAgeRestricted, String director, int seasons, int episodes) {
        super(title, description, mediaType, releaseYear, genre, isAgeRestricted);
        this.director = director;
        this.seasons = seasons;
        this.episodes = episodes;
    }
}
