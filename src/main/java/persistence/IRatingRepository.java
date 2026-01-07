package persistence;

import Modules.Rating;
import Modules.RatingDto;

import java.util.List;

public interface IRatingRepository {

    Rating addRating(int mediaId,long userId, Rating rating);
    Rating confirmRating(long userId, long ratingId);
    Rating likeRating(long userId, long ratingId);
    boolean deleteRating(long userId, long ratingId);
    Rating updateRating(long userId, long ratingId, Rating rating);
    List<RatingDto> getRatingIfOwned(long userId);
}
