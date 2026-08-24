package com.poker.poker_.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "games", indexes = {
    @Index(name = "idx_game_room", columnList = "room_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "pot_amount")
    private Long potAmount = 0L;

    @Column(name = "rake_amount")
    private Long rakeAmount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GameStatus status = GameStatus.STARTING;

    @Column(name = "actions_log", columnDefinition = "JSON")
    private String actionsLog;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
}
