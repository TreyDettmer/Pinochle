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

/**
 * Enum for all Melds in Pinochle
 *
 * @author Steven R. Vegdahl
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public enum Meld implements Serializable {
    //All of the different melds
    //Ordering matters because checkMelds checks in ascending order, with the most points in each
    //class (type) first so that if a player has for instance a Double Pinochle, they would get
    //a Double Pinochle instead of a Pinochle.
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

    //instance variables
    private final String NAME;
    private final String DESCRIPTION;
    private final int POINTS;
    private final int TYPE;

    /**
     * Constructor
     *
     * @param name name of the meld
     * @param description description of the meld
     * @param points points for the meld
     * @param type type of the meld
     */
    Meld(String name, String description, int points, int type) {
        this.NAME = name;
        this.DESCRIPTION = description;
        this.POINTS = points;
        this.TYPE = type;
    }

    /**
     * Gets the name of the meld
     *
     * @return name of the meld
     */
    public String getName() {
        return NAME;
    }

    /**
     * Gets the description of the meld
     *
     * @return description of the meld
     */
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Gets the points for the meld
     *
     * @return points for the meld
     */
    public int getPoints() {
        return POINTS;
    }

    /**
     * Gets the type of the meld
     *
     * @return type of the meld
     */
    public int getType() {
        return TYPE;
    }

    /**
     * Checks if given array of cards is a valid meld
     *
     * @param cards cards to iterate through
     * @param required cards that must be in cards in order for meld to be valid
     * @return whether the given array of cards is a valid meld
     */
    private static boolean isValidMeld(ArrayList<Card> cards, Card[] required) {
        boolean valid = true;
        ArrayList<Card> temp = (ArrayList<Card>) cards.clone();
        //If the deck contains a card that is required for the meld
        //it can no longer be used again for that particular meld.
        //Ex. we wouldn't get a double pinochle when we really had a pinochle.
        for (Card card : required) {
            if (!temp.contains(card)) {
                valid = false;
                break;
            } else {
                temp.remove(card);
            }
        }
        //If the meld is valid, we remove all the cards from the deck, so the
        //cards cannot be reused within the same type
        if (valid) {
            for (Card card : required) {
                cards.remove(card);
            }
        }
        return valid;
    }

    /**
     * Checks the melds in a deck
     *
     * @param deck the deck to check
     * @param trump the current trump suit
     * @return melds that the deck contains
     */
    public static ArrayList<Meld> checkMelds(Deck deck, Suit trump) {
        ArrayList<Meld> melds = new ArrayList<>();
        //Make clones of our deck so that cards may be reused across different types
        //Type 1 deck clone
        ArrayList<Card> class1Deck = new Deck(deck).getCards();
        for (int i = 0; i < Meld.values().length ; i++) {
            Meld meld = Meld.values()[i];
            //Must be a type 1 meld
            if (meld.getType() != 1) continue;
            Card[] required;
            switch (meld) {
                case RUN:
                    required = new Card[]{new Card(Rank.TEN, trump), new Card(Rank.JACK, trump), new Card(Rank.QUEEN, trump), new Card(Rank.KING, trump), new Card(Rank.ACE, trump)};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case RUN_KING:
                    required = new Card[]{new Card(Rank.TEN, trump), new Card(Rank.JACK, trump), new Card(Rank.QUEEN, trump), new Card(Rank.KING, trump), new Card(Rank.ACE, trump), new Card(Rank.KING, trump)};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case RUN_QUEEN:
                    required = new Card[]{new Card(Rank.TEN, trump), new Card(Rank.JACK, trump), new Card(Rank.QUEEN, trump), new Card(Rank.KING, trump), new Card(Rank.ACE, trump), new Card(Rank.QUEEN, trump)};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case RUN_MARRIAGE:
                    required = new Card[]{new Card(Rank.TEN, trump), new Card(Rank.JACK, trump), new Card(Rank.QUEEN, trump), new Card(Rank.KING, trump), new Card(Rank.ACE, trump), new Card(Rank.KING, trump), new Card(Rank.QUEEN, trump)};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case DOUBLE_RUN:
                    required = new Card[]{new Card(Rank.TEN, trump), new Card(Rank.JACK, trump), new Card(Rank.QUEEN, trump), new Card(Rank.KING, trump), new Card(Rank.ACE, trump), new Card(Rank.TEN, trump), new Card(Rank.JACK, trump), new Card(Rank.QUEEN, trump), new Card(Rank.KING, trump), new Card(Rank.ACE, trump)};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case DIX:
                    required = new Card[]{new Card(Rank.NINE, trump)};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case ROYAL_MARRIAGE:
                    required = new Card[]{new Card(Rank.KING, trump), new Card(Rank.QUEEN, trump)};
                    if (isValidMeld(class1Deck, required)) melds.add(meld);
                    break;
                case COMMON_MARRIAGE:
                    //Common marriage does not have a specific set of required cards
                    //Instead we a apply a general rule of a king and queen in a non trump suit
                    for (Suit suit : Suit.values()) {
                        if (suit.equals(trump)) continue;
                        //Iterate through all three possibilities
                        Card king = new Card(Rank.KING, suit);
                        Card queen = new Card(Rank.QUEEN, suit);
                        if (class1Deck.contains(king) && class1Deck.contains(queen)) {
                            //Remove the cards so that cards cannot be reused within the type
                            class1Deck.remove(king);
                            class1Deck.remove(queen);
                            melds.add(meld);
                            break;
                        }
                    }
                    break;
            }

        }
        //Type 2 deck clone
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
        //Type 3 deck clone
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

    /**
     * calculates the total points of an array of melds
     * @param melds melds whose point values are summed
     * @return the total points
     */
    public static int totalPoints(ArrayList<Meld> melds) {
        int total = 0;
        for (Meld meld : melds) {
            total += meld.getPoints();
        }
        return total;
    }
}
