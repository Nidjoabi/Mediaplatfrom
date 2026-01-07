package service;

import Modules.FavoriteMediaDto;

import java.util.List;

public interface IFavoritesService {

    boolean addFavorite(long userId, int mediaId);
    boolean removeFavorite(long userId, int mediaId);
    List<FavoriteMediaDto> getFavorites(long userId);
}
