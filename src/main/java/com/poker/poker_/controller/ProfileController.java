package com.poker.poker_.controller;

import com.poker.poker_.entity.User;

import com.poker.poker_.payload.response.MessageResponse;
import com.poker.poker_.payload.response.UserProfileResponse;
import com.poker.poker_.security.services.UserDetailsImpl;
import com.poker.poker_.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Optional<User> userOptional = userService.findByUsername(userDetails.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return ResponseEntity.ok(new UserProfileResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getAvatar(),
                    user.getChips(),
                    user.getEloRating()
            ));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found."));
    }

}
