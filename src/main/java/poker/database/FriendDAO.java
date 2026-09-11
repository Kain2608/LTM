package poker.database;

import poker.model.Friend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendDAO {

    public boolean sendFriendRequest(Long userId, Long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, friendId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean acceptFriendRequest(Long friendshipId, Long userId) {
        String sql = "UPDATE friends SET status = 'ACCEPTED' WHERE friendship_id = ? AND friend_id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, friendshipId);
            pstmt.setLong(2, userId); // Only the target can accept
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeOrRejectFriend(Long friendshipId, Long userId) {
        // Can be removed by either party
        String sql = "DELETE FROM friends WHERE friendship_id = ? AND (user_id = ? OR friend_id = ?)";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, friendshipId);
            pstmt.setLong(2, userId);
            pstmt.setLong(3, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Friend> getFriendsList(Long userId) {
        String sql = "SELECT f.*, u.username, u.avatar FROM friends f " +
                     "JOIN users u ON (f.friend_id = u.user_id OR f.user_id = u.user_id) " +
                     "WHERE (f.user_id = ? OR f.friend_id = ?) AND f.status = 'ACCEPTED' AND u.user_id != ?";
        return getFriendListQuery(sql, userId, userId, userId);
    }

    public List<Friend> getPendingRequests(Long userId) {
        // Find requests where the current user is the TARGET (friend_id)
        String sql = "SELECT f.*, u.username, u.avatar FROM friends f " +
                     "JOIN users u ON f.user_id = u.user_id " +
                     "WHERE f.friend_id = ? AND f.status = 'PENDING'";
        return getFriendListQuery(sql, userId);
    }

    private List<Friend> getFriendListQuery(String sql, Long... params) {
        List<Friend> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            for (int i = 0; i < params.length; i++) {
                pstmt.setLong(i + 1, params[i]);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Friend friend = new Friend();
                    friend.setFriendshipId(rs.getLong("friendship_id"));
                    friend.setUserId(rs.getLong("user_id"));
                    friend.setFriendId(rs.getLong("friend_id"));
                    friend.setStatus(rs.getString("status"));
                    friend.setCreatedAt(rs.getTimestamp("created_at"));
                    friend.setFriendUsername(rs.getString("username"));
                    friend.setFriendAvatar(rs.getString("avatar"));
                    list.add(friend);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

