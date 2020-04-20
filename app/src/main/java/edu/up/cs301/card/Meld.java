package edu.up.cs301.card;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/* Melds

  Implements the list of, and methods to find, melds of cards.

  Alex Mak, Justin Lee
  April 2020
 */
public enum Meld implements Serializable {

    DOUBLE_RUN("Double Run",  "Two runs", 1500, 1),
    RUN_MARRIAGE("Run + Marraige",  "Run with another trump king and queen", 230, 1),
    RUN_KING("Run + King",  "Run with another trump king", 190, 1),
    RUN_QUEEN("Run + Queen",  "Run with another trump queen", 190, 1),
    RUN("Run",  "Trump jack, queen, king, ten, ace", 150, 1),
    ROYAL_MARRIAGE("Royal Marriage",  "Trump king and queen", 40, 1),
    COMMON_MARRIAGE("Common Marriage",  "King and queen not in trump suit", 20, 1),
    DIX("Dix",  "Trump nine", 10, 1),
    DOUBLE_PINOCHLE("Double Pinochle",  "All jacks of diamonds and queen of spades", 300, 2),
    PINOCHLE("Pinochle",  "One jack of diamonds and one queen of spades", 40, 2),
    ACES_ABOUND("Aces Abound",  "All eight aces", 1000, 3),
    KINGS_ABOUND("Kings Abound",  "All eight kings", 800, 3),
    QUEENS_ABOUND("Queens Abound",  "All eight queens", 600, 3),
    JACKS_ABOUND("Jacks Abound",  "All eight jacks", 400, 3),
    ACES_AROUND("Aces Around",  "One ace of each suit", 100, 3),
    KINGS_AROUND("Kings Around",  "One king of each suit", 80, 3),
    QUEENS_AROUND("Queens Around",  "One queen of each suit", 60, 3),
    JACKS_AROUND("Jacks Around",  "One jack of each suit", 40, 3);

    // to satisfy Serializable interface
    private static final long serialVersionUID = 8673272710125325L;

    private final String NAME;
    private final String DESCRIPTION;
    private final int POINTS;
    private final int TYPE;

    Meld(String name, String description, int points, int type) {
        this.NAME = name;
        this.DESCRIPTION = description;
        this.POINTS = points;
        this.TYPE = type;
    }

    public String getName() {
        // Returns the english name for the meld
        return NAME;
    }

    public String getDescription() {
        // Returns the description of the meld.
        return DESCRIPTION;
    }

    public int getPoints() {
        // Returns the amount of points a meld is worth
        return POINTS;
    }

    public int getType() {
        // Return whether the meld is type 1, 2, or 3.
        return TYPE;
    }

    private static boolean isValidMeld(ArrayList<Card> cards, Card[] required) {
        // Given a hand, and meld requirements, returns whether hand is sufficient to make the meld
        boolean valid = true;
        ArrayList<Card> temp = (ArrayList<Card>) cards.clone();
        for (Card card : required) {
            // For each of the required cards, invalidate if one is missing.
            if (!temp.contains(card)) {
                valid = false;
                break;
            } else {
                temp.remove(card);
            }
        }
        if (valid) {
            for (Card card : required) {
                cards.remove(card);
            }
        }
        return valid;
    }

