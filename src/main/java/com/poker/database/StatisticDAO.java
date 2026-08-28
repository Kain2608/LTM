package com.poker.database;

import com.poker.model.MatchHistory;
import com.poker.model.PlayerStatistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatisticDAO {

    public List<MatchHistory> getMatchHistory(Long userId, int limit) {
        String sql = "SELECT g.game_id, g.room_id, g.pot_amount, g.started_at, g.ended_at, " +
                     "gp.seat_number, gp.starting_chips, gp.ending_chips, gp.chips_change, gp.result " +
                     "FROM games g " +
                     "JOIN game_players gp ON g.game_id = gp.game_id " +
                     "WHERE gp.user_id = ? " +
                     "ORDER BY g.started_at DESC LIMIT ?";
        List<MatchHistory> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setLong(1, userId);
            pstmt.setInt(2, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MatchHistory h = new MatchHistory();
                    h.setGameId(rs.getLong("game_id"));
                    h.setRoomId(rs.getLong("room_id"));
                    h.setPotAmount(rs.getLong("pot_amount"));
                    h.setStartedAt(rs.getTimestamp("started_at"));
                    h.setEndedAt(rs.getTimestamp("ended_at"));
                    h.setSeatNumber(rs.getInt("seat_number"));
                    h.setStartingChips(rs.getLong("starting_chips"));
                    h.setEndingChips(rs.getLong("ending_chips"));
                    h.setChipsChange(rs.getLong("chips_change"));
                    h.setResult(rs.getString("result"));
                    list.add(h);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Optional<PlayerStatistic> findByUserId(Long userId) {
        String sql = "SELECT * FROM player_statistics WHERE user_id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStatistic(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<PlayerStatistic> getLeaderboardByWins(int limit) {
        String sql = "SELECT * FROM player_statistics ORDER BY games_won DESC LIMIT ?";
        return getLeaderboard(sql, limit);
    }

    public List<PlayerStatistic> getLeaderboardByBiggestWin(int limit) {
        String sql = "SELECT * FROM player_statistics ORDER BY biggest_win DESC LIMIT ?";
        return getLeaderboard(sql, limit);
    }

    private List<PlayerStatistic> getLeaderboard(String sql, int limit) {
        List<PlayerStatistic> list = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStatistic(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private PlayerStatistic mapResultSetToStatistic(ResultSet rs) throws SQLException {
        PlayerStatistic stat = new PlayerStatistic();
        stat.setUserId(rs.getLong("user_id"));
        stat.setGamesPlayed(rs.getInt("games_played"));
        stat.setGamesWon(rs.getInt("games_won"));
        stat.setGamesLost(rs.getInt("games_lost"));
        stat.setTotalChipWon(rs.getLong("total_chip_won"));
        stat.setTotalChipLost(rs.getLong("total_chip_lost"));
        stat.setBiggestWin(rs.getLong("biggest_win"));
        stat.setBiggestPotWon(rs.getLong("biggest_pot_won"));
        stat.setTotalPlayTimeSeconds(rs.getLong("total_play_time_seconds"));
        return stat;
    }
}
