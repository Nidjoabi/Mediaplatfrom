package service;

import persistence.UserSqlRepository;

import java.util.UUID;

public class TokenService {

    private static final TokenService instance = new TokenService();
    public static TokenService getInstance(){return instance;}

    public String createToken(String username) {

        return username + " - " + UUID.randomUUID().toString();
    }
}