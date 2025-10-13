package service;

import Modules.Game;
import persistence.IGameRepository;

public class GameService implements IGameService {
    private static GameService instance = null;
    private final IGameRepository gameRepository;

    public GameService(IGameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public static GameService getInstance(IGameRepository gameRepository){
        if (instance == null) {
            instance = new GameService(gameRepository);
        }
        return instance;
    }
    @Override
    public Game addGame(Game game) {
        if (game == null) {
            System.out.println("Game is null");
        }
        return gameRepository.addGame(game);
    }
}