package persistence;

import Modules.LeaderBoardDto;

import java.util.List;

public interface ILeaderboardRepository {

    List<LeaderBoardDto> getLeaderboard();
}
