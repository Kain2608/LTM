package poker.model;

import java.sql.Timestamp;

public class Friend {
    private Long friendshipId;
    private Long userId;
    private Long friendId;
    private String status;
    private Timestamp createdAt;
    private String friendUsername;
    private String friendAvatar;

    public Friend() {}

    public Long getFriendshipId() { return friendshipId; }
    public void setFriendshipId(Long friendshipId) { this.friendshipId = friendshipId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getFriendId() { return friendId; }
    public void setFriendId(Long friendId) { this.friendId = friendId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getFriendUsername() { return friendUsername; }
    public void setFriendUsername(String friendUsername) { this.friendUsername = friendUsername; }
    public String getFriendAvatar() { return friendAvatar; }
    public void setFriendAvatar(String friendAvatar) { this.friendAvatar = friendAvatar; }
}

