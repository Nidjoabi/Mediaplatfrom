package Modules;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game extends Media {

    @JsonAlias({"studio", "developerStudio"})
    private String developerStudio;

    public Game() { super();}

    public Game(String title, String description, String mediaType, int releaseYear, List<String> genres, int ageRestriction, String developerStudio) {
        super(title, description, mediaType, releaseYear, genres, ageRestriction);
        this.developerStudio = developerStudio;
    }

    public String getDeveloperStudio() {
        return developerStudio;
    }
    public void setDeveloperStudio(String developerStudio) {
        this.developerStudio = developerStudio;
    }



}
