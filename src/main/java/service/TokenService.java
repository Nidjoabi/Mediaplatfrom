package service;

import java.util.UUID;

public class TokenService {
    public String createToken(String username) {

        return username + " - " + UUID.randomUUID().toString();
    }
}