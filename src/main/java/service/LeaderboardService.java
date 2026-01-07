package service;


import Modules.LeaderBoardDto;
import persistence.ILeaderboardRepository;

import java.util.List;

public class LeaderboardService implements ILeaderboardService {

    private static LeaderboardService instance = null;
    private final ILeaderboardRepository leaderboardRepository;

    public LeaderboardService(ILeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    public static LeaderboardService getInstance(ILeaderboardRepository leaderboardRepository){
        if (instance == null) {
            instance = new LeaderboardService(leaderboardRepository);
        }
        return instance;
    }

    @Override
    public List<LeaderBoardDto> getLeaderboard(){
        return leaderboardRepository.getLeaderboard();
    }
}
