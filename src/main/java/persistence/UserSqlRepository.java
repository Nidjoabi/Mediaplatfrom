package persistence;

import Modules.User;
import database.DatabaseManager;
import database.UnitOfWork;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserSqlRepository implements IUserRepository {
    private UnitOfWork unitOfWork;
    private static UserSqlRepository instance = null;
    public static UserSqlRepository getInstance(UnitOfWork unitOfWork){
        if(instance == null){
            instance = new UserSqlRepository(unitOfWork);
        }
        return instance;
    }



    private UserSqlRepository(UnitOfWork unitOfWork) {

        this.unitOfWork = unitOfWork;
    }

    @Override
    public User createUser(String username, String password, String email) {
        String sql = """ 
                INSERT INTO users (username, password, email)
                VALUES (?, ?, ?)
                RETURNING user_id, username, email, token
                """;
        try(PreparedStatement ps = unitOfWork.prepareStatement(sql)){

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("INSERT RETURNING lieferte keine Zeile.");
                }
                unitOfWork.commitTransaction();
                User u = getUser(rs);
                return u;
            }
        } catch (SQLException ex) {
            // 23505 = unique_violation (z. B. bei UNIQUE(email))
            if ("23505".equals(ex.getSQLState())) {
                throw new RuntimeException("Benutzername oder E-Mail bereits vergeben.", ex);
            }
            throw new RuntimeException("Konnte User nicht erstellen.", ex);
        }

    }

    private static User getUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUser_id(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setEmail(rs.getString("email"));
        u.setToken(rs.getString("token"));
        return u;
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = """
                SELECT * FROM users WHERE username = ?;
        """;
        try(PreparedStatement ps = unitOfWork.prepareStatement(sql)){
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                User u = getUser(rs);
                return u;

            }
        }catch (SQLException ex){
            throw new RuntimeException("Fehler beim Abrufen des Users mit username=" + username, ex);
        }
    }

}
