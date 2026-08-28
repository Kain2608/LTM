package com.poker.model;

import java.sql.Timestamp;

public class MatchHistory {
    private Long gameId;
    private Long roomId;
    private Long potAmount;
    private Timestamp startedAt;
    private Timestamp endedAt;
    private Integer seatNumber;
    private Long startingChips;
    private Long endingChips;
    private Long chipsChange;
    private String result;

    public MatchHistory() {}

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getPotAmount() { return potAmount; }
    public void setPotAmount(Long potAmount) { this.potAmount = potAmount; }
    public Timestamp getStartedAt() { return startedAt; }
    public void setStartedAt(Timestamp startedAt) { this.startedAt = startedAt; }
    public Timestamp getEndedAt() { return endedAt; }
    public void setEndedAt(Timestamp endedAt) { this.endedAt = endedAt; }
    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }
    public Long getStartingChips() { return startingChips; }
    public void setStartingChips(Long startingChips) { this.startingChips = startingChips; }
    public Long getEndingChips() { return endingChips; }
    public void setEndingChips(Long endingChips) { this.endingChips = endingChips; }
    public Long getChipsChange() { return chipsChange; }
    public void setChipsChange(Long chipsChange) { this.chipsChange = chipsChange; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}
