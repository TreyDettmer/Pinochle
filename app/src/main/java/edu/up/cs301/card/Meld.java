package edu.up.cs301.card;

import java.io.Serializable;
import java.util.ArrayList;

public enum Meld implements Serializable {

    RUN("Run",  "Trump jack, queen, king, ten, ace", 150, "regEX"),
    RUN_KING("Run + King",  "Run with another trump king", 190, "regEX"),
    RUN_QUEEN("Run + Queen",  "Run with another trump queen", 190, "regEX"),
    RUN_MARRIAGE("Run + Marraige",  "Run with another king and queen", 230, "regEX"),
    DOUBLE_RUN("Double Run",  "Two runs", 1500, "regEX"),
    DIX("Dix",  "Trump nine", 10, "regEX"),
    ROYAL_MARRIAGE("Royal Marriage",  "Trump king and queen", 40, "regEX"),
    COMMON_MARRIAGE("Common Marriage",  "King and queen not in trump suit", 20, "regEX"),
    PINOCHLE("Pinochle",  "One jack of diamonds and one queen of spades", 40, "regEX"),
    DOUBLE_PINOCHLE("Double Pinochle",  "All jacks of diamonds and queen of spades", 300, "regEX"),
    ACES_AROUND("Aces Around",  "One ace of each suit", 100, "regEX"),
    ACES_ABOUND("Aces Abound",  "All eight aces", 1000, "regEX"),
    KINGS_AROUND("Kings Around",  "One king of each suit", 80, "regEX"),
    KINGS_ABOUND("Kings Abound",  "All eight kings", 800, "regEX"),
    QUEENS_AROUND("Queens Around",  "One queen of each suit", 60, "regEX"),
    QUEENS_ABOUND("Queens Abound",  "All eight queens", 600, "regEX"),
    JACKS_AROUND("Jacks Around",  "One jack of each suit", 40, "regEX"),
    JACKS_ABOUND("Jacks Abound",  "All eight jacks", 400, "regEX");

    // to satisfy Serializable interface
    private static final long serialVersionUID = 8673272710125325L;

    private final String NAME;
    private final String DESCRIPTION;
    private final int POINTS;
    private final String REGEX;

    Meld(String name, String description, int points, String regEx) {
        this.NAME = name;
        this.DESCRIPTION = description;
        this.POINTS = points;
        this.REGEX = regEx;
    }

    public String getName() {
        return NAME;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public int getPoints() {
        return POINTS;
    }

    public String getRegEx() {
        return REGEX;
    }


    public static ArrayList<Meld> checkMelds(Deck deck, Suit trump) {
        return null;
    }

    public static int totalPoints(ArrayList<Meld> melds) {
        return -1;
    }

}
