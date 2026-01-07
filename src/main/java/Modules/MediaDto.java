package Modules;

import java.util.List;

public class MediaDto {

    private String title;
    private String description;
    private String mediaType;
    private int releaseYear;
    private List<String> genres;
    private int ageRestriction;
    private int mediaId;
    private int ratingCount;
    private double score;

    // details (je nach type)
    private String studio;        // game
    private String director;      // movie/series
    private Integer movieLength;  // movie
    private Integer seasons;      // series
    private Integer episodes;     // series

    public MediaDto() {}



    public String getMediaType() {
        return mediaType;
    }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public void setGenres(List<String> genres) { this.genres = genres; }
    public void setAgeRestriction(int ageRestriction) { this.ageRestriction = ageRestriction; }
    public void setMediaId(int mediaId) { this.mediaId = mediaId; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
    public void setScore(double score) { this.score = score; }

    // Setter für Details
    public void setStudio(String studio) { this.studio = studio; }
    public void setDirector(String director) { this.director = director; }
    public void setMovieLength(Integer movieLength) { this.movieLength = movieLength; }
    public void setSeasons(Integer seasons) { this.seasons = seasons; }
    public void setEpisodes(Integer episodes) { this.episodes = episodes; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getReleaseYear() { return releaseYear; }
    public List<String> getGenres() { return genres; }
    public int getAgeRestriction() { return ageRestriction; }
    public int getMediaId() { return mediaId; }
    public int getRatingCount() { return ratingCount; }
    public double getScore() { return score; }

    public String getStudio() { return studio; }
    public String getDirector() { return director; }
    public Integer getMovieLength() { return movieLength; }
    public Integer getSeasons() { return seasons; }
    public Integer getEpisodes() { return episodes; }

}
