package Controller;

import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.server.Response;
import service.ILeaderboardService;

public class LeaderboardController extends Controller{

    private ILeaderboardService leaderboardService;

    public LeaderboardController(ILeaderboardService leaderboardService){
        this.leaderboardService = leaderboardService;
    }

    public Response getLeaderboard(){
        try {
            var result = leaderboardService.getLeaderboard();
            return new Response(HttpStatus.OK, ContentType.JSON,
                    getObjectMapper().writeValueAsString(result));

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"message\":\"Error processing request\"}");
        }
    }

}
