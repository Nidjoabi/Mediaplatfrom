package service;

import Modules.FavoriteMediaDto;
import persistence.IFavoritesRepository;

import java.util.List;

public class FavoritesService implements IFavoritesService {
    private static FavoritesService instance = null;
    private final IFavoritesRepository favoritesRepository;

    public FavoritesService(IFavoritesRepository favoritesRepository) {

        this.favoritesRepository = favoritesRepository;
    }

    public static FavoritesService getInstance(IFavoritesRepository favoritesRepository) {
        if (instance == null) {
            instance = new FavoritesService(favoritesRepository);
        }
        return instance;

    }

    @Override
    public boolean addFavorite(long userId, int mediaId){
        if(mediaId <= 0) throw new IllegalArgumentException("Invalid mediaId");
        if(userId <= 0)  throw new IllegalArgumentException("Invalid userId");
        return favoritesRepository.addFavorite(userId, mediaId);
    }

    @Override
    public boolean removeFavorite(long userId, int mediaId){
        if(mediaId <= 0) throw new IllegalArgumentException("Invalid mediaId");
        if(userId <= 0)  throw new IllegalArgumentException("Invalid userId");
        return favoritesRepository.removeFavorite(userId, mediaId);
    }

    @Override
    public List<FavoriteMediaDto> getFavorites(long userId){
        if(userId <= 0) throw new IllegalArgumentException("Invalid userId");
        return favoritesRepository.getFavorites(userId);
    }
}
