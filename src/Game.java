public class Game extends Media{

    private String developerStudio;



    public Game(String title, String description, String mediaType, int releaseYear,  String genre, Boolean isAgeRestricted) {
        super(title, description, mediaType, releaseYear, genre, isAgeRestricted);
        this.developerStudio = developerStudio;
    }


}
