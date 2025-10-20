package service;

public interface IUserService   {

    boolean register(String username, String password, String email);
    String login(String username, String password);
}
