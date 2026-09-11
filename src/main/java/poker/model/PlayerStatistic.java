package poker.model;

public class PlayerStatistic {
    private Long userId;
    private Integer gamesPlayed;
    private Integer gamesWon;
    private Integer gamesLost;
    private Long totalChipWon;
    private Long totalChipLost;
    private Long biggestWin;
    private Long biggestPotWon;
    private Long totalPlayTimeSeconds;

    public PlayerStatistic() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(Integer gamesPlayed) { this.gamesPlayed = gamesPlayed; }
    public Integer getGamesWon() { return gamesWon; }
    public void setGamesWon(Integer gamesWon) { this.gamesWon = gamesWon; }
    public Integer getGamesLost() { return gamesLost; }
    public void setGamesLost(Integer gamesLost) { this.gamesLost = gamesLost; }
    public Long getTotalChipWon() { return totalChipWon; }
    public void setTotalChipWon(Long totalChipWon) { this.totalChipWon = totalChipWon; }
    public Long getTotalChipLost() { return totalChipLost; }
    public void setTotalChipLost(Long totalChipLost) { this.totalChipLost = totalChipLost; }
    public Long getBiggestWin() { return biggestWin; }
    public void setBiggestWin(Long biggestWin) { this.biggestWin = biggestWin; }
    public Long getBiggestPotWon() { return biggestPotWon; }
    public void setBiggestPotWon(Long biggestPotWon) { this.biggestPotWon = biggestPotWon; }
    public Long getTotalPlayTimeSeconds() { return totalPlayTimeSeconds; }
    public void setTotalPlayTimeSeconds(Long totalPlayTimeSeconds) { this.totalPlayTimeSeconds = totalPlayTimeSeconds; }
}

