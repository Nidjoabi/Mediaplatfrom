package Modules;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rating {

    private long ratingId;
    private int mediaId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long creatorUserId;

    private int stars;
    private int likes = 0;

    @JsonAlias({"comment"})
    private String text;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String creatorUsername;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String mediaTitle;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean confirmed = false;

    public Rating() {}

    public Rating(int mediaId, int stars, int likes, String text, long creatorUserId) {
        this.mediaId = mediaId;
        this.stars = stars;
        this.likes = likes;
        this.text = text;
        this.creatorUserId = creatorUserId;
    }

    public long getRatingId() { return ratingId; }
    public void setRatingId(long ratingId) { this.ratingId = ratingId; }

    public int getMediaId() { return mediaId; }
    public void setMediaId(int mediaId) { this.mediaId = mediaId; }

    public long getCreatorUserId() { return creatorUserId; }
    public void setCreatorUserId(long creatorUserId) { this.creatorUserId = creatorUserId; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public String getText() { return text; }

    @JsonAlias({"comment"})
    public void setText(String text) { this.text = text; }

    public String getCreatorUsername() { return creatorUsername; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }

    public String getMediaTitle() { return mediaTitle; }
    public void setMediaTitle(String mediaTitle) { this.mediaTitle = mediaTitle; }

    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }

}
