package com.poker.poker_.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_players")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "starting_chips")
    private Long startingChips;

    @Column(name = "ending_chips")
    private Long endingChips;

    @Column(name = "chips_change")
    private Long chipsChange;

    @Column(name = "total_bet")
    private Long totalBet = 0L;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GameResult result;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PlayerStatus status = PlayerStatus.PLAYING;
}
