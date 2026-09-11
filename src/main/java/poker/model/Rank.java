package poker.model;

public enum Rank {
    TWO(2, "2", "2"),
    THREE(3, "3", "3"),
    FOUR(4, "4", "4"),
    FIVE(5, "5", "5"),
    SIX(6, "6", "6"),
    SEVEN(7, "7", "7"),
    EIGHT(8, "8", "8"),
    NINE(9, "9", "9"),
    TEN(10, "10", "10"),
    JACK(11, "jack", "J"),
    QUEEN(12, "queen", "Q"),
    KING(13, "king", "K"),
    ACE(14, "ace", "A");

    private final int value;
    private final String fileName;
    private final String symbol;

    Rank(int value, String fileName, String symbol) {
        this.value = value;
        this.fileName = fileName;
        this.symbol = symbol;
    }

    public int getValue() {
        return value;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSymbol() {
        return symbol;
    }
}
