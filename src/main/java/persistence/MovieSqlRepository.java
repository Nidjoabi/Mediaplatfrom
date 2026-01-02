package persistence;

import Modules.Movie;
import database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
}
