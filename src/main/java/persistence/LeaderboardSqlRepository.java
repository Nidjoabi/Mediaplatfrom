package persistence;

import Modules.LeaderBoardDto;
import database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardSqlRepository implements ILeaderboardRepository {

    private final UnitOfWork unitOfWork;
    private static LeaderboardSqlRepository instance = null;

    public static LeaderboardSqlRepository getInstance(UnitOfWork unitOfWork) {
        if (instance == null) {
            instance = new LeaderboardSqlRepository(unitOfWork);
        }
        return instance;
    }

    private LeaderboardSqlRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public List<LeaderBoardDto> getLeaderboard() {

        String sql = """
            SELECT
            u.username                              AS username,
            COUNT(r.rating_id)                      AS rating_count,
            RANK() OVER (ORDER BY COUNT(r.rating_id) DESC) AS user_rank
            FROM users u
            LEFT JOIN ratings r
            ON r.creator_user_id = u.user_id
            AND r.confirmed = true
            GROUP BY u.user_id, u.username
            ORDER BY rating_count DESC, username ASC
        """;

        try (PreparedStatement ps = unitOfWork.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                List<LeaderBoardDto> out = new ArrayList<>();
                while (rs.next()) {
                    LeaderBoardDto dto = new LeaderBoardDto();
                    dto.setUsername(rs.getString("username"));
                    dto.setRatingCount(rs.getInt("rating_count"));
                    dto.setRank(rs.getInt("user_rank"));
                    out.add(dto);
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Konnte Leaderboard nicht laden.", e);
        }
    }
}
