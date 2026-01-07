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
}

