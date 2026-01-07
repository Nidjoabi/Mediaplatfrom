package service;

import Modules.Rating;
import Modules.RatingDto;
import persistence.IRatingRepository;

import java.util.List;

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
    public Rating addRating(int mediaId, long userId, Rating rating) {
        if (rating == null) throw new IllegalArgumentException("rating is null");
        if (rating.getStars() < 1 || rating.getStars() > 5) throw new IllegalArgumentException("stars must be 1..5");
        if (rating.getLikes() < 0) rating.setLikes(0);

        return ratingRepository.addRating(mediaId, userId, rating);
    }

    @Override
    public Rating confirmRating(long userId, long ratingId) {
        if (ratingId <=0) throw new IllegalArgumentException("ratingId missing!");
        return ratingRepository.confirmRating(userId, ratingId);
    }

    @Override
    public Rating likeRating(long userId, long ratingId) {
        if (ratingId <=0) throw new IllegalArgumentException("ratingId missing!");
        return ratingRepository.likeRating(userId, ratingId);
    }

    @Override
    public Rating updateRating(long userId, long ratingId, Rating rating) {
        if (ratingId <=0) throw new IllegalArgumentException("ratingId missing!");
        return ratingRepository.updateRating(userId, ratingId, rating);
    }

    @Override
    public List<RatingDto> getRatingIfOwned(long userId){
        if (userId <=0) throw new IllegalArgumentException("userId missing!");
        return ratingRepository.getRatingIfOwned(userId);
    }


}
