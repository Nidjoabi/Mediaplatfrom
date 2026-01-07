package persistence;

import Modules.FavoriteMediaDto;

import java.util.List;

public interface IFavoritesRepository {

    boolean addFavorite(long userId, int mediaId);
    boolean removeFavorite(long userId, int mediaId);
    List<FavoriteMediaDto> getFavorites(long userId);
}
