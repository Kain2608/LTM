package poker.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public enum PlayerStatus {
        ACTIVE,
        FOLDED,
        ALL_IN,
        OUT
    }

    public enum PlayerRole {
        NONE,
        DEALER,
        SMALL_BLIND,
        BIG_BLIND
    }

    private final int id;
    private final String name;
    private final String avatarFileName;
    private final boolean isHuman;
    
    private int seatIndex;
    private int chips;
    private int currentBet;
    private int totalBetInHand;
    private PlayerStatus status;
    private PlayerRole role;
    private String lastAction;
    private final List<Card> holeCards = new ArrayList<>();
    private HandEvaluator.HandResult bestHand;

    public Player(int id, String name, String avatarFileName, int initialChips, boolean isHuman) {
        this.id = id;
        this.name = name;
        this.avatarFileName = avatarFileName;
        this.chips = initialChips;
        this.isHuman = isHuman;
        this.status = PlayerStatus.ACTIVE;
        this.role = PlayerRole.NONE;
        this.lastAction = "";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatarFileName() {
        return avatarFileName;
    }

    public int getSeatIndex() {
        return seatIndex;
    }

    public void setSeatIndex(int seatIndex) {
        this.seatIndex = seatIndex;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public void addChips(int amount) {
        this.chips += amount;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public int getTotalBetInHand() {
        return totalBetInHand;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public PlayerRole getRole() {
        return role;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public List<Card> getHoleCards() {
        return holeCards;
    }

    public HandEvaluator.HandResult getBestHand() {
        return bestHand;
    }

    public void setBestHand(HandEvaluator.HandResult bestHand) {
        this.bestHand = bestHand;
    }

    public void addCard(Card card) {
        holeCards.add(card);
    }

    public void clearHand() {
        holeCards.clear();
        currentBet = 0;
        totalBetInHand = 0;
        lastAction = "";
        bestHand = null;
        if (chips > 0) {
            status = PlayerStatus.ACTIVE;
        } else {
            status = PlayerStatus.OUT;
        }
    }

    public int placeBet(int amount) {
        int actualBet = Math.min(amount, chips);
        chips -= actualBet;
        currentBet += actualBet;
        totalBetInHand += actualBet;
        if (chips == 0) {
            status = PlayerStatus.ALL_IN;
        }
        return actualBet;
    }

    public void resetRoundBet() {
        currentBet = 0;
    }
}
