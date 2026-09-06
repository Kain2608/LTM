package poker.model;

import java.util.Objects;

public class Card implements Comparable<Card> {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public String getImageRelativePath() {
        // e.g. "png cards/card fronts/spades/ace of spades.png"
        return "png cards/card fronts/" + suit.getFolderName() + "/" + rank.getFileName() + " of " + suit.getFolderName() + ".png";
    }

    public String getDisplayName() {
        return rank.getSymbol() + suit.getSymbol();
    }

    @Override
    public int compareTo(Card o) {
        return Integer.compare(this.rank.getValue(), o.rank.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
