package handler;
import Modules.Game;
import Modules.Media;

public interface IMediaHandler<T extends Media> {

    T addMedia(T mediaType);
    boolean validateMedia(T mediaType);

}
