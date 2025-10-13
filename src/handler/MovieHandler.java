package handler;


import Modules.Media;
import Modules.Movie;

import service.IGameService;
import service.IMovieService;

public class MovieHandler implements IMediaHandler<Movie>{

    public IMovieService movieService;
    public MovieHandler(IMovieService movieService) {
        this.movieService = movieService;
    }


    @Override
    public Movie addMedia(Movie mediaType){

        if(!validateMedia(mediaType))return null;
        return movieService.addMovie(mediaType);
    }

    @Override
    public boolean validateMedia(Movie media) {
        if (media == null) return false;

        return media.getTitle() != null && !media.getTitle().isBlank();
    }

}
