package Modules;

public class Rating {

    private Media media;
    private int stars;
    private int likes;
    private User creator;
    private String text;

    public Rating(Media media, int stars, int likes, String text, User creator ) {
        this.media = media;
        this.stars = stars;
        this.likes = likes;
        this.text = text;
        this.creator = creator;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }





}
