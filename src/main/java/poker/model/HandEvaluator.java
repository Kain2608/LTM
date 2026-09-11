package poker.model;

import java.util.*;
import java.util.stream.Collectors;

public class HandEvaluator {

    public enum HandType {
        HIGH_CARD(1, "High Card"),
        ONE_PAIR(2, "One Pair"),
        TWO_PAIR(3, "Two Pair"),
        THREE_OF_A_KIND(4, "Three of a Kind"),
        STRAIGHT(5, "Straight"),
        FLUSH(6, "Flush"),
        FULL_HOUSE(7, "Full House"),
        FOUR_OF_A_KIND(8, "Four of a Kind"),
        STRAIGHT_FLUSH(9, "Straight Flush"),
        ROYAL_FLUSH(10, "Royal Flush");

        private final int rank;
        private final String name;

        HandType(int rank, String name) {
            this.rank = rank;
            this.name = name;
        }

        public int getRank() {
            return rank;
        }

        public String getName() {
            return name;
        }
    }

    public static class HandResult implements Comparable<HandResult> {
        private final HandType handType;
        private final String name;
        private final List<Integer> tieBreakerValues;
        private final List<Card> bestFiveCards;

        public HandResult(HandType handType, String name, List<Integer> tieBreakerValues, List<Card> bestFiveCards) {
            this.handType = handType;
            this.name = name;
            this.tieBreakerValues = tieBreakerValues;
            this.bestFiveCards = bestFiveCards;
        }

        public HandType getHandType() {
            return handType;
        }

        public String getName() {
            return name;
        }

        public List<Card> getBestFiveCards() {
            return bestFiveCards;
        }

