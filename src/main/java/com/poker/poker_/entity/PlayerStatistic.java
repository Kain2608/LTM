package com.poker.poker_.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatistic {
    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "games_played")
    private Integer gamesPlayed = 0;

    @Column(name = "games_won")
    private Integer gamesWon = 0;

    @Column(name = "games_lost")
    private Integer gamesLost = 0;

    @Column(name = "total_chip_won")
    private Long totalChipWon = 0L;

    @Column(name = "total_chip_lost")
    private Long totalChipLost = 0L;

    @Column(name = "biggest_win")
    private Long biggestWin = 0L;

    @Column(name = "biggest_pot_won")
    private Long biggestPotWon = 0L;

    @Column(name = "total_play_time_seconds")
    private Long totalPlayTimeSeconds = 0L;
}
