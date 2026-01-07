package Controller;

import Modules.Game;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.IGameService;

public class GameStrategy implements MediaStrategy {

    private final IGameService gameService;

    public GameStrategy(IGameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public String type() {
        return "game";
    }

    @Override
    public Response add(JsonNode node, long userId, ObjectMapper mapper) throws Exception {
        Game game = mapper.treeToValue(node, Game.class);
        Game created = gameService.addGame(game, userId);
        return new Response(HttpStatus.CREATED, ContentType.JSON, mapper.writeValueAsString(created));
    }

    @Override
    public Response update(int mediaId, JsonNode node, long userId, ObjectMapper mapper) throws Exception {
        Game game = mapper.treeToValue(node, Game.class);
        Game updated = gameService.updateGame(mediaId, game, userId);

        if (updated == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"message\":\"Not found or not owner\"}");
        }

        return new Response(HttpStatus.OK, ContentType.JSON, mapper.writeValueAsString(updated));
    }

    @Override
    public Response getById(int mediaId, ObjectMapper mapper) throws Exception {
        Game game = gameService.getGameById(mediaId);

        if (game == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"message\":\"Game not found\"}");
        }

        return new Response(HttpStatus.OK, ContentType.JSON, mapper.writeValueAsString(game));
    }
}
