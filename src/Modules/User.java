package Modules;

public class User {

    private String username;
    private String password;
    private String email;
    private String token;


    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.token = null;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }


}
