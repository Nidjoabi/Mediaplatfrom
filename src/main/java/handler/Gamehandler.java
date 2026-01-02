package handler;

import Modules.Game;
import service.IGameService;

public class Gamehandler implements IMediaHandler<Game> {

    public IGameService gameService;
    public Gamehandler(IGameService gameService) {
        this.gameService = gameService;
    }


    @Override
    public Game addMedia(Game mediaType, long userId){

        if(!validateMedia(mediaType))return null;
        return gameService.addGame(mediaType, userId);
    }

    @Override
    public boolean validateMedia(Game media) {
        if (media == null) return false;

        return media.getTitle() != null && !media.getTitle().isBlank();
    }


}

