package Modules;

public class FavoriteMediaDto {
    private int mediaId;
    private String title;
    private String mediaType;
    private int releaseYear;

    public FavoriteMediaDto(int mediaId, String title, String mediaType, int releaseYear) {
        this.mediaId = mediaId;
        this.title = title;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getMediaId() {
        return mediaId;
    }
    public String getTitle() {
        return title;
    }
    public String getMediaType() {
        return mediaType;
    }
    public int getReleaseYear() {
        return releaseYear;
    }
}

