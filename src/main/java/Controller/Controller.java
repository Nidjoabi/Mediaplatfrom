package Controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Controller {
    private ObjectMapper objectMapper;

    public Controller() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
