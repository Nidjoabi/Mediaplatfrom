package service;

import Modules.MediaDto;

import java.util.List;

public interface IMediaService {
    boolean deleteMedia(int mediaId, long userId);
    String getMediaType(int mediaId);
    List<MediaDto> searchMedia(String title, String genre, String mediaType,
                               Integer releaseYear, Integer ageRestriction, String sortBy);
}