        @Override
        public int compareTo(HandResult o) {
            if (this.handType.getRank() != o.handType.getRank()) {
                return Integer.compare(this.handType.getRank(), o.handType.getRank());
            }
            for (int i = 0; i < Math.min(this.tieBreakerValues.size(), o.tieBreakerValues.size()); i++) {
                int cmp = Integer.compare(this.tieBreakerValues.get(i), o.tieBreakerValues.get(i));
                if (cmp != 0) return cmp;
            }
            return 0;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static HandResult evaluate(List<Card> holeCards, List<Card> communityCards) {
        List<Card> allCards = new ArrayList<>();
        if (holeCards != null) allCards.addAll(holeCards);
        if (communityCards != null) allCards.addAll(communityCards);

        if (allCards.size() < 5) {
            return new HandResult(HandType.HIGH_CARD, "Incomplete Hand", Collections.singletonList(0), allCards);
        }

        List<List<Card>> combinations = generateCombinations(allCards, 5);
        HandResult bestResult = null;

        for (List<Card> combo : combinations) {
            HandResult result = evaluateFiveCards(combo);
            if (bestResult == null || result.compareTo(bestResult) > 0) {
                bestResult = result;
            }
        }

        return bestResult;
    }

    private static List<List<Card>> generateCombinations(List<Card> cards, int k) {
        List<List<Card>> result = new ArrayList<>();
        combineHelper(cards, k, 0, new ArrayList<>(), result);
        return result;
    }

    private static void combineHelper(List<Card> cards, int k, int start, List<Card> current, List<List<Card>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < cards.size(); i++) {
            current.add(cards.get(i));
            combineHelper(cards, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    private static HandResult evaluateFiveCards(List<Card> cards) {
        List<Card> sorted = new ArrayList<>(cards);
        sorted.sort(Collections.reverseOrder());

        boolean isFlush = checkFlush(sorted);
        int straightHighRank = checkStraight(sorted);
        boolean isStraight = straightHighRank > 0;

        Map<Integer, Integer> rankCounts = new HashMap<>();
        for (Card c : sorted) {
            rankCounts.put(c.getRank().getValue(), rankCounts.getOrDefault(c.getRank().getValue(), 0) + 1);
        }

        // Royal / Straight Flush
        if (isFlush && isStraight) {
            if (straightHighRank == 14) {
                return new HandResult(HandType.ROYAL_FLUSH, "Royal Flush", Collections.singletonList(14), sorted);
            } else {
                return new HandResult(HandType.STRAIGHT_FLUSH, "Straight Flush", Collections.singletonList(straightHighRank), sorted);
            }
        }

        // Four of a kind
        List<Integer> fourRanks = getRanksWithCount(rankCounts, 4);
        if (!fourRanks.isEmpty()) {
            int quadVal = fourRanks.get(0);
            int kicker = getKickers(rankCounts, Collections.singletonList(quadVal)).get(0);
            return new HandResult(HandType.FOUR_OF_A_KIND, "Four of a Kind (" + getRankSymbol(quadVal) + "s)",
                    Arrays.asList(quadVal, kicker), sorted);
        }

        // Full House
        List<Integer> threeRanks = getRanksWithCount(rankCounts, 3);
        List<Integer> pairRanks = getRanksWithCount(rankCounts, 2);
        if (!threeRanks.isEmpty() && !pairRanks.isEmpty()) {
            int tVal = threeRanks.get(0);
            int pVal = pairRanks.get(0);
            return new HandResult(HandType.FULL_HOUSE, "Full House (" + getRankSymbol(tVal) + "s full of " + getRankSymbol(pVal) + "s)",
                    Arrays.asList(tVal, pVal), sorted);
        }

        // Flush
        if (isFlush) {
            List<Integer> values = sorted.stream().map(c -> c.getRank().getValue()).collect(Collectors.toList());
            return new HandResult(HandType.FLUSH, "Flush (" + getRankSymbol(values.get(0)) + " High)", values, sorted);
        }

        // Straight
        if (isStraight) {
            return new HandResult(HandType.STRAIGHT, "Straight (" + getRankSymbol(straightHighRank) + " High)",
                    Collections.singletonList(straightHighRank), sorted);
        }

        // Three of a kind
        if (!threeRanks.isEmpty()) {
            int tVal = threeRanks.get(0);
            List<Integer> kickers = getKickers(rankCounts, Collections.singletonList(tVal));
            List<Integer> tieBreakers = new ArrayList<>();
            tieBreakers.add(tVal);
            tieBreakers.addAll(kickers);
            return new HandResult(HandType.THREE_OF_A_KIND, "Three of a Kind (" + getRankSymbol(tVal) + "s)",
                    tieBreakers, sorted);
        }

        // Two Pair
        if (pairRanks.size() >= 2) {
            int p1 = pairRanks.get(0);
            int p2 = pairRanks.get(1);
            List<Integer> kickers = getKickers(rankCounts, Arrays.asList(p1, p2));
            List<Integer> tieBreakers = new ArrayList<>(Arrays.asList(p1, p2));
            tieBreakers.addAll(kickers);
            return new HandResult(HandType.TWO_PAIR, "Two Pair (" + getRankSymbol(p1) + "s & " + getRankSymbol(p2) + "s)",
                    tieBreakers, sorted);
        }

        // One Pair
        if (pairRanks.size() == 1) {
            int p1 = pairRanks.get(0);
            List<Integer> kickers = getKickers(rankCounts, Collections.singletonList(p1));
            List<Integer> tieBreakers = new ArrayList<>(Collections.singletonList(p1));
            tieBreakers.addAll(kickers);
            return new HandResult(HandType.ONE_PAIR, "Pair of " + getRankSymbol(p1) + "s",
                    tieBreakers, sorted);
        }

        // High Card
        List<Integer> kickers = sorted.stream().map(c -> c.getRank().getValue()).collect(Collectors.toList());
        return new HandResult(HandType.HIGH_CARD, "High Card " + getRankSymbol(kickers.get(0)),
                kickers, sorted);
    }

    private static boolean checkFlush(List<Card> cards) {
        Suit s = cards.get(0).getSuit();
        for (Card c : cards) {
            if (c.getSuit() != s) return false;
        }
        return true;
    }

    private static int checkStraight(List<Card> sortedCards) {
        Set<Integer> uniqueRanks = new TreeSet<>(Collections.reverseOrder());
        for (Card c : sortedCards) uniqueRanks.add(c.getRank().getValue());

        List<Integer> ranks = new ArrayList<>(uniqueRanks);
        if (ranks.contains(14)) { // Ace low straight A-2-3-4-5
            ranks.add(1);
        }

        for (int i = 0; i <= ranks.size() - 5; i++) {
            if (ranks.get(i) - ranks.get(i + 4) == 4) {
                return ranks.get(i);
            }
        }
        return -1;
    }

    private static List<Integer> getRanksWithCount(Map<Integer, Integer> counts, int targetCount) {
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == targetCount) {
                result.add(entry.getKey());
            }
        }
        result.sort(Collections.reverseOrder());
        return result;
    }

    private static List<Integer> getKickers(Map<Integer, Integer> counts, List<Integer> exclude) {
        List<Integer> kickers = new ArrayList<>();
        for (Integer rank : counts.keySet()) {
            if (!exclude.contains(rank)) {
                kickers.add(rank);
            }
        }
        kickers.sort(Collections.reverseOrder());
        return kickers;
    }

    private static String getRankSymbol(int rankVal) {
        for (Rank r : Rank.values()) {
            if (r.getValue() == rankVal) return r.getSymbol();
        }
        return String.valueOf(rankVal);
    }
}
