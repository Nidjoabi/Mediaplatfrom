package Modules;

public class Rating<T extends Media> {

    T Media;
    private int Stars;
    private int Likes;
    User Creator;

    public Rating(T Media, int Stars, int Likes) {
        this.Media = Media;
        this.Stars = Stars;
        this.Likes = Likes;
    }

    public T getMedia() {
        return Media;
    }


}
