package poker.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import poker.model.Player;
import poker.model.PokerGameEngine;

import java.util.ArrayList;
import java.util.List;

public class PokerTableContainer extends BorderPane {
    private final TableAreaNode tableAreaNode = new TableAreaNode();
    private final List<PlayerNode> playerNodes = new ArrayList<>();
    private int heroSeatIndex = 0; // Local Client Player Seat Index (Default 0)

    public PokerTableContainer(List<Player> players) {
        this.setStyle("-fx-background-color: radial-gradient(center 50% 45%, radius 75%, rgba(14, 49, 41, 0.42), rgba(3, 9, 14, 0.72));");
        this.setPadding(new Insets(14, 18, 12, 18));

        // Levels & Flags matching screenshot aesthetic
        String[] levels = {"41", "16", "33", "20", "31", "41"};
        String[] flags = {"🇻🇳", "🇧🇷", "🇬🇧", "🇺🇸", "🇳🇱", "🇩🇪"};

        // Set player stacks
        if (players.size() >= 6) {
            players.get(0).setChips(101000);
            players.get(1).setChips(23010);
            players.get(2).setChips(10168);
            players.get(3).setChips(16420);
            players.get(4).setChips(17000);
            players.get(5).setChips(8000);
        }

        // Create Player Nodes with initial visual seat positions
        int nSeats = players.size();
        for (int i = 0; i < nSeats; i++) {
            Player p = players.get(i);
            p.setSeatIndex(i); // Assign logical seat index
            String lvl = i < levels.length ? levels[i] : "10";
            String flg = i < flags.length ? flags[i] : "🌐";
            int initialVisualSeat = (i - heroSeatIndex + nSeats) % nSeats;
            PlayerNode pNode = new PlayerNode(p, initialVisualSeat, lvl, flg);
            playerNodes.add(pNode);
        }

        buildLayout();
    }

    public void setHeroSeatIndex(int newHeroSeatIndex) {
        this.heroSeatIndex = newHeroSeatIndex;
        int nSeats = playerNodes.size();
        for (int i = 0; i < nSeats; i++) {
            int visualSeat = (i - heroSeatIndex + nSeats) % nSeats;
            playerNodes.get(i).setVisualSeatPosition(visualSeat);
        }
        buildLayout();
    }

    public int getHeroSeatIndex() {
        return heroSeatIndex;
    }

    private void buildLayout() {
        this.getChildren().clear();
        this.setCenter(tableAreaNode);

        int nSeats = playerNodes.size();
        PlayerNode[] visualSlots = new PlayerNode[Math.max(6, nSeats)];

        for (int i = 0; i < nSeats; i++) {
            int vIdx = (i - heroSeatIndex + nSeats) % nSeats;
            visualSlots[vIdx] = playerNodes.get(i);
        }

        // Top Row: Visual Seat 2 (Top Left), Visual Seat 3 (Top Center), Visual Seat 4 (Top Right)
        HBox topRow = new HBox(70, visualSlots[2], visualSlots[3], visualSlots[4]);
        topRow.setAlignment(Pos.CENTER);
        topRow.setPadding(new Insets(0, 0, 5, 0));
        this.setTop(topRow);

        // Bottom Row: Visual Seat 1 (Bottom Left), Visual Seat 0 (Hero Bottom Center), Visual Seat 5 (Bottom Right)
        HBox bottomRow = new HBox(80, visualSlots[1], visualSlots[0], visualSlots[5]);
        bottomRow.setAlignment(Pos.CENTER);
        bottomRow.setPadding(new Insets(5, 0, 0, 0));
        this.setBottom(bottomRow);
    }

    public void updateView(PokerGameEngine engine) {
        tableAreaNode.updateTable(engine, heroSeatIndex);

        int currentTurn = engine.getCurrentTurnIndex();
        boolean isShowdown = (engine.getCurrentPhase() == PokerGameEngine.GamePhase.SHOWDOWN);

        for (int i = 0; i < playerNodes.size(); i++) {
            PlayerNode pNode = playerNodes.get(i);
            boolean isTurn = (i == currentTurn && !isShowdown);
            pNode.updateView(isTurn, isShowdown, heroSeatIndex);
            if (!isShowdown) {
                pNode.hideWin();
            }
        }
    }

    public void triggerShowdownWin(List<Player> winners, int potAmount) {
        for (PlayerNode pNode : playerNodes) {
            if (winners.contains(pNode.getPlayer())) {
                pNode.showWin(potAmount / winners.size());
            }
        }
    }

    public void triggerEmojiOnHero(String emoji) {
        if (heroSeatIndex >= 0 && heroSeatIndex < playerNodes.size()) {
            playerNodes.get(heroSeatIndex).triggerEmoji(emoji);
        }
    }

    public TableAreaNode getTableAreaNode() {
        return tableAreaNode;
    }

    public List<PlayerNode> getPlayerNodes() {
        return playerNodes;
    }
}
