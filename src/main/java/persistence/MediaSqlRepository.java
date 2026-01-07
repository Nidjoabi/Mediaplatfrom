package persistence;

import Modules.MediaDto;
import database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public String getMediaType(int mediaId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");

        String sql = """
                SELECT media_type
                FROM media
                WHERE media_id = ?
                """;
        try(PreparedStatement ps = unitOfWork.prepareStatement(sql)){
            ps.setInt(1, mediaId);

            try(ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString("media_type");
                }
        } catch (SQLException e) {
            throw new RuntimeException("mediaId konnte nicht gefunden werden",e);
        }
    }

    @Override
    public List<MediaDto> searchMedia(
            String title,
            String genre,
            String mediaType,
            Integer releaseYear,
            Integer ageRestriction,
            String sortBy
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT
            m.media_id,
            m.title,
            m.description,
            m.media_type,
            m.release_year,
            m.genres,
            m.age_restriction,

            gd.studio,
            md.director AS movie_director,
            md.movie_length,
            sd.director AS series_director,
            sd.seasons,
            sd.episodes,

            COALESCE(ROUND(AVG(r.stars)::numeric, 2), 0) AS avg_score,
            COUNT(r.rating_id) AS rating_count

        FROM media m
        LEFT JOIN games_details  gd ON gd.games_id  = m.media_id
        LEFT JOIN movie_details  md ON md.movie_id  = m.media_id
        LEFT JOIN series_details sd ON sd.series_id = m.media_id

        LEFT JOIN ratings r
            ON r.media_id = m.media_id
           AND r.confirmed = true

        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();
        // Marker für Genre-Array
        List<String> genreList = null;

        // --- Filter ---
        if (title != null && !title.isBlank()) {
            sql.append(" AND LOWER(m.title) LIKE LOWER(?) ");
            params.add("%" + title.trim() + "%");
        }

        if (mediaType != null && !mediaType.isBlank()) {
            sql.append(" AND LOWER(m.media_type) = LOWER(?) ");
            params.add(mediaType.trim());
        }

        if (releaseYear != null) {
            sql.append(" AND m.release_year = ? ");
            params.add(releaseYear);
        }

        if (ageRestriction != null) {
            sql.append(" AND m.age_restriction <= ? ");
            params.add(ageRestriction);
        }

        // --- Genres: "sci-fi,thriller" => m.genres @> ? (text[])
        if (genre != null && !genre.isBlank()) {
            genreList = new ArrayList<>();
            for (String g : genre.split(",")) {
                String trimmed = g.trim();
                if (!trimmed.isBlank()) genreList.add(trimmed);
            }
            if (!genreList.isEmpty()) {
                sql.append(" AND m.genres @> ? ");
                params.add("__GENRE_ARRAY__"); // Platzhalter
            }
        }

        // --- GROUP BY ---
        sql.append("""
        GROUP BY
            m.media_id, m.title, m.description, m.media_type, m.release_year, m.genres, m.age_restriction,
            gd.studio,
            md.director, md.movie_length,
            sd.director, sd.seasons, sd.episodes
    """);

        // --- Sort ---
        String order;
        if ("score".equalsIgnoreCase(sortBy)) {
            order = " ORDER BY avg_score DESC, rating_count DESC, m.title ASC ";
        } else if ("title".equalsIgnoreCase(sortBy)) {
            order = " ORDER BY m.title ASC ";
        } else if ("releaseYear".equalsIgnoreCase(sortBy)) {
            order = " ORDER BY m.release_year DESC ";
        } else {
            order = " ORDER BY m.media_id DESC ";
        }
        sql.append(order);

        // --- Execute ---
        try (PreparedStatement ps = unitOfWork.prepareStatement(sql.toString())) {

            int idx = 1;
            for (Object p : params) {
                if (p instanceof Integer i) {
                    ps.setInt(idx++, i);
                } else if (p instanceof String s && "__GENRE_ARRAY__".equals(s)) {
                    // Genre-Array setzen
                    var arr = ps.getConnection().createArrayOf("text", genreList.toArray(new String[0]));
                    ps.setArray(idx++, arr);
                } else {
                    ps.setString(idx++, (String) p);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<MediaDto> out = new ArrayList<>();

                while (rs.next()) {
                    MediaDto dto = new MediaDto();

                    dto.setMediaId(rs.getInt("media_id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setDescription(rs.getString("description"));
                    dto.setMediaType(rs.getString("media_type"));
                    dto.setReleaseYear(rs.getInt("release_year"));
                    dto.setAgeRestriction(rs.getInt("age_restriction"));

                    // genres: text[]
                    var genresArr = rs.getArray("genres");
                    if (genresArr != null) {
                        String[] g = (String[]) genresArr.getArray();
                        dto.setGenres(java.util.Arrays.asList(g));
                    } else {
                        dto.setGenres(java.util.List.of());
                    }

                    // score meta
                    dto.setScore(rs.getDouble("avg_score"));
                    dto.setRatingCount(rs.getInt("rating_count"));

                    // details nach Typ
                    String t = dto.getMediaType() == null ? "" : dto.getMediaType().toLowerCase();
                    if ("game".equals(t)) {
                        dto.setStudio(rs.getString("studio"));
                    } else if ("movie".equals(t)) {
                        dto.setDirector(rs.getString("movie_director"));
                        int len = rs.getInt("movie_length");
                        dto.setMovieLength(rs.wasNull() ? null : len);
                    } else if ("series".equals(t)) {
                        dto.setDirector(rs.getString("series_director"));

                        int s = rs.getInt("seasons");
                        dto.setSeasons(rs.wasNull() ? null : s);

                        int e = rs.getInt("episodes");
                        dto.setEpisodes(rs.wasNull() ? null : e);
                    }

                    out.add(dto);
                }

                return out;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Konnte Media Search nicht laden.", e);
        }
    }





}

