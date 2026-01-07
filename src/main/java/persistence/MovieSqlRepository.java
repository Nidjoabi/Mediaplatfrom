package persistence;

import Modules.Game;
import Modules.Movie;
import database.UnitOfWork;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieSqlRepository implements IMovieRepository{

    private final UnitOfWork unitOfWork;
    private static MovieSqlRepository instance = null;

    public static MovieSqlRepository getInstance(UnitOfWork unitOfWork) {
        if (instance == null) {
            instance = new MovieSqlRepository(unitOfWork);
        }
        return instance;
    }

    private MovieSqlRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public Movie addMovie(Movie movie, long userId) {

        if (movie == null) {
            throw new IllegalArgumentException("movie is null");
        }

        String sqlMedia = """
            INSERT INTO media (title, description, media_type, release_year, genres, age_restriction, created_by_user_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING media_id
            """;

        String sqlMovie = """
                INSERT INTO movie_details(movie_id, director, movie_length)
                VALUES (?,?,?)
                """;

        try{
            long mediaId;

            try(PreparedStatement ps = unitOfWork.prepareStatement(sqlMedia)){
                ps.setString(1, movie.getTitle());
                ps.setString(2, movie.getDescription());
                ps.setString(3, "movie");
                ps.setInt(4, movie.getReleaseYear());

                java.sql.Array genresArr = ps.getConnection()
                        .createArrayOf("text", movie.getGenres().toArray(new String[0]));
                ps.setArray(5, genresArr);

                ps.setInt(6, movie.getAgeRestriction());
                ps.setLong(7, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("INSERT RETURNING lieferte keine ID.");
                    }

                    mediaId = rs.getLong("media_id");

                }
            }
            try (PreparedStatement ps2 = unitOfWork.prepareStatement(sqlMovie)) {
                ps2.setLong(1, mediaId);
                ps2.setString(2, movie.getDirector());
                ps2.setInt(3, movie.getMovieLength());
                ps2.executeUpdate();
            }

            unitOfWork.commitTransaction();
            return movie;

        }catch(SQLException e){
            throw new RuntimeException("Konnte Series nicht erstellen.", e);
        }
    }

    public boolean deleteMovie(int mediaId, long userId) {

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

    @Override
    public Movie updateMovie(int mediaId, Movie movie, long userId) {
        if (movie == null) {
            throw new IllegalArgumentException("movie is null");
        }

        String sqlMedia = """
                UPDATE media
                SET title = ?, description = ?, media_type = ?, release_year = ?, genres = ?, age_restriction = ?
                WHERE media_id = ? AND created_by_user_id = ?
                RETURNING media_id;
                """;

        String sqlMovie = """
                UPDATE movie_details
                SET director = ?,
                    movie_length = ?
                WHERE movie_id = ?
                """;
        try (PreparedStatement psMedia = unitOfWork.prepareStatement(sqlMedia)) {
            psMedia.setString(1, movie.getTitle());
            psMedia.setString(2, movie.getDescription());
            psMedia.setString(3, movie.getMediaType());
            psMedia.setInt(4, movie.getReleaseYear());


            var arr = psMedia.getConnection().createArrayOf("text", movie.getGenres().toArray(new String[0]));
            psMedia.setArray(5, arr);

            psMedia.setInt(6, movie.getAgeRestriction());
            psMedia.setLong(7, mediaId);
            psMedia.setLong(8, userId);

            long updatedMediaId;
            try (ResultSet rsMedia = psMedia.executeQuery()) {
                if (!rsMedia.next()) {
                    unitOfWork.rollbackTransaction();
                    return null; // not found / not owner
                }
                updatedMediaId = rsMedia.getLong("media_id");
            }

            try (PreparedStatement psMovie = unitOfWork.prepareStatement(sqlMovie)) {
                psMovie.setString(1, movie.getDirector());
                psMovie.setInt(2, movie.getMovieLength());
                psMovie.setLong(3, updatedMediaId);

                int updated = psMovie.executeUpdate();
                if (updated == 0) {
                    unitOfWork.rollbackTransaction();
                    return null;
                }

            }
            unitOfWork.commitTransaction();
            return movie;

        } catch (SQLException ex) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Konnte Movie nicht updaten.", ex);
        }
    }

    public Movie getMovieById(int mediaId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");

        String sql = """
                    SELECT
                    m.title,
                    m.description,
                    m.media_type,
                    m.release_year,
                    m.genres,
                    m.age_restriction,
                    mo.director,
                    mo.movie_length
                    FROM media m
                    JOIN movie_details mo ON mo.movie_id = m.media_id
                    WHERE m.media_id = ? AND m.media_type = 'movie'
                """;

        try (PreparedStatement ps = unitOfWork.prepareStatement(sql)) {
            ps.setInt(1, mediaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Movie movie = new Movie();
                movie.setTitle(rs.getString("title"));
                movie.setDescription(rs.getString("description"));
                movie.setMediaType(rs.getString("media_type"));
                movie.setReleaseYear(rs.getInt("release_year"));
                movie.setAgeRestriction(rs.getInt("age_restriction"));

                Array arr = rs.getArray("genres");
                if (arr != null) {
                    String[] gArr = (String[]) arr.getArray();
                    movie.setGenres(Arrays.asList(gArr));
                } else {
                    movie.setGenres(List.of());
                }

                movie.setMovieLength(rs.getInt("movie_length"));
                movie.setDirector(rs.getString("director"));

                return movie;
            }


        } catch (SQLException e) {
            throw new RuntimeException("Game konnte nicht gefunden werden",e);
        }
    }
}
