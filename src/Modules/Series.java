package Modules;

public class Series extends Media {

    private String director;
    private int seasons;
    private int episodes;


    public Series(String title, String description, String mediaType, int releaseYear,  String genre, Boolean isAgeRestricted, String director, int seasons, int episodes) {
        super(title, description, mediaType, releaseYear, genre, isAgeRestricted);
        this.director = director;
        this.seasons = seasons;
        this.episodes = episodes;
    }

    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }
    public int getSeasons() {
        return seasons;
    }
    public void setSeasons(int seasons) {
        this.seasons = seasons;
    }
    public int getEpisodes() {
        return episodes;
    }
    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }
}
