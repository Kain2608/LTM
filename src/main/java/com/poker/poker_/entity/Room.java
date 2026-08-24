package com.poker.poker_.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "room_name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", length = 20)
    private RoomType type = RoomType.PUBLIC;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "max_players")
    private Integer maxPlayers = 6;

    @Column(name = "small_blind")
    private Long smallBlind = 50L;

    @Column(name = "big_blind")
    private Long bigBlind = 100L;

    @Column(name = "min_buyin")
    private Long minBuyin = 1000L;

    @Column(name = "max_buyin")
    private Long maxBuyin = 10000L;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoomStatus status = RoomStatus.WAITING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
