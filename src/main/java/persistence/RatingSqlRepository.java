package persistence;

import Modules.Rating;
import Modules.RatingDto;
import database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingSqlRepository implements IRatingRepository{

    private final UnitOfWork unitOfWork;
    private static RatingSqlRepository instance = null;

    public static RatingSqlRepository getInstance(UnitOfWork unitOfWork) {
        if (instance == null) {
            instance = new RatingSqlRepository(unitOfWork);
        }
        return instance;
    }

    private RatingSqlRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public Rating addRating(int mediaId,long userId,Rating rating) {

        String sql = """
                INSERT INTO ratings(
                media_id,
                creator_user_id,
                stars,
                text,
                likes)
                Values(?, ?, ?, ?, ?)
                RETURNING rating_id;
                """;

        String sqlFetch = """
            SELECT r.rating_id, r.media_id, r.creator_user_id, r.stars, r.text, r.likes,
                           r.confirmed, r.confirmed_at,
                           u.username AS creator_username,
                           m.title    AS media_title
                    FROM ratings r
                    JOIN users u ON u.user_id = r.creator_user_id
                    JOIN media  m ON m.media_id = r.media_id
                    WHERE r.rating_id = ?
        """;

        try (PreparedStatement ps = this.unitOfWork.prepareStatement(sql)) {
            ps.setInt(1, mediaId);
            ps.setLong(2, userId);
            ps.setInt(3, rating.getStars());
            ps.setString(4, rating.getText());
            ps.setInt(5, rating.getLikes());

            long ratingId;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                ratingId = rs.getLong(1);
            }

            try(PreparedStatement ps2 = this.unitOfWork.prepareStatement(sqlFetch)) {
                ps2.setLong(1, ratingId);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    rs2.next();
                    Rating out = new Rating();
                    out.setRatingId(rs2.getLong(1));
                    out.setMediaId(rs2.getInt(2));
                    out.setCreatorUserId(rs2.getLong(3));
                    out.setStars(rs2.getInt(4));
                    out.setText(rs2.getString(5));
                    out.setLikes(rs2.getInt(6));
                    out.setConfirmed(rs2.getBoolean(7));
                    out.setCreatorUsername(rs2.getString("creator_username"));
                    out.setMediaTitle(rs2.getString("media_title"));

                    unitOfWork.commitTransaction();
                    return out;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Rating konnte nicht erstellt werden!",e);
        }

    }

    @Override
    public Rating confirmRating(long userId, long ratingId) {
        String sqlUpdate = """
        UPDATE ratings
        SET confirmed = true, confirmed_at = NOW()
        WHERE rating_id = ? AND creator_user_id = ? AND confirmed = false
        RETURNING rating_id
    """;

        String sqlFetch = """
        SELECT r.rating_id, r.media_id, r.creator_user_id, r.stars, r.text, r.likes,
               r.confirmed, r.confirmed_at,
               u.username AS creator_username,
               m.title    AS media_title
        FROM ratings r
        JOIN users u ON u.user_id = r.creator_user_id
        JOIN media  m ON m.media_id = r.media_id
        WHERE r.rating_id = ?
    """;

        try (PreparedStatement ps = unitOfWork.prepareStatement(sqlUpdate)) {
            ps.setLong(1, ratingId);
            ps.setLong(2, userId);

            long updatedId;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    unitOfWork.rollbackTransaction();
                    return null; // nicht gefunden / nicht owner / schon confirmed
                }
                updatedId = rs.getLong("rating_id");
            }

            try (PreparedStatement ps2 = unitOfWork.prepareStatement(sqlFetch)) {
                ps2.setLong(1, updatedId);

                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (!rs2.next()) {
                        unitOfWork.rollbackTransaction();
                        return null;
                    }

                    Rating out = new Rating();
                    out.setRatingId(rs2.getLong("rating_id"));
                    out.setMediaId(rs2.getInt("media_id"));
                    out.setCreatorUserId(rs2.getLong("creator_user_id"));
                    out.setStars(rs2.getInt("stars"));
                    out.setText(rs2.getString("text"));
                    out.setLikes(rs2.getInt("likes"));
                    out.setConfirmed(rs2.getBoolean("confirmed"));
                    out.setCreatorUsername(rs2.getString("creator_username"));
                    out.setMediaTitle(rs2.getString("media_title"));

                    unitOfWork.commitTransaction();
                    return out;
                }
            }

        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Rating konnte nicht bestätigt werden!", e);
        }
    }

    @Override
    public Rating likeRating(long userId, long ratingId) {
        String sqlInsertLike = """
        INSERT INTO rating_likes (rating_id, user_id)
        VALUES (?, ?)
    """;

        String sqlUpdateLikes = """
        UPDATE ratings
        SET likes = likes + 1
        WHERE rating_id = ?
        RETURNING rating_id
    """;

        String sqlFetch = """
        SELECT r.rating_id, r.media_id, r.creator_user_id, r.stars, r.text, r.likes,
               r.confirmed, r.confirmed_at,
               u.username AS creator_username,
               m.title    AS media_title
        FROM ratings r
        JOIN users u ON u.user_id = r.creator_user_id
        JOIN media  m ON m.media_id = r.media_id
        WHERE r.rating_id = ?
    """;

        try {

            try (PreparedStatement ps = unitOfWork.prepareStatement(sqlInsertLike)) {
                ps.setLong(1, ratingId);
                ps.setLong(2, userId);
                ps.executeUpdate();
            }


            long updatedId;
            try (PreparedStatement ps2 = unitOfWork.prepareStatement(sqlUpdateLikes)) {
                ps2.setLong(1, ratingId);

                try (ResultSet rs = ps2.executeQuery()) {
                    if (!rs.next()) {
                        unitOfWork.rollbackTransaction();
                        return null; // rating existiert nicht
                    }
                    updatedId = rs.getLong("rating_id");
                }
            }

            // 3) Rating zurückgeben
            try (PreparedStatement ps3 = unitOfWork.prepareStatement(sqlFetch)) {
                ps3.setLong(1, updatedId);

                try (ResultSet rs3 = ps3.executeQuery()) {
                    if (!rs3.next()) {
                        unitOfWork.rollbackTransaction();
                        return null;
                    }

                    Rating out = new Rating();
                    out.setRatingId(rs3.getLong("rating_id"));
                    out.setMediaId(rs3.getInt("media_id"));
                    out.setCreatorUserId(rs3.getLong("creator_user_id"));
                    out.setStars(rs3.getInt("stars"));
                    out.setText(rs3.getString("text"));
                    out.setLikes(rs3.getInt("likes"));
                    out.setConfirmed(rs3.getBoolean("confirmed"));
                    out.setCreatorUsername(rs3.getString("creator_username"));
                    out.setMediaTitle(rs3.getString("media_title"));

                    unitOfWork.commitTransaction();
                    return out;
                }
            }

        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();


            if ("23505".equals(e.getSQLState())) {
                return null;
            }
            throw new RuntimeException("Konnte Rating nicht liken.", e);
        }
    }

    @Override
    public boolean deleteRating(long userId, long ratingId) {
        String sql = """
                DELETE FROM ratings
                WHERE rating_id = ? AND creator_user_id = ?
                RETURNING rating_id
                """;
        try(PreparedStatement ps = unitOfWork.prepareStatement(sql)){
            ps.setLong(1, ratingId);
            ps.setLong(2, userId);

            try(ResultSet rs = ps.executeQuery()){
                boolean deleted = rs.next();
                unitOfWork.commitTransaction();
                return deleted;
            }

        }catch (SQLException e){
            throw new RuntimeException("Konnte Rating nicht gelöscht werden.", e);
        }
    }

    @Override
    public Rating updateRating(long userId, long ratingId, Rating rating) {
        String sql = """
                UPDATE ratings
                SET stars = ?, text = ?
                WHERE rating_id = ? AND  creator_user_id = ?
        """;

        try(PreparedStatement ps = unitOfWork.prepareStatement(sql)){
            ps.setInt(1, rating.getStars());
            ps.setString(2, rating.getText());

            ps.setLong(3, ratingId);
            ps.setLong(4, userId);

            int updated = ps.executeUpdate();
            if(updated == 0){
                unitOfWork.rollbackTransaction();
                return null;
            }

            unitOfWork.commitTransaction();
            return rating;


        }catch (SQLException ex) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Konnte Rating nicht updaten.", ex);
        }

    }

    @Override
    public List<RatingDto> getRatingIfOwned(long userId) {

        String sql = """
            SELECT r.stars,
                           r.text,
                           r.likes,
                           m.title AS media_title,
                           m.media_type
                    FROM ratings r
                    JOIN media m ON m.media_id = r.media_id
                    WHERE r.creator_user_id = ?
                    ORDER BY r.created_at DESC;
        """;

        try (PreparedStatement ps = unitOfWork.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                List<RatingDto> list = new ArrayList<>();

                while (rs.next()) {
                    int stars = rs.getInt("stars");
                    String comment = rs.getString("text");
                    int likes = rs.getInt("likes");
                    String mediaTitle = rs.getString("media_title");
                    String mediaType = rs.getString("media_type");

                    list.add(new RatingDto(stars, comment, likes, mediaTitle, mediaType));
                }

                return list; // leer wenn nicht owner oder nicht gefunden
            }
        } catch (SQLException e) {
            throw new RuntimeException("Konnte Rating nicht laden.", e);
        }
    }

}