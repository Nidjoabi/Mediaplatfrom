package Modules;

public class LeaderBoardDto {
    private String username;
    private int ratingCount;
    private int rank;

    public LeaderBoardDto() {}

    public LeaderBoardDto(String username, int ratingCount, int rank) {
        this.username = username;
        this.ratingCount = ratingCount;
        this.rank = rank;
    }

    public String getUsername() {
        return username;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public int getRank() {
        return rank;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
