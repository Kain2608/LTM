package com.poker.poker_.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "daily_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatistic {
    @Id
    @Column(name = "stat_date")
    private LocalDate statDate;

    @Column(name = "total_new_users")
    private Integer totalNewUsers = 0;

    @Column(name = "total_active_users")
    private Integer totalActiveUsers = 0;

    @Column(name = "total_games_played")
    private Integer totalGamesPlayed = 0;

    @Column(name = "total_rooms_created")
    private Integer totalRoomsCreated = 0;

    @Column(name = "total_chips_circulated")
    private Long totalChipsCirculated = 0L;

    @Column(name = "total_wins")
    private Integer totalWins = 0;
}
