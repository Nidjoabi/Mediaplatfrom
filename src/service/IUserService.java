package service;

public interface IUserService   {

    boolean register(String username, String password, String email);
    boolean login(String username, String password);
}
