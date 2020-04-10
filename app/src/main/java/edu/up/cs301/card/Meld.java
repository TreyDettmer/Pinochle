package edu.up.cs301.card;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public enum Meld implements Serializable {

    RUN("Run",  "Trump jack, queen, king, ten, ace", 150, "(,[DHCS]T)(,[DHCS]J)+(,[DHCS]Q)+(,[DHCS]K)+(,[DHCS]A)+"),
    RUN_KING("Run + King",  "Run with another trump king", 190, "(,[DHCS]T)(,[DHCS]J)+(,[DHCS]Q)+(,[DHCS]K){2,}(,[DHCS]A)+"),
    RUN_QUEEN("Run + Queen",  "Run with another trump queen", 190, "(,[DHCS]T)(,[DHCS]J)+(,[DHCS]Q){2,}(,[DHCS]K)+(,[DHCS]A)+"),
    RUN_MARRIAGE("Run + Marraige",  "Run with another trump king and queen", 230, "(,[DHCS]T)(,[DHCS]J)+(,[DHCS]Q){2,}(,[DHCS]K){2,}(,[DHCS]A)+"),
    DOUBLE_RUN("Double Run",  "Two runs", 1500, "(,[DHCS]T){2,}(,[DHCS]J){2,}(,[DHCS]Q){2,}(,[DHCS]K){2,}(,[DHCS]A){2,}"),
    DIX("Dix",  "Trump nine", 10, "([DHCS]N"),
    ROYAL_MARRIAGE("Royal Marriage",  "Trump king and queen", 40, "(,[DHCS]Q)+(,[DHCS]K)+"),
    COMMON_MARRIAGE("Common Marriage",  "King and queen not in trump suit", 20, "(,[dhcs]Q)+(,[dhcs]K)+"),
    PINOCHLE("Pinochle",  "One jack of diamonds and one queen of spades", 40, ",[dD][jJ],[sS][qQ]"),
    DOUBLE_PINOCHLE("Double Pinochle",  "All jacks of diamonds and queen of spades", 300, ",[dD]J,[dD]J,[sS]Q,[sS]Q"),
    ACES_AROUND("Aces Around",  "One ace of each suit", 100, "(?=.*[dD]A)(?=.*[hH]A)(?=.*[cC]A)(?=.*[sS]A)"),
    ACES_ABOUND("Aces Abound",  "All eight aces", 1000, "(.*,.A){8}"),
    KINGS_AROUND("Kings Around",  "One king of each suit", 80, "(?=.*[dD]K)(?=.*[hH]K)(?=.*[cC]K)(?=.*[sS]K)"),
    KINGS_ABOUND("Kings Abound",  "All eight kings", 800, "(.*,.K){8}"),
    QUEENS_AROUND("Queens Around",  "One queen of each suit", 60, "(?=.*[dD]Q)(?=.*[hH]Q)(?=.*[cC]Q)(?=.*[sS]Q)"),
    QUEENS_ABOUND("Queens Abound",  "All eight queens", 600, "(.*,.Q){8}"),
    JACKS_AROUND("Jacks Around",  "One jack of each suit", 40, "(?=.*[dD]J)(?=.*[hH]J)(?=.*[cC]J)(?=.*[sS]J)"),
    JACKS_ABOUND("Jacks Abound",  "All eight jacks", 400, "(.*,.J){8}");

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


    public static ArrayList<Meld> checkMelds(Deck deck, Suit trump, int type) {
        // 9 T J Q K A
        // 9 A J K Q T
        // C D H S

        String trumpChar = "" + trump.getShortName();
        String[] simplifiedDeckArr = deck.toString().split(" ");
        List<String> list = new ArrayList<>();

        //HashMap<String, Integer> helperMap = new HashMap<>();
        for (String str : simplifiedDeckArr) {
            String rank = str.substring(0, 1);
            String suit = str.substring(1);
            if (suit.equals(trumpChar)) {
                list.add(suit + rank);
            } else {
                list.add(suit.toLowerCase() + rank);
            }
            /*
            if (helperMap.containsKey(str)) {
                helperMap.put(str, helperMap.get(str) + 1);
            } else {
                helperMap.put(str, 1);
            }*/
        }
        Collections.sort(list);
        String newSimplifiedDeck = "";
        for (String s : list) newSimplifiedDeck += "," + s;
        // Special flags
        //while (jd-- > 0) newSimplifiedDeck += "@";
        //while (qs-- > 0) newSimplifiedDeck += "#";
        ArrayList<Meld> toReturn = new ArrayList<>();
        List<Meld> potential;
        switch (type) {
            case 1:
                potential = new ArrayList(Arrays.asList(Meld.RUN, Meld.RUN_KING, Meld.RUN_QUEEN, Meld.RUN_MARRIAGE, Meld.ROYAL_MARRIAGE, Meld.COMMON_MARRIAGE, Meld.DIX));
                for (Meld m : potential) {
                    if (Pattern.matches(m.REGEX, newSimplifiedDeck)) toReturn.add(m);
                }
                break;

            case 2:
                potential = new ArrayList(Arrays.asList(Meld.PINOCHLE, Meld.DOUBLE_PINOCHLE));
                for (Meld m : potential) {
                    if (Pattern.matches(m.REGEX, newSimplifiedDeck)) toReturn.add(m);
                }
                break;

            case 3:
                potential = new ArrayList(Arrays.asList(Meld.JACKS_ABOUND, Meld.JACKS_AROUND, Meld.QUEENS_ABOUND, Meld.QUEENS_AROUND, Meld.ACES_ABOUND, Meld.ACES_AROUND, Meld.KINGS_ABOUND, Meld.KINGS_AROUND));
                for (Meld m : potential) {
                    if (Pattern.matches(m.REGEX, newSimplifiedDeck)) toReturn.add(m);
                }
                break;
        }
        return toReturn;
    }

    public static int totalPoints(ArrayList<Meld> melds) {
        return -1;
    }  

}
