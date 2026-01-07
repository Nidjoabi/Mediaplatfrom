package Controller;

import Modules.Series;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.ISeriesService;

public class SeriesStrategy implements MediaStrategy {

    private final ISeriesService seriesService;

    public SeriesStrategy(ISeriesService seriesService) {
        this.seriesService = seriesService;
    }

    @Override
    public String type() {
        return "series";
    }

    @Override
    public Response add(JsonNode node, long userId, ObjectMapper mapper) throws Exception {
        Series series = mapper.treeToValue(node, Series.class);
        Series created = seriesService.addSeries(series, userId);
        return new Response(HttpStatus.CREATED, ContentType.JSON, mapper.writeValueAsString(created));
    }

    @Override
    public Response update(int mediaId, JsonNode node, long userId, ObjectMapper mapper) throws Exception {
        Series series = mapper.treeToValue(node, Series.class);
        Series updated = seriesService.updateSeries(mediaId, series, userId);

        if (updated == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"message\":\"Not found or not owner\"}");
        }

        return new Response(HttpStatus.OK, ContentType.JSON, mapper.writeValueAsString(updated));
    }

    @Override
    public Response getById(int mediaId, ObjectMapper mapper) throws Exception {
        Series series = seriesService.getSeriesById(mediaId);

        if (series == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"message\":\"Series not found\"}");
        }

        return new Response(HttpStatus.OK, ContentType.JSON, mapper.writeValueAsString(series));
    }
}
