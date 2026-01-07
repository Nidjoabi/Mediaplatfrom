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
    public Game addGame(Game game, long userId) {
        return gameRepository.addGame(game, userId);
    }

    public boolean deleteGame(int mediaId, long userId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");
        return gameRepository.deleteGame(mediaId, userId);
    }

    @Override
    public Game updateGame(int mediaId,Game game, long userId) {
        if(game == null) throw new IllegalArgumentException("game is null");
        return gameRepository.updateGame(mediaId,game, userId);
    }

    @Override
    public Game getGameById(int mediaId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");
        return gameRepository.getGameById(mediaId);
    }
}