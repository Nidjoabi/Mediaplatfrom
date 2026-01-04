package persistence;

import Modules.Game;
import Modules.Series;
import database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeriesSqlRepository implements ISeriesRepository {

    private final UnitOfWork unitOfWork;
    private static SeriesSqlRepository instance = null;

    public static SeriesSqlRepository getInstance(UnitOfWork unitOfWork) {
        if (instance == null) {
            instance = new SeriesSqlRepository(unitOfWork);
        }
        return instance;
    }

    private SeriesSqlRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }


    @Override
    public Series addSeries(Series series, long userId) {
        if (series == null) {
            throw new IllegalArgumentException("series is null");
        }

        String sqlMedia = """
        INSERT INTO media (title, description, media_type, release_year, genres, age_restriction, created_by_user_id)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        RETURNING media_id
    """;

        String sqlSeries = """
        INSERT INTO series_details (series_id, director, seasons, episodes)
        VALUES (?, ?, ?, ?)
    """;

        try {
            long mediaId;

            try (PreparedStatement ps = unitOfWork.prepareStatement(sqlMedia)) {
                ps.setString(1, series.getTitle());
                ps.setString(2, series.getDescription());
                ps.setString(3, "series");
                ps.setInt(4, series.getReleaseYear());


                java.sql.Array genresArr = ps.getConnection()
                        .createArrayOf("text", series.getGenres().toArray(new String[0]));
                ps.setArray(5, genresArr);

                ps.setInt(6, series.getAgeRestriction());
                ps.setLong(7, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("INSERT RETURNING lieferte keine ID.");
                    }

                    mediaId = rs.getLong("media_id");

                }
            }


            try (PreparedStatement ps2 = unitOfWork.prepareStatement(sqlSeries)) {
                ps2.setLong(1, mediaId);
                ps2.setString(2, series.getDirector());
                ps2.setInt(3, series.getSeasons());
                ps2.setInt(4, series.getEpisodes());
                ps2.executeUpdate();
            }

            unitOfWork.commitTransaction();
            return series;

        } catch (SQLException e) {
            throw new RuntimeException("Konnte Series nicht erstellen.", e);
        }
    }

    public boolean deleteSeries(int mediaId, long userId) {


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
            throw new RuntimeException("Konnte Series nicht gelöscht werden.", e);
        }
    }
}
