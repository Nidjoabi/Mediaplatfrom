import Modules.User;
import Modules.Movie;
public class Main {
    public static void main(String[] args) {
        System.out.printf("Hello and welcome!");

    }

    User user1 = new User("nikola", "1234", "fakemail@gmail.com");
    Movie m1 = new Movie(
            "Inception",
            "A mind-bending sci-fi thriller",
            "Movie",
            2010,
            "Sci-Fi",
            true,
            "Christopher Nolan",
            148
    );

}