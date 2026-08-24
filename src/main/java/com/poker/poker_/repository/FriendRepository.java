package com.poker.poker_.repository;

import com.poker.poker_.entity.Friend;
import com.poker.poker_.entity.FriendStatus;
import com.poker.poker_.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByUserAndFriend(User user, User friend);
    List<Friend> findByUserAndStatus(User user, FriendStatus status);
    List<Friend> findByFriendAndStatus(User friend, FriendStatus status);
}
