package Modules;

public class RatingDto {
    private int stars;
    private String comment;
    private int likes;
    private String mediaTitle;
    private String mediaType;

    public RatingDto(int stars, String comment, int likes, String mediaTitle, String mediaType) {
        this.stars = stars;
        this.comment = comment;
        this.likes = likes;
        this.mediaTitle = mediaTitle;
        this.mediaType = mediaType;
    }
}
