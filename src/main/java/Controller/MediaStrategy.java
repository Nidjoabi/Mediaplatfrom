package Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import restserver.server.Response;

public interface MediaStrategy {

    String type();
    Response add(JsonNode node, long userId, ObjectMapper mapper) throws Exception;

    Response update(int mediaId, JsonNode node, long userId, ObjectMapper mapper) throws Exception;

    Response getById(int mediaId, ObjectMapper mapper) throws Exception;
}
