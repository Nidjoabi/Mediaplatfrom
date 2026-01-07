package service;

import Modules.MediaDto;
import persistence.IGameRepository;
import persistence.IMediaRepository;

import java.util.List;
import java.util.Locale;

public class MediaService implements IMediaService {

    private static MediaService instance = null;
    private final IMediaRepository mediaRepository;
    private IGameService gameService;
    private ISeriesService seriesService;
    private IMovieService movieService;

    public MediaService(IMediaRepository mediaRepository,
                        IGameService gameService,
                        ISeriesService seriesService,
                        IMovieService movieService) {
        this.mediaRepository = mediaRepository;
        this.gameService = gameService;
        this.seriesService = seriesService;
        this.movieService = movieService;

    }

    public static MediaService getInstance(IMediaRepository mediaRepository,
                                           IGameService gameService,
                                           ISeriesService seriesService,
                                           IMovieService movieService) {
        if (instance == null) {
            instance = new MediaService(mediaRepository, gameService, seriesService, movieService);
        }
        return instance;
    }

    @Override
    public boolean deleteMedia(int mediaId, long userId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");

        String type = mediaRepository.getMediaTypeIfOwned(mediaId, userId);

        return switch(type.toLowerCase()){
            case "movie" -> movieService.deleteMovie(mediaId, userId);
            case "series" -> seriesService.deleteSeries(mediaId, userId);
            case "game" -> gameService.deleteGame(mediaId, userId);
            default -> false;
        };
    }

    @Override
    public String getMediaType(int mediaId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");
        return mediaRepository.getMediaType(mediaId);
    }

    @Override
    public List<MediaDto> searchMedia(String title, String genre, String mediaType,
                                      Integer releaseYear, Integer ageRestriction, String sortBy){
        return mediaRepository.searchMedia(title, genre, mediaType, releaseYear, ageRestriction, sortBy);

    }
}
