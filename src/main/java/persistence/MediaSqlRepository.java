package persistence;

import Modules.Game;
import database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MediaSqlRepository implements IMediaRepository {

    private final UnitOfWork unitOfWork;
    private static MediaSqlRepository instance = null;

    public static MediaSqlRepository getInstance(UnitOfWork unitOfWork) {
        if (instance == null) {
            instance = new MediaSqlRepository(unitOfWork);
        }
        return instance;
    }

    private MediaSqlRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public int getMediaId(String title, String mediaType, int releaseYear ){
        String sql = """
                SELECT media_id
                FROM media
                WHERE title = ? AND media_type = ? AND release_year = ?
                """;
        try(PreparedStatement ps = unitOfWork.prepareStatement(sql)){
            ps.setString(1, title);
            ps.setString(2, mediaType);
            ps.setInt(3, releaseYear);

            try (ResultSet rs = ps.executeQuery()){
                if(!rs.next()) return -1;
                return rs.getInt("media_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Media ID konnte nicht gefunden werden!",e);
        }
    }

    @Override
    public String getMediaTypeIfOwned(int mediaId, long userId) {
        String sql = """
                SELECT media_type
                FROM media
                WHERE media_id = ? AND created_by_user_id = ?
        """;

        try(PreparedStatement ps = unitOfWork.prepareStatement(sql)){
            ps.setInt(1, mediaId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()){
                if(!rs.next()) return null;
                return rs.getString("media_type");
            }
        } catch(SQLException e){
            throw new RuntimeException("media_type konnte nicht gefunden werden!",e);
        }
    }
}
