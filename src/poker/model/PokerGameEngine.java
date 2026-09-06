package poker.model;

import java.util.*;

public class PokerGameEngine {
    public enum GamePhase {
        IDLE("Press Deal to start hand"),
        PRE_FLOP("Pre-Flop Betting"),
        FLOP("The Flop"),
        TURN("The Turn"),
        RIVER("The River"),
        SHOWDOWN("Showdown");

        private final String displayName;

        GamePhase(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public interface GameEventListener {
        void onGameStateChanged();
        void onMessageLogged(String message, String category);
        void onPlayerAction(Player player, String actionDescription);
        void onShowdown(List<Player> winners, int amountWon);
    }

    private final List<Player> players = new ArrayList<>();
    private final List<Card> communityCards = new ArrayList<>();
    private final Deck deck = new Deck();

    private GamePhase currentPhase = GamePhase.IDLE;
    private int pot = 0;
    private int highestBet = 0;
    private int minRaise = 20;

    private int dealerIndex = 0;
    private int currentTurnIndex = 0;
    private int smallBlind = 10;
    private int bigBlind = 20;
    private int handCount = 0;

    private final List<GameEventListener> listeners = new ArrayList<>();

    public PokerGameEngine() {
        initDefaultPlayers();
    }

    private void initDefaultPlayers() {
        players.add(new Player(0, "Alex (You)", "avatar_player_1.png", 1000, true));
        players.add(new Player(1, "Sophia", "avatar_player_2.png", 1000, false));
        players.add(new Player(2, "Marcus", "avatar_player_3.png", 1000, false));
        players.add(new Player(3, "Elena", "avatar_player_4.png", 1000, false));
        players.add(new Player(4, "David", "avatar_player_5.png", 1000, false));
        players.add(new Player(5, "Lucas", "avatar_player_6.png", 1000, false));
    }

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public int getPot() {
        return pot;
    }

    public int getHighestBet() {
        return highestBet;
    }

    public int getMinRaise() {
        return minRaise;
    }

    public int getSmallBlind() {
        return smallBlind;
    }

    public int getBigBlind() {
        return bigBlind;
    }

    public int getHandCount() {
        return handCount;
    }

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public Player getCurrentPlayer() {
        if (currentTurnIndex >= 0 && currentTurnIndex < players.size()) {
            return players.get(currentTurnIndex);
        }
        return null;
    }

    public void startNewHand() {
        handCount++;
        pot = 0;
        highestBet = 0;
        minRaise = bigBlind;
        communityCards.clear();
        deck.reset();

        // Reset player roles and hands
        for (Player p : players) {
            p.clearHand();
            p.setRole(Player.PlayerRole.NONE);
        }

        // Filter active players who have chips
        List<Player> activeWithChips = getActivePlayersWithChips();
        if (activeWithChips.size() < 2) {
            logMessage("Not enough players with chips to start game! Resetting chips.", "warning");
            for (Player p : players) {
                p.setChips(1000);
                p.setStatus(Player.PlayerStatus.ACTIVE);
            }
        }

        // Rotate dealer
        dealerIndex = (dealerIndex + 1) % players.size();
        while (players.get(dealerIndex).getChips() <= 0) {
            dealerIndex = (dealerIndex + 1) % players.size();
        }

        int sbIndex = getNextActiveIndex(dealerIndex);
        int bbIndex = getNextActiveIndex(sbIndex);

        players.get(dealerIndex).setRole(Player.PlayerRole.DEALER);
        players.get(sbIndex).setRole(Player.PlayerRole.SMALL_BLIND);
        players.get(bbIndex).setRole(Player.PlayerRole.BIG_BLIND);

        logMessage("--- Starting Hand #" + handCount + " ---", "system");
        logMessage("Dealer: " + players.get(dealerIndex).getName(), "info");

        // Post blinds
        int sbPosted = players.get(sbIndex).placeBet(smallBlind);
        int bbPosted = players.get(bbIndex).placeBet(bigBlind);
        pot += sbPosted + bbPosted;
        highestBet = bigBlind;

        logMessage(players.get(sbIndex).getName() + " posts SB $" + sbPosted, "blind");
        logMessage(players.get(bbIndex).getName() + " posts BB $" + bbPosted, "blind");

        // Deal 2 hole cards to each player
        for (int i = 0; i < 2; i++) {
            for (Player p : players) {
                if (p.getStatus() != Player.PlayerStatus.OUT) {
                    p.addCard(deck.dealCard());
                }
            }
        }

        currentPhase = GamePhase.PRE_FLOP;
        currentTurnIndex = getNextActiveIndex(bbIndex);

        notifyStateChanged();
        processTurnIfNeeded();
    }

    public void processHumanAction(String actionType, int raiseAmount) {
        Player human = players.get(0);
        if (currentTurnIndex != 0 || currentPhase == GamePhase.IDLE || currentPhase == GamePhase.SHOWDOWN) {
            return;
        }

        executePlayerAction(human, actionType, raiseAmount);
    }

    private void executePlayerAction(Player player, String actionType, int raiseAmount) {
        int callAmount = highestBet - player.getCurrentBet();

        switch (actionType.toUpperCase()) {
            case "FOLD":
                player.setStatus(Player.PlayerStatus.FOLDED);
                player.setLastAction("FOLD");
                logMessage(player.getName() + " Folds", "fold");
                break;

            case "CHECK":
                if (callAmount > 0) {
                    // Cannot check if there's a bet, auto convert to CALL or FOLD
                    executePlayerAction(player, "CALL", 0);
                    return;
                }
                player.setLastAction("CHECK");
                logMessage(player.getName() + " Checks", "check");
                break;

            case "CALL":
                int actualCall = player.placeBet(callAmount);
                pot += actualCall;
                player.setLastAction(actualCall == 0 ? "CHECK" : "CALL $" + actualCall);
                logMessage(player.getName() + " Calls $" + actualCall, "call");
                break;

            case "RAISE":
            case "BET":
                int targetTotalBet = highestBet + raiseAmount;
                int needed = targetTotalBet - player.getCurrentBet();
                int actualRaised = player.placeBet(needed);
                pot += actualRaised;
                highestBet = player.getCurrentBet();
                minRaise = Math.max(bigBlind, raiseAmount);
                if (player.getStatus() == Player.PlayerStatus.ALL_IN) {
                    player.setLastAction("ALL-IN $" + actualRaised);
                    logMessage(player.getName() + " goes ALL-IN for $" + actualRaised, "raise");
                } else {
                    player.setLastAction("RAISE $" + highestBet);
                    logMessage(player.getName() + " Raises to $" + highestBet, "raise");
                }
                break;
        }

        notifyAction(player, player.getLastAction());
        advanceTurn();
    }

    private void advanceTurn() {
        if (checkRoundComplete()) {
            advancePhase();
        } else {
            currentTurnIndex = getNextActiveIndex(currentTurnIndex);
            notifyStateChanged();
            processTurnIfNeeded();
        }
    }

    private void processTurnIfNeeded() {
        if (currentPhase == GamePhase.IDLE || currentPhase == GamePhase.SHOWDOWN) return;

        Player p = getCurrentPlayer();
        if (p == null) {
            advancePhase();
            return;
        }

        if (!p.isHuman()) {
            // AI Turn
            simulateAiTurn(p);
        }
    }

    private void simulateAiTurn(Player ai) {
        int callAmt = highestBet - ai.getCurrentBet();
        HandEvaluator.HandResult currentEval = HandEvaluator.evaluate(ai.getHoleCards(), communityCards);
        HandEvaluator.HandType handType = currentEval.getHandType();

        String chosenAction;
        int raiseAmt = 0;

        Random rand = new Random();
        int roll = rand.nextInt(100);

        if (callAmt == 0) {
            // Can check or bet
            if (handType.getRank() >= HandEvaluator.HandType.ONE_PAIR.getRank() && roll < 50) {
                chosenAction = "BET";
                raiseAmt = Math.max(bigBlind, (int) (pot * 0.5));
            } else if (roll < 15) {
                // Bluff
                chosenAction = "BET";
                raiseAmt = bigBlind;
            } else {
                chosenAction = "CHECK";
            }
        } else {
            // Facing bet
            if (callAmt > ai.getChips()) {
                // All in or fold
                if (handType.getRank() >= HandEvaluator.HandType.TWO_PAIR.getRank() || roll < 40) {
                    chosenAction = "CALL";
                } else {
                    chosenAction = "FOLD";
                }
            } else if (handType.getRank() >= HandEvaluator.HandType.THREE_OF_A_KIND.getRank()) {
                if (roll < 60) {
                    chosenAction = "RAISE";
                    raiseAmt = Math.max(minRaise, (int) (pot * 0.6));
                } else {
                    chosenAction = "CALL";
                }
            } else if (handType.getRank() >= HandEvaluator.HandType.ONE_PAIR.getRank() || callAmt <= bigBlind * 2) {
                if (roll < 80) {
                    chosenAction = "CALL";
                } else {
                    chosenAction = "FOLD";
                }
            } else {
                if (roll < 20 && callAmt < pot * 0.3) {
                    chosenAction = "CALL";
                } else {
                    chosenAction = "FOLD";
                }
            }
        }

        executePlayerAction(ai, chosenAction, raiseAmt);
    }

    private boolean checkRoundComplete() {
        List<Player> active = getActivePlayersInHand();
        if (active.size() <= 1) {
            return true;
        }

        // Count how many can act
        int playersWhoCanAct = 0;
        for (Player p : active) {
            if (p.getStatus() == Player.PlayerStatus.ACTIVE) {
                playersWhoCanAct++;
            }
        }

        if (playersWhoCanAct <= 1) {
            // All-in or only one active player left
            boolean allMatched = true;
            for (Player p : active) {
                if (p.getCurrentBet() < highestBet && p.getChips() > 0) {
                    allMatched = false;
                    break;
                }
            }
            if (allMatched) return true;
        }

        // Standard round check: everyone matched highest bet and had a turn
        for (Player p : active) {
            if (p.getStatus() == Player.PlayerStatus.ACTIVE) {
                if (p.getCurrentBet() < highestBet || p.getLastAction().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void advancePhase() {
        // Reset current bets for all players
        for (Player p : players) {
            p.resetRoundBet();
        }
        highestBet = 0;

        List<Player> active = getActivePlayersInHand();
        if (active.size() <= 1) {
            handleSingleWinner(active.isEmpty() ? null : active.get(0));
            return;
        }

        switch (currentPhase) {
            case PRE_FLOP:
                currentPhase = GamePhase.FLOP;
                communityCards.add(deck.dealCard());
                communityCards.add(deck.dealCard());
                communityCards.add(deck.dealCard());
                logMessage("--- The Flop: " + getCardsString(communityCards) + " ---", "phase");
                break;

            case FLOP:
                currentPhase = GamePhase.TURN;
                communityCards.add(deck.dealCard());
                logMessage("--- The Turn: " + communityCards.get(3).getDisplayName() + " ---", "phase");
                break;

            case TURN:
                currentPhase = GamePhase.RIVER;
                communityCards.add(deck.dealCard());
                logMessage("--- The River: " + communityCards.get(4).getDisplayName() + " ---", "phase");
                break;

            case RIVER:
                handleShowdown();
                return;

            default:
                return;
        }

        currentTurnIndex = getNextActiveIndex(dealerIndex);
        notifyStateChanged();
        processTurnIfNeeded();
    }

    private void handleSingleWinner(Player winner) {
        currentPhase = GamePhase.SHOWDOWN;
        if (winner != null) {
            winner.addChips(pot);
            logMessage("🎉 " + winner.getName() + " wins $" + pot + " (Everyone else folded)", "win");
            notifyShowdown(Collections.singletonList(winner), pot);
        }
        notifyStateChanged();
    }

    private void handleShowdown() {
        currentPhase = GamePhase.SHOWDOWN;
        logMessage("--- Showdown! ---", "phase");

        List<Player> active = getActivePlayersInHand();
        HandEvaluator.HandResult bestResult = null;
        List<Player> winners = new ArrayList<>();

        for (Player p : active) {
            HandEvaluator.HandResult res = HandEvaluator.evaluate(p.getHoleCards(), communityCards);
            p.setBestHand(res);
            logMessage(p.getName() + " shows [" + getCardsString(p.getHoleCards()) + "] -> " + res.getName(), "showdown");

            if (bestResult == null) {
                bestResult = res;
                winners.add(p);
            } else {
                int cmp = res.compareTo(bestResult);
                if (cmp > 0) {
                    bestResult = res;
                    winners.clear();
                    winners.add(p);
                } else if (cmp == 0) {
                    winners.add(p);
                }
            }
        }

        int share = pot / winners.size();
        for (Player w : winners) {
            w.addChips(share);
        }

        if (winners.size() == 1) {
            logMessage("🏆 " + winners.get(0).getName() + " wins $" + pot + " with " + bestResult.getName() + "!", "win");
        } else {
            logMessage("🤝 Split pot! " + winners.size() + " players share $" + pot + " each ($" + share + ") with " + bestResult.getName(), "win");
        }

        notifyShowdown(winners, pot);
        notifyStateChanged();
    }

    private List<Player> getActivePlayersInHand() {
        List<Player> list = new ArrayList<>();
        for (Player p : players) {
            if (p.getStatus() == Player.PlayerStatus.ACTIVE || p.getStatus() == Player.PlayerStatus.ALL_IN) {
                list.add(p);
            }
        }
        return list;
    }

    private List<Player> getActivePlayersWithChips() {
        List<Player> list = new ArrayList<>();
        for (Player p : players) {
            if (p.getChips() > 0) list.add(p);
        }
        return list;
    }

    private int getNextActiveIndex(int startIndex) {
        int idx = (startIndex + 1) % players.size();
        int count = 0;
        while (count < players.size()) {
            Player p = players.get(idx);
            if (p.getStatus() == Player.PlayerStatus.ACTIVE) {
                return idx;
            }
            idx = (idx + 1) % players.size();
            count++;
        }
        return startIndex;
    }

    private String getCardsString(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            sb.append(cards.get(i).getDisplayName());
            if (i < cards.size() - 1) sb.append(" ");
        }
        return sb.toString();
    }

    private void logMessage(String msg, String cat) {
        for (GameEventListener l : listeners) {
            l.onMessageLogged(msg, cat);
        }
    }

    private void notifyStateChanged() {
        for (GameEventListener l : listeners) {
            l.onGameStateChanged();
        }
    }

    private void notifyAction(Player p, String desc) {
        for (GameEventListener l : listeners) {
            l.onPlayerAction(p, desc);
        }
    }

    private void notifyShowdown(List<Player> winners, int potAmt) {
        for (GameEventListener l : listeners) {
            l.onShowdown(winners, potAmt);
        }
    }
}
