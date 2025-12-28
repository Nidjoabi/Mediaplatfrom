package handler;

import Modules.Rating;
import service.IRatingService;

public class RatingHandler implements IRatingHandler{

    public IRatingService ratingService;
    public RatingHandler(IRatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Override
    public Rating addRating(Rating rating) {
        return ratingService.addRating(rating);
    }


}
