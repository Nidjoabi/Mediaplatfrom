package persistence;

import Modules.FavoriteMediaDto;
import database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoritesSqlRepository implements IFavoritesRepository{

    private final UnitOfWork unitOfWork;
    private static FavoritesSqlRepository instance = null;

    public static FavoritesSqlRepository getInstance(UnitOfWork unitOfWork) {
        if (instance == null) {
            instance = new FavoritesSqlRepository(unitOfWork);
        }
        return instance;
    }

    private FavoritesSqlRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public boolean addFavorite(long userId, int mediaId) {
        String sql = """
            INSERT INTO favorites (user_id, media_id)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
        """;
        try (PreparedStatement ps = unitOfWork.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, mediaId);

            int changed = ps.executeUpdate();
            unitOfWork.commitTransaction();
            return changed == 1;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Konnte Favorite nicht setzen.", e);
        }
    }

    @Override
    public boolean removeFavorite(long userId, int mediaId) {
        String sql = """
            DELETE FROM favorites
            WHERE user_id = ? AND media_id = ?
        """;
        try (PreparedStatement ps = unitOfWork.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, mediaId);

            int changed = ps.executeUpdate();
            unitOfWork.commitTransaction();
            return changed == 1;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Konnte Favorite nicht entfernen.", e);
        }
    }

    @Override
    public List<FavoriteMediaDto> getFavorites(long userId){
        String sql = """
        SELECT m.media_id, m.title, m.media_type, m.release_year
        FROM favorites f
        JOIN media m ON m.media_id = f.media_id
        WHERE f.user_id = ?
        ORDER BY f.created_at DESC
    """;

        try (PreparedStatement ps = unitOfWork.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                List<FavoriteMediaDto> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new FavoriteMediaDto(
                            rs.getInt("media_id"),
                            rs.getString("title"),
                            rs.getString("media_type"),
                            rs.getInt("release_year")
                    ));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Konnte Favoriten nicht laden.", e);
        }

    }
}
