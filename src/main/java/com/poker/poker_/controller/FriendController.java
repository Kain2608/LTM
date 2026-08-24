package com.poker.poker_.controller;

import com.poker.poker_.entity.User;
import com.poker.poker_.payload.response.FriendResponse;
import com.poker.poker_.payload.response.MessageResponse;
import com.poker.poker_.security.services.UserDetailsImpl;
import com.poker.poker_.service.FriendService;
import com.poker.poker_.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<User> userOptional = userService.findByUsername(userDetails.getUsername());
        return userOptional.orElseThrow(() -> new RuntimeException("Error: User not found."));
    }

    @PostMapping("/request/{targetUsername}")
    public ResponseEntity<?> sendFriendRequest(@PathVariable String targetUsername) {
        try {
            User currentUser = getCurrentUser();
            String result = friendService.sendFriendRequest(currentUser, targetUsername);
            return ResponseEntity.ok(new MessageResponse(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/accept/{friendshipId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long friendshipId) {
        try {
            User currentUser = getCurrentUser();
            String result = friendService.acceptFriendRequest(currentUser, friendshipId);
            return ResponseEntity.ok(new MessageResponse(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/reject/{friendshipId}")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable Long friendshipId) {
        try {
            User currentUser = getCurrentUser();
            String result = friendService.rejectFriendRequest(currentUser, friendshipId);
            return ResponseEntity.ok(new MessageResponse(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<FriendResponse>> getFriendsList() {
        User currentUser = getCurrentUser();
        List<FriendResponse> friends = friendService.getFriendsList(currentUser);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendResponse>> getPendingRequests() {
        User currentUser = getCurrentUser();
        List<FriendResponse> pendingRequests = friendService.getPendingRequests(currentUser);
        return ResponseEntity.ok(pendingRequests);
    }
}
