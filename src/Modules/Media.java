package Modules;

public class Media {

    private String title;
    private String description;
    private String mediaType;
    private int releaseYear;
    private String genre;
    private Boolean isAgeRestricted = false;

    public Media(String title, String description, String mediaType, int releaseYear,  String genre, Boolean isAgeRestricted) {
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.isAgeRestricted = isAgeRestricted;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    public Boolean getIsAgeRestricted() {
        return isAgeRestricted;
    }
    public void setIsAgeRestricted(Boolean isAgeRestricted) {
        this.isAgeRestricted = isAgeRestricted;
    }


}
