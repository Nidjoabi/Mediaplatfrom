package serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.IMediaRepository;
import service.IGameService;
import service.IMovieService;
import service.ISeriesService;
import service.MediaService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MediaServiceTest {

    private IMediaRepository mediaRepo;
    private IGameService gameService;
    private ISeriesService seriesService;
    private IMovieService movieService;

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        mediaRepo = mock(IMediaRepository.class);
        gameService = mock(IGameService.class);
        seriesService = mock(ISeriesService.class);
        movieService = mock(IMovieService.class);

        mediaService = new MediaService(mediaRepo, gameService, seriesService, movieService);
    }

    @Test
    void deleteMedia_throwsIllegalArgumentException_whenMediaIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> mediaService.deleteMedia(0, 1L));
        assertThrows(IllegalArgumentException.class, () -> mediaService.deleteMedia(-1, 1L));
    }

    @Test
    void deleteMedia_routesToMovieService_whenTypeMovie() {
        when(mediaRepo.getMediaTypeIfOwned(10, 7L)).thenReturn("movie");
        when(movieService.deleteMovie(10, 7L)).thenReturn(true);

        boolean deleted = mediaService.deleteMedia(10, 7L);

        assertTrue(deleted);
        verify(movieService).deleteMovie(10, 7L);
        verifyNoInteractions(seriesService, gameService);
    }

    @Test
    void getMediaType_delegatesToRepository() {
        when(mediaRepo.getMediaType(5)).thenReturn("series");

        assertEquals("series", mediaService.getMediaType(5));
        verify(mediaRepo).getMediaType(5);
    }
}
