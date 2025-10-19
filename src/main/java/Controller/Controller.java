package Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Controller {
    private ObjectMapper objectMapper;

    public Controller() {
        objectMapper = new ObjectMapper();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
