package Modules;

public class Game extends Media {

    private String developerStudio;



    public Game(String title, String description, String mediaType, int releaseYear,  String genre, Boolean isAgeRestricted, String developerStudio) {
        super(title, description, mediaType, releaseYear, genre, isAgeRestricted);
        this.developerStudio = developerStudio;
    }

    public String getDeveloperStudio() {
        return developerStudio;
    }


}
