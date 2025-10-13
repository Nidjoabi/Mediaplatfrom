import Modules.User;

import handler.Gamehandler;
import handler.Userhandler;
import persistence.IUserRepository;
import persistence.UserSqlRepository;
import persistence.IGameRepository;
import persistence.GameSqlRepository;
import service.IUserService;
import service.UserService;
import service.TokenService;
import service.IGameService;
import service.GameService;



public class Main {
    public static void main(String[] args) {

        // === User Setup ===
        IUserRepository userRepository = UserSqlRepository.getInstance();
        TokenService tokenService = TokenService.getInstance();
        IUserService userService = UserService.getInstance(userRepository, tokenService);

        User user = new User("admin", "password", "email");

        Userhandler userHandler = new Userhandler(userService);
        boolean registered = userHandler.register(user.getUsername(), user.getPassword(), user.getEmail());
        boolean loggedIn = userHandler.login(user.getUsername(), user.getPassword());

        System.out.println("registered? " + registered);
        System.out.println("Login erfolgreich? " + loggedIn);
        User fromRepo = userRepository.getUserByUsername(user.getUsername());
        System.out.println("Token im Repo: " + fromRepo.getToken());
        System.out.println("Token im lokalen Objekt: " + user.getToken());


        // === Game Setup ===
        IGameRepository gameRepository = GameSqlRepository.getInstance(); // Liste als "DB"
        IGameService gameService = new GameService(gameRepository);
        Gamehandler gameHandler = new Gamehandler(gameService);


    }
}
