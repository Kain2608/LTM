package com.poker.poker_.service;

import com.poker.poker_.entity.Friend;
import com.poker.poker_.entity.FriendStatus;
import com.poker.poker_.entity.Notification;
import com.poker.poker_.entity.NotificationType;
import com.poker.poker_.entity.User;
import com.poker.poker_.payload.response.FriendResponse;
import com.poker.poker_.repository.FriendRepository;
import com.poker.poker_.repository.NotificationRepository;
import com.poker.poker_.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public String sendFriendRequest(User sender, String targetUsername) {
        if (sender.getUsername().equals(targetUsername)) {
            throw new RuntimeException("Error: You cannot send a friend request to yourself.");
        }

        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("Error: Target user not found."));

        // Check if a friendship already exists (either direction)
        Optional<Friend> existingFriendship1 = friendRepository.findByUserAndFriend(sender, targetUser);
        Optional<Friend> existingFriendship2 = friendRepository.findByUserAndFriend(targetUser, sender);

        if (existingFriendship1.isPresent() || existingFriendship2.isPresent()) {
            throw new RuntimeException("Error: A friend request or friendship already exists.");
        }

        Friend friendRequest = new Friend();
        friendRequest.setUser(sender);
        friendRequest.setFriend(targetUser);
        friendRequest.setStatus(FriendStatus.PENDING);
        friendRepository.save(friendRequest);

        Notification notification = new Notification();
        notification.setUser(targetUser);
        notification.setType(NotificationType.FRIEND_REQUEST);
        notification.setContent(sender.getUsername() + " has sent you a friend request.");
        notificationRepository.save(notification);

        return "Friend request sent successfully to " + targetUsername;
    }

    @Transactional
    public String acceptFriendRequest(User targetUser, Long friendshipId) {
        Friend friendRequest = friendRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Error: Friend request not found."));

        if (!friendRequest.getFriend().getId().equals(targetUser.getId())) {
            throw new RuntimeException("Error: You are not authorized to accept this request.");
        }

        if (friendRequest.getStatus() != FriendStatus.PENDING) {
            throw new RuntimeException("Error: Friend request is not pending.");
        }

        friendRequest.setStatus(FriendStatus.ACCEPTED);
        friendRepository.save(friendRequest);
        
        // Save the reverse direction automatically to make queries easier
        Friend reverseFriend = new Friend();
        reverseFriend.setUser(targetUser);
        reverseFriend.setFriend(friendRequest.getUser());
        reverseFriend.setStatus(FriendStatus.ACCEPTED);
        friendRepository.save(reverseFriend);

        return "Friend request accepted.";
    }

    @Transactional
    public String rejectFriendRequest(User targetUser, Long friendshipId) {
        Friend friendRequest = friendRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Error: Friend request not found."));

        if (!friendRequest.getFriend().getId().equals(targetUser.getId())) {
            throw new RuntimeException("Error: You are not authorized to reject this request.");
        }

        friendRepository.delete(friendRequest);
        return "Friend request rejected and removed.";
    }

    public List<FriendResponse> getFriendsList(User user) {
        List<Friend> friends = friendRepository.findByUserAndStatus(user, FriendStatus.ACCEPTED);
        List<FriendResponse> responseList = new ArrayList<>();
        
        for (Friend f : friends) {
            User friendInfo = f.getFriend();
            responseList.add(new FriendResponse(
                    f.getId(),
                    friendInfo.getUsername(),
                    friendInfo.getAvatar(),
                    friendInfo.getEloRating(),
                    "ACCEPTED"
            ));
        }
        return responseList;
    }

    public List<FriendResponse> getPendingRequests(User user) {
        List<Friend> pendingRequests = friendRepository.findByFriendAndStatus(user, FriendStatus.PENDING);
        List<FriendResponse> responseList = new ArrayList<>();
        
        for (Friend f : pendingRequests) {
            User senderInfo = f.getUser();
            responseList.add(new FriendResponse(
                    f.getId(),
                    senderInfo.getUsername(),
                    senderInfo.getAvatar(),
                    senderInfo.getEloRating(),
                    "PENDING"
            ));
        }
        return responseList;
    }
}
