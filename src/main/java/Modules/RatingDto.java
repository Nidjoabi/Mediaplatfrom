package Modules;

public class RatingDto {
    private int stars;
    private String comment;
    private int likes;
    private String mediaTitle;
    private String mediaType;

    public RatingDto() {}

    public RatingDto(int stars, String comment, int likes, String mediaTitle, String mediaType) {
        this.stars = stars;
        this.comment = comment;
        this.likes = likes;
        this.mediaTitle = mediaTitle;
        this.mediaType = mediaType;
    }


    public int getStars() { return stars; }
    public String getComment() { return comment; }
    public int getLikes() { return likes; }
    public String getMediaTitle() { return mediaTitle; }
    public String getMediaType() { return mediaType; }

    public void setStars(int stars) { this.stars = stars; }
    public void setComment(String comment) { this.comment = comment; }
    public void setLikes(int likes) { this.likes = likes; }
    public void setMediaTitle(String mediaTitle) { this.mediaTitle = mediaTitle; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
}