    public static ArrayList<Meld> checkMelds(Deck deck, Suit trump) {
        ArrayList<Meld> melds = new ArrayList<>();
        // List of found melds.
        ArrayList<Card> class1Deck = new Deck(deck).getCards();
        // Hand
        Card NINE = new Card(Rank.NINE, trump);
        Card TEN = new Card(Rank.TEN, trump);
        Card JACK = new Card(Rank.JACK, trump);
        Card QUEEN = new Card(Rank.QUEEN, trump);
        Card KING = new Card(Rank.KING, trump);
        Card ACE = new Card(Rank.ACE, trump);
        for (int i = 0; i < Meld.values().length ; i++) {
            // Iterating over the indeces of all possible melds:
            Meld meld = Meld.values()[i];
            if (meld.getType() != 1) continue;
            Card[] required;

            switch (meld) {
                case RUN:
                    required = new Card[]{TEN, JACK, QUEEN, KING, ACE};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case RUN_KING:
                    required = new Card[]{TEN, JACK, QUEEN, KING, ACE, new Card(KING)};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case RUN_QUEEN:
                    required = new Card[]{TEN, JACK, QUEEN, KING, ACE, new Card(QUEEN)};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case RUN_MARRIAGE:
                    required = new Card[]{TEN, JACK, QUEEN, KING, ACE, new Card(KING), new Card(QUEEN)};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case DOUBLE_RUN:
                    required = new Card[]{TEN, JACK, QUEEN, KING, ACE, new Card(TEN), new Card(JACK), new Card(QUEEN), new Card(KING), new Card(ACE)};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case DIX:
                    required = new Card[]{NINE};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case ROYAL_MARRIAGE:
                    required = new Card[]{KING, QUEEN};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case COMMON_MARRIAGE:
                    for (Suit suit : Suit.values()) {
                        if (suit.equals(trump)) continue;
                        Card king = new Card(Rank.KING, suit);
                        Card queen = new Card(Rank.QUEEN, suit);
                        if (class1Deck.contains(king) && class1Deck.contains(queen)) {
                            class1Deck.remove(king);
                            class1Deck.remove(queen);
                            melds.add(meld);
                            break;
                        }
                    }
                    break;
            }

        }
        ArrayList<Card> class2Deck = new Deck(deck).getCards();
        for (int i = 0; i < Meld.values().length ; i++) {
            Meld meld = Meld.values()[i];
            if (meld.getType() != 2) continue;
            Card[] required;
            switch (meld) {
                case PINOCHLE:
                    required = new Card[]{new Card(Rank.JACK, Suit.Diamond), new Card(Rank.QUEEN, Suit.Spade)};
                    if (isValidMeld(class2Deck, required)) melds.add(meld);
                    break;
                case DOUBLE_PINOCHLE:
                    required = new Card[]{new Card(Rank.JACK, Suit.Diamond), new Card(Rank.JACK, Suit.Diamond), new Card(Rank.QUEEN, Suit.Spade), new Card(Rank.QUEEN, Suit.Spade)};
                    if (isValidMeld(class2Deck, required)) melds.add(meld);
                    break;
            }
        }
        ArrayList<Card> class3Deck = new Deck(deck).getCards();
        for (int i = 0; i < Meld.values().length ; i++) {
            Meld meld = Meld.values()[i];
            if (meld.getType() != 3) continue;
            Card[] required;
            switch (meld) {
                case ACES_AROUND:
                    required = new Card[]{new Card(Rank.ACE, Suit.Heart), new Card(Rank.ACE, Suit.Spade), new Card(Rank.ACE, Suit.Club), new Card(Rank.ACE, Suit.Diamond)};
                    if (isValidMeld(class3Deck, required)) melds.add(meld);
                    break;
                case ACES_ABOUND:
                    required = new Card[]{new Card(Rank.ACE, Suit.Heart), new Card(Rank.ACE, Suit.Spade), new Card(Rank.ACE, Suit.Club), new Card(Rank.ACE, Suit.Diamond),
                                          new Card(Rank.ACE, Suit.Heart), new Card(Rank.ACE, Suit.Spade), new Card(Rank.ACE, Suit.Club), new Card(Rank.ACE, Suit.Diamond)};
                    if (isValidMeld(class3Deck, required)) melds.add(meld);
                    break;
                case KINGS_AROUND:
                    required = new Card[]{new Card(Rank.KING, Suit.Heart), new Card(Rank.KING, Suit.Spade), new Card(Rank.KING, Suit.Club), new Card(Rank.KING, Suit.Diamond)};
                    if (isValidMeld(class3Deck, required)) melds.add(meld);
                    break;
                case KINGS_ABOUND:
                    required = new Card[]{new Card(Rank.KING, Suit.Heart), new Card(Rank.KING, Suit.Spade), new Card(Rank.KING, Suit.Club), new Card(Rank.KING, Suit.Diamond),
                            new Card(Rank.KING, Suit.Heart), new Card(Rank.KING, Suit.Spade), new Card(Rank.KING, Suit.Club), new Card(Rank.KING, Suit.Diamond)};
                    if (isValidMeld(class3Deck, required)) melds.add(meld);
                    break;
                case QUEENS_AROUND:
                    required = new Card[]{new Card(Rank.QUEEN, Suit.Heart), new Card(Rank.QUEEN, Suit.Spade), new Card(Rank.QUEEN, Suit.Club), new Card(Rank.QUEEN, Suit.Diamond)};
                    if (isValidMeld(class3Deck, required)) melds.add(meld);
                    break;
                case QUEENS_ABOUND:
                    required = new Card[]{new Card(Rank.QUEEN, Suit.Heart), new Card(Rank.QUEEN, Suit.Spade), new Card(Rank.QUEEN, Suit.Club), new Card(Rank.QUEEN, Suit.Diamond),
                            new Card(Rank.QUEEN, Suit.Heart), new Card(Rank.QUEEN, Suit.Spade), new Card(Rank.QUEEN, Suit.Club), new Card(Rank.QUEEN, Suit.Diamond)};
                    if (isValidMeld(class3Deck, required)) melds.add(meld);
                    break;
                case JACKS_AROUND:
                    required = new Card[]{new Card(Rank.JACK, Suit.Heart), new Card(Rank.JACK, Suit.Spade), new Card(Rank.JACK, Suit.Club), new Card(Rank.JACK, Suit.Diamond)};
                    if (isValidMeld(class3Deck, required)) melds.add(meld);
                    break;
                case JACKS_ABOUND:
                    required = new Card[]{new Card(Rank.JACK, Suit.Heart), new Card(Rank.JACK, Suit.Spade), new Card(Rank.JACK, Suit.Club), new Card(Rank.JACK, Suit.Diamond),
                            new Card(Rank.JACK, Suit.Heart), new Card(Rank.JACK, Suit.Spade), new Card(Rank.JACK, Suit.Club), new Card(Rank.JACK, Suit.Diamond)};
                    if (isValidMeld(class3Deck, required)) melds.add(meld);
                    break;
            }
        }
        return melds;
    }

    public static int totalPoints(ArrayList<Meld> melds) {
        int total = 0;
        for (Meld meld : melds) {
            total += meld.getPoints();
        }
        return total;
    }
}
