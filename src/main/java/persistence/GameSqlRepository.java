package persistence;

import Modules.Game;
import database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
            throw new IllegalArgumentException("movie is null");
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
            throw new RuntimeException("Konnte Series nicht erstellen.", e);
        }
    }

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
            throw new RuntimeException("Konnte Movie nicht gelöscht werden.", e);
        }
    }
}
