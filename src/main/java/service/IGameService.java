package service;

import Modules.Game;

public interface IGameService {
     Game addGame(Game game, long userId);
     boolean deleteGame(int mediaId, long userId);
}
