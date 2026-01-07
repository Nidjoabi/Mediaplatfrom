package persistence;

import Modules.MediaDto;

import java.util.List;

public interface IMediaRepository {
    List<MediaDto> searchMedia(String title, String genre, String mediaType,
                               Integer releaseYear, Integer ageRestriction, String sortBy);
    String getMediaTypeIfOwned(int mediaId, long userId);
    String getMediaType(int mediaId);

}
