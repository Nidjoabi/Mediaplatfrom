package persistence;

import Modules.User;
import database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public void createUser(String username, String password, String email) {
        String sql = """ 
                INSERT INTO users (username, password, email)
                VALUES (?, ?, ?)
                RETURNING user_id, username, password, email
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
            }
        } catch (SQLException ex) {
            // 23505 = unique_violation (UNIQUE(email))
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

    @Override
    public User getProfile(long userId) {
        String sql = """
                SELECT user_id, username, email
                FROM users
                WHERE  user_id = ?;
        """;

        try (PreparedStatement ps = unitOfWork.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                User u = new User();
                u.setUser_id(rs.getInt("user_id"));     // oder setUserId(...) je nach deinem Model
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));

                return u;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Konnte Profil nicht laden.", e);
        }

    }

    @Override
    public User updateProfile(long userId, User user){
        String sql = """
                UPDATE users
                SET username = ?, password = ?, email = ?
                WHERE user_id = ?;
        """;
        try(PreparedStatement ps = unitOfWork.prepareStatement(sql)){
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setLong(4,userId);

            int updated = ps.executeUpdate();
            if(updated == 0){
                unitOfWork.rollbackTransaction();
                return null;
            }

            unitOfWork.commitTransaction();
            return user;



        }catch (SQLException ex){
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Konnte Profil nicht updaten",ex);
        }
    }

}
