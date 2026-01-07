package persistence;

import Modules.Game;
import database.UnitOfWork;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameSqlRepository implements IGameRepository {

    private final UnitOfWork unitOfWork;
    private static GameSqlRepository instance = null;

    public static GameSqlRepository getInstance(UnitOfWork unitOfWork) {
        if (instance == null) {
            instance = new GameSqlRepository(unitOfWork);
        }
        return instance;
    }

    private GameSqlRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }



    @Override
    public Game addGame(Game game, long userId) {

        if (game == null) {
            throw new IllegalArgumentException("game is null");
        }

        String sqlMedia = """
            INSERT INTO media (title, description, media_type, release_year, genres, age_restriction, created_by_user_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING media_id
            """;

        String sqlGame = """
                INSERT INTO games_details (games_id, studio)
                VALUES (?, ?)
                """;

        try{
            long mediaId;

            try(PreparedStatement ps = unitOfWork.prepareStatement(sqlMedia)){
                ps.setString(1, game.getTitle());
                ps.setString(2, game.getDescription());
                ps.setString(3, "game");
                ps.setInt(4, game.getReleaseYear());

                java.sql.Array genresArr = ps.getConnection()
                        .createArrayOf("text", game.getGenres().toArray(new String[0]));
                ps.setArray(5, genresArr);

                ps.setInt(6, game.getAgeRestriction());
                ps.setLong(7, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("INSERT RETURNING lieferte keine ID.");
                    }

                    mediaId = rs.getLong("media_id");

                }
            }
            try (PreparedStatement ps2 = unitOfWork.prepareStatement(sqlGame)) {
                ps2.setLong(1, mediaId);
                ps2.setString(2, game.getDeveloperStudio());
                ps2.executeUpdate();
            }

            unitOfWork.commitTransaction();
            return game;

        }catch(SQLException e){
            throw new RuntimeException("Konnte Game nicht erstellen.", e);
        }
    }

    @Override
    public boolean deleteGame(int mediaId, long userId) {

        String sql = """
                DELETE FROM media
                WHERE media_id = ? AND created_by_user_id = ?
                RETURNING media_id
        """;



        try(PreparedStatement ps = unitOfWork.prepareStatement(sql)){
            ps.setLong(1, mediaId);
            ps.setLong(2, userId);

            try(ResultSet rs = ps.executeQuery()){
                boolean deleted = rs.next();
                unitOfWork.commitTransaction();
                return deleted;
            }
        }catch (SQLException e){
            throw new RuntimeException("Konnte Game nicht gelöscht werden.", e);
        }
    }

    @Override
    public Game updateGame(int mediaId, Game game, long userId) {
        if (game == null) {
            throw new IllegalArgumentException("Game is null");
        }

        String sqlMedia = """
                UPDATE media
                SET title = ?, description = ?, release_year = ?, genres = ?, age_restriction = ?
                WHERE media_id = ? AND created_by_user_id = ?
                RETURNING media_id;
                """;

        String sqlGame = """
                UPDATE games_details
                SET studio = ?
                WHERE games_id = ?
                """;
        try (PreparedStatement psMedia = unitOfWork.prepareStatement(sqlMedia)) {
            psMedia.setString(1, game.getTitle());
            psMedia.setString(2, game.getDescription());
            psMedia.setString(3, game.getMediaType());
            psMedia.setInt(4, game.getReleaseYear());

            var arr = psMedia.getConnection().createArrayOf("text", game.getGenres().toArray(new String[0]));
            psMedia.setArray(5, arr);

            psMedia.setInt(6, game.getAgeRestriction());
            psMedia.setLong(7, mediaId);
            psMedia.setLong(8, userId);

            long updatedMediaId;
            try (ResultSet rsMedia = psMedia.executeQuery()) {
                if (!rsMedia.next()) {
                    unitOfWork.rollbackTransaction();
                    return null;
                }
                updatedMediaId = rsMedia.getLong("media_id");
            }

            try (PreparedStatement psGame = unitOfWork.prepareStatement(sqlGame)) {
                psGame.setString(1, game.getDeveloperStudio());
                psGame.setLong(2, updatedMediaId);

                int updated = psGame.executeUpdate();
                if (updated == 0) {
                    unitOfWork.rollbackTransaction();
                    return null;
                }
            }
            unitOfWork.commitTransaction();
            return game;

        } catch (SQLException ex) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Konnte Game nicht updaten.", ex);
        }
    }

    public Game getGameById(int mediaId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");

        String sql = """
                    SELECT
                    m.title,
                    m.description,
                    m.media_type,
                    m.release_year,
                    m.genres,
                    m.age_restriction,
                    g.studio
                    FROM media m
                    JOIN games_details g ON g.games_id = m.media_id
                    WHERE m.media_id = ? AND m.media_type = 'game'
                """;

        try (PreparedStatement ps = unitOfWork.prepareStatement(sql)) {
            ps.setInt(1, mediaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Game game = new Game();
                game.setTitle(rs.getString("title"));
                game.setDescription(rs.getString("description"));
                game.setMediaType(rs.getString("media_type"));
                game.setReleaseYear(rs.getInt("release_year"));
                game.setAgeRestriction(rs.getInt("age_restriction"));

                Array arr = rs.getArray("genres");
                if (arr != null) {
                    String[] gArr = (String[]) arr.getArray();
                    game.setGenres(Arrays.asList(gArr));
                } else {
                    game.setGenres(List.of());
                }


                game.setDeveloperStudio(rs.getString("studio"));

                return game;
            }


        } catch (SQLException e) {

            throw new RuntimeException("Game konnte nicht gefunden werden",e);
        }
    }

}
