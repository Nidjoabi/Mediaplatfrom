package service;

import Modules.Rating;
import persistence.IRatingRepository;

public class RatingService implements IRatingService{

    private static RatingService instance = null;
    private final IRatingRepository ratingRepository;

    private RatingService(IRatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public static RatingService getInstance(IRatingRepository ratingRepository) {
        if (instance == null) {
            instance = new RatingService(ratingRepository);
        }
        return instance;
    }

    @Override
    public Rating addRating(Rating rating) {
        return ratingRepository.addRating(rating);
    }

}
