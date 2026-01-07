package persistence;

import Modules.Game;
import Modules.Movie;
import Modules.Series;
import database.UnitOfWork;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public Series updateSeries(int mediaId, Series series, long userId) {
        if (series == null) {
            throw new IllegalArgumentException("Series is null");
        }

        String sqlMedia = """
                UPDATE media
                SET title = ?, description = ?, media_type = ?, release_year = ?, genres = ?, age_restriction = ?
                WHERE media_id = ? AND created_by_user_id = ?
                RETURNING media_id;
                """;

        String sqlSeries = """
                UPDATE series_details
                SET director = ?,
                    episodes = ?,
                    seasons = ?,
                WHERE series_id = ?
                """;
        try (PreparedStatement psMedia = unitOfWork.prepareStatement(sqlMedia)) {
            psMedia.setString(1, series.getTitle());
            psMedia.setString(2, series.getDescription());
            psMedia.setString(3, series.getMediaType());
            psMedia.setInt(4,series.getReleaseYear());

            var arr = psMedia.getConnection().createArrayOf("text", series.getGenres().toArray(new String[0]));
            psMedia.setArray(5, arr);

            psMedia.setInt(6, series.getAgeRestriction());
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

            try (PreparedStatement psSeries = unitOfWork.prepareStatement(sqlSeries)) {
                psSeries.setString(1,series.getDirector());
                psSeries.setInt(2, series.getSeasons());
                psSeries.setInt(3, series.getEpisodes());
                psSeries.setLong(4, updatedMediaId);

                int updated = psSeries.executeUpdate();
                if (updated == 0) {
                    unitOfWork.rollbackTransaction();
                    return null;
                }
            }
            unitOfWork.commitTransaction();
            return series;

        } catch (SQLException ex) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Konnte Game nicht updaten.", ex);
        }
    }

    public Series getSeriesById(int mediaId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");

        String sql = """
                    SELECT
                    m.title,
                    m.description,
                    m.media_type,
                    m.release_year,
                    m.genres,
                    m.age_restriction,
                    s.director,
                    s.episodes,
                    s.seasons
                    FROM media m
                    JOIN series_details s ON s.series_id = m.media_id
                    WHERE m.media_id = ? AND m.media_type = 'series'
                """;

        try (PreparedStatement ps = unitOfWork.prepareStatement(sql)) {
            ps.setInt(1, mediaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Series series = new Series();
                series.setTitle(rs.getString("title"));
                series.setDescription(rs.getString("description"));
                series.setMediaType(rs.getString("media_type"));
                series.setReleaseYear(rs.getInt("release_year"));
                series.setAgeRestriction(rs.getInt("age_restriction"));

                Array arr = rs.getArray("genres");
                if (arr != null) {
                    String[] gArr = (String[]) arr.getArray();
                    series.setGenres(Arrays.asList(gArr));
                } else {
                    series.setGenres(List.of());
                }

                series.setDirector(rs.getString("director"));
                series.setEpisodes(rs.getInt("episodes"));
                series.setSeasons(rs.getInt("seasons"));
                return series;
            }


        } catch (SQLException e) {
            throw new RuntimeException("Game konnte nicht gefunden werden",e);
        }
    }
}
