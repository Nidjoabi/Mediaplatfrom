package service;

import Modules.LeaderBoardDto;

import java.util.List;

public interface ILeaderboardService {
    List<LeaderBoardDto> getLeaderboard();
}
