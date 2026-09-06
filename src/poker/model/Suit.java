package poker.model;

public enum Suit {
    CLUBS("clubs", "♣", "Black"),
    DIAMONDS("diamonds", "♦", "Red"),
    HEARTS("hearts", "♥", "Red"),
    SPADES("spades", "♠", "Black");

    private final String folderName;
    private final String symbol;
    private final String color;

    Suit(String folderName, String symbol, String color) {
        this.folderName = folderName;
        this.symbol = symbol;
        this.color = color;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getColor() {
        return color;
    }
}
