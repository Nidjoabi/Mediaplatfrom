package persistence;

import Modules.Game;

import java.util.ArrayList;
import java.util.List;

public class GameSqlRepository implements IGameRepository {

    private static final GameSqlRepository instance = new GameSqlRepository();
    public static GameSqlRepository getInstance(){return instance;}

    private final List<Game> games;
    public GameSqlRepository() { games = new ArrayList<>();}


    @Override
    public Game addGame(Game game) {
        if (game == null) {
            System.out.println("Game is null");
        }
        games.add(game);
        return game;
    }
}
