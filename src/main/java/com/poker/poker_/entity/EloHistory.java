package com.poker.poker_.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "elo_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EloHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "elo_before", nullable = false)
    private Integer eloBefore;

    @Column(name = "elo_after", nullable = false)
    private Integer eloAfter;

    @Column(name = "elo_change", nullable = false)
    private Integer eloChange;

    @Column(length = 255)
    private String reason = "GAME_RESULT";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
