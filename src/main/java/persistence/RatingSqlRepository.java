package persistence;

import Modules.Rating;
import Modules.Series;

import java.util.ArrayList;
import java.util.List;

public class RatingSqlRepository implements IRatingRepository{

    private static final RatingSqlRepository instance = new RatingSqlRepository();
    public static RatingSqlRepository getInstance(){return instance;}

    private final List<Rating> ratinglist;
    public RatingSqlRepository() { ratinglist = new ArrayList<>();}

    public Rating addRating(Rating rating) {
        ratinglist.add(rating);
        return rating;
    }
}
