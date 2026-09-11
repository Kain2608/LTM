package poker.database;

import poker.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UserDAO {

    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) return Optional.empty();
        String key = username.trim().toLowerCase();

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE LOWER(username) = ?")) {
             
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to find user by username: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User> findById(Long userId) {
        if (userId == null) return Optional.empty();

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE user_id = ?")) {
             
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to find user by id: " + e.getMessage());
        }
        return Optional.empty();
    }

    public User createUser(String username, String email, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        String defaultAvatar = "avatar_player_1.png";
        long defaultChips = 100000L;
        int defaultElo = 1200;

        // 1. Insert into Database users table
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO users (username, email, password_hash, avatar, chips, elo_rating) VALUES (?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
             
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, defaultAvatar);
            pstmt.setLong(5, defaultChips);
            pstmt.setInt(6, defaultElo);
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);

                    // Insert initial stats record into player_statistics table
                    try (PreparedStatement statPstmt = conn.prepareStatement(
                            "INSERT INTO player_statistics (user_id, games_played, games_won, games_lost, total_chip_won, total_chip_lost) VALUES (?, 0, 0, 0, 0, 0)")) {
                        statPstmt.setLong(1, id);
                        statPstmt.executeUpdate();
                    } catch (Exception ignored) {}

                    User user = new User();
                    user.setUserId(id);
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPasswordHash(hashedPassword);
                    user.setAvatar(defaultAvatar);
                    user.setChips(defaultChips);
                    user.setEloRating(defaultElo);
                    
                    return user;
                }
            }
        } catch (Exception e) {
            System.err.println("DB Notice during createUser: " + e.getMessage());
        }

        return null;
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) return false;
        try {
            return BCrypt.checkpw(rawPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));

        String avatar = rs.getString("avatar");
        user.setAvatar(avatar != null && !avatar.isEmpty() ? avatar : "avatar_player_1.png");

        long chips = rs.getLong("chips");
        user.setChips(rs.wasNull() ? 100000L : chips);

        int elo = rs.getInt("elo_rating");
        user.setEloRating(rs.wasNull() ? 1200 : elo);
        return user;
    }
}
