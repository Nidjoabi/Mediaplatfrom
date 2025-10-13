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
}
