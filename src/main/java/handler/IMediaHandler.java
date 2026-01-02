package handler;
import Modules.Media;

public interface IMediaHandler<T extends Media> {

    T addMedia(T mediaType, long userId);
    boolean validateMedia(T mediaType);

}
