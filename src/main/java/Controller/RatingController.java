package Controller;

import Modules.Rating;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import persistence.IRatingRepository;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.IRatingService;

public class RatingController extends Controller{

    private final IRatingService ratingService;

    public RatingController(IRatingService ratingService) {
        this.ratingService = ratingService;
    }

    public Response addRating(int mediaId, long userId, String requestBody) {
        try {
            Rating rating = getObjectMapper().readValue(requestBody, Rating.class);
            Rating created = ratingService.addRating(mediaId, userId, rating);
            if (created == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Could not create rating\"}");
            }
            return new Response(HttpStatus.CREATED, ContentType.JSON,
                    getObjectMapper().writeValueAsString(created));
        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\":\"Error\"}");
        }
    }

    public Response confirmRating(long userId, long ratingId) {
        try {
            Rating confirmed = ratingService.confirmRating(userId, ratingId);
            if (confirmed == null) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                        "{\"message\":\"Not found / not owner / already confirmed\"}");
            }
            return new Response(HttpStatus.OK, ContentType.JSON,
                    getObjectMapper().writeValueAsString(confirmed));

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }
    }

    public Response likeRating(long userId, long ratingId) {
        try {
            Rating updated = ratingService.likeRating(userId, ratingId);

            if (updated == null) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                        "{\"message\":\"Not found or already liked\"}");
            }

            return new Response(HttpStatus.OK, ContentType.JSON,
                    getObjectMapper().writeValueAsString(updated));

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }
    }

    public Response updateRating(long userId, long ratingId, String requestBody) {
        try {

            if (ratingId <= 0) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"message\":\"ratingId is required\"}"
                );
            }

            if (requestBody == null) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"message\":\"rating body is required\"}"
                );
            }

            Rating rating = getObjectMapper().readValue(requestBody, Rating.class);
            Rating updated = ratingService.updateRating(userId, ratingId, rating);

            if (updated == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{\"message\":\"Not found or not owner\"}"
                );
            }

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    getObjectMapper().writeValueAsString(updated)
            );

        } catch (IllegalArgumentException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{\"message\":\"" + e.getMessage() + "\"}"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{\"message\":\"Error processing request\"}"
            );
        }
    }




}
