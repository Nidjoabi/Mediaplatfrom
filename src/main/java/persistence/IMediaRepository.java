package persistence;

import Modules.Game;

public interface IMediaRepository {

    int getMediaId(String title, String mediaType, int releaseYear);
    String getMediaTypeIfOwned(int mediaId, long userId);
}
