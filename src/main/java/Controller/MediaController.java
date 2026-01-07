package Controller;

import Modules.Movie;
import Modules.Series;
import Modules.Game;
import com.fasterxml.jackson.databind.JsonNode;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaController extends Controller{

    private final IMediaService mediaService;
    private final IFavoritesService favoritesService;


    private final Map<String, MediaStrategy> strategies;


    public MediaController(IMediaService mediaService,
                           IFavoritesService favoritesService,
                           List<MediaStrategy> strategyList) {

        this.mediaService = mediaService;
        this.favoritesService = favoritesService;

        this.strategies = new HashMap<>();
        for (MediaStrategy s : strategyList) {
            this.strategies.put(s.type().toLowerCase(), s);
        }
    }


    public Response addMedia(String requestBody, long userId) {
        try {
            JsonNode node = getObjectMapper().readTree(requestBody);
            String type = node.path("mediaType").asText(null);

            if (type == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"mediaType is required\"}");
            }

            MediaStrategy strategy = strategies.get(type.toLowerCase());
            if (strategy == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"Unknown mediaType\"}");
            }

            return strategy.add(node, userId, getObjectMapper());

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }
    }


    public Response deleteMedia(int mediaId, long userId) {
        try {
            if (mediaId <= 0) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"mediaId is required\"}");
            }

            boolean deleted = mediaService.deleteMedia(mediaId, userId);

            if (deleted) {
                return new Response(HttpStatus.OK, ContentType.JSON,
                        "{\"message\":\"Deleted\"}");
            } else {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                        "{\"message\":\"Not found or not owner\"}");
            }

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }
    }

    public Response updateMedia(int mediaId, String requestBody, long userId) {
        try {
            if (mediaId <= 0) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"mediaId is required\"}");
            }

            JsonNode node = getObjectMapper().readTree(requestBody);
            String type = node.path("mediaType").asText(null);

            if (type == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"mediaType is required\"}");
            }

            MediaStrategy strategy = strategies.get(type.toLowerCase());
            if (strategy == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"Unknown mediaType\"}");
            }

            return strategy.update(mediaId, node, userId, getObjectMapper());

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }
    }


    public Response getMediaById(int mediaId) {
        try {
            if (mediaId <= 0) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"mediaId is required\"}");
            }

            String type = mediaService.getMediaType(mediaId);
            if (type == null) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                        "{\"message\":\"Media not found\"}");
            }

            MediaStrategy strategy = strategies.get(type.toLowerCase());
            if (strategy == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"message\":\"Unknown mediaType\"}");
            }

            return strategy.getById(mediaId, getObjectMapper());

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }
    }

    public Response searchMedia(String title, String genre, String mediaType,
                                Integer releaseYear, Integer ageRestriction, String sortBy) {
        try {

            var result = mediaService.searchMedia(title, genre, mediaType, releaseYear, ageRestriction, sortBy);

            return new Response(HttpStatus.OK, ContentType.JSON,
                    getObjectMapper().writeValueAsString(result));

        } catch (NumberFormatException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"message\":\"releaseYear/ageRestriction must be a number\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }
    }

    private Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        return Integer.parseInt(s.trim());
    }



    public Response addFavorite(long userId, int mediaId) {
        try {
            boolean created = favoritesService.addFavorite(userId, mediaId);

            return new Response(HttpStatus.OK, ContentType.JSON,
                    created ? "{\"message\":\"favorited\"}" : "{\"message\":\"already favorited\"}");
        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }
    }

    public Response removeFavorite(long userId, int mediaId) {
        try {
            boolean removed = favoritesService.removeFavorite(userId, mediaId);
            return new Response(removed ? HttpStatus.OK : HttpStatus.NOT_FOUND, ContentType.JSON,
                    removed ? "{\"message\":\"unfavorited\"}" : "{\"message\":\"not favorited\"}");
        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }
    }



}
