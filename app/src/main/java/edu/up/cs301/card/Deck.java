package edu.up.cs301.card;


import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Deck class - represents a deck of cards (not necessarily a full one)
 *
 * @author Steven R. Vegdahl
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 *
 */
public class Deck implements Serializable {

    // to satisfy Serializable interface
    private static final long serialVersionUID = 3216223171210121485L;

    // the cards in our deck; the last card in the ArrayList is the top card
    // in the deck
    private ArrayList<Card> cards;

    /**
     * constructor, creating an empty deck
     */
    public Deck() {
        cards = new ArrayList<Card>();
    }

    /** copy constructor, making an exact copy of a deck
     *
     * @param orig
     * 		the deck from which the copy should be made
     */
    public Deck(Deck orig) {
        // synchronize to ensure that original is not being modified as we
        // iterate over it
        synchronized(orig.cards) {
            // create a new arrayList for our new deck; add each card in it
            cards = new ArrayList<Card>();
            for (Card c: orig.cards) {
                cards.add(c);
            }
        }
    }

    /**
     * adds one of each card, increasing the size of the deck by 52. Cards are added
     * spades first (King to Ace), then similarly with hearts, diamonds and clubs.
     *
     * @return
     * 		the deck
     */
    public Deck reset() {
        // add the cards
        synchronized(this.cards) {
            for (char s : "SHDC".toCharArray()) {
                for (char r : "9TJQKA".toCharArray()) {
                    this.add(Card.fromString(""+r+s));
                    this.add(Card.fromString(""+r+s));
                }
            }
        }

        // return the deck
        return this;
    }

    /**
     * Returns the cards in the deck.
     *
     * @return an array list of the cards in the deck.
     */
    public ArrayList<Card> getCards() {
        return (ArrayList<Card>) cards.clone();
    }

    /**
     *
     */
    public Deck shuffle() {
        // synchronize so that we don't have someone trying to modify the
        // deck as we're modifying it
        synchronized (this.cards) {
            // go through a loop that randomly rearranges the cards
            for (int i = cards.size(); i > 1; i--) {
                int spot = (int)(i*Math.random());
                Card temp = cards.get(spot);
                cards.set(spot, cards.get(i-1));
                cards.set(i-1, temp);
            }
        }

        // return the deck
        return this;
    }

    /**
     * Sorts the deck based on ranking order from lowest to highest.
     */
    public void sort() {
        // Temporary ArrayList to store the cards before updating the deck.
        ArrayList<Card> temp = new ArrayList<>();

        // Converts the deck ArrayList to an array.
        Object[] cardArray = cards.toArray();

        // Sorts the deck array.
        Arrays.sort(cardArray);

        /*
         * For loop used to convert the deck array back to
         * an ArrayList form. Adds each element in the array
         * to the temp ArrayList.
         */
        for (int i = 0; i < cardArray.length; i++) {
            temp.add((Card) cardArray[i]);
        }
        // Sets the cards of the temp ArrayList to the cards of the deck.
        cards = temp;
    }

    /**
     * Moves the top card the current deck to the top of another; does nothing if
     * the first deck is empty
     *
     * @param targetDeck
     * 		the deck to which the card should be moved
     */
    public void moveTopCardTo(Deck targetDeck) {

        // will hold the card
        Card c = null;

        // size of the first deck
        int size;

        // indivisibly check the deck for empty, and remove the card, to
        // avoid a race condition
        synchronized(this.cards) {
            size = this.size();
            if (size > 0) {
                c = cards.remove(cards.size()-1);
            }
        }

        // if the original size was non-zero, add the card to the top of the
        // target deck
        if (size > 0) {
            targetDeck.add(c);
        }
    }

    /**
     * move all cards in the current deck to a another deck by repeated moving
     * a single card from top to top
     *
     * @param target
     * 		the deck that will get the cards
     */
    public void moveAllCardsTo(Deck target) {
        // if the source and target are the same, ignore
        if (this == target) {
            return;
        }

        // keep moving cards until the current deck is empty
        while (size() > 0) {
            moveTopCardTo(target);
        }
    }

    /**
     * add a card or cards to the top of a deck
     *
     * @param cards
     * 		the card to add
     */
    public void add(Card... cards) {
        // synchronize so that the underlying ArrayList is not accessed
        // inconsistently
        synchronized(this.cards) {
            for (Card c : cards) {
                this.cards.add(c);
            }
        }
    }



    /**
     * remove cards
     *
     * @param card
     * 		the cards to remove
     */
    public void remove(Card... card) {
        // synchronize so that the underlying ArrayList is not accessed
        // inconsistently
        synchronized(this.cards) {
            for (Card c : card) {
                if (cards.indexOf(c) != -1) {
                    cards.remove(c);
                }
            }
        }
    }

    /**
     * clear the deck
     *
     */
    public Card[] clear() {
        // synchronize so that the underlying ArrayList is not accessed
        // inconsistently
        synchronized(this.cards) {
            Card[] clone = cards.toArray(new Card[0]);
            cards.clear();
            return clone;
        }
    }

    /**
     * @return
     * 		the number of cards in the deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * replace each element in the deck with a null card; does not change
     * the size of the deck, but rather causes the deck to yield null for
     * when accessed with a valid index. A null card might represent that
     * there is a card in that position, but that it is face-down.
     */
    public void nullifyDeck() {
        // synchronize so that we don't get any race conditions (e.g., with
        // shuffle()
        synchronized (this.cards) {
            // null out each card
            for (int i = 0; i < cards.size(); i++) {
                cards.set(i, null);
            }
        }
    }

    /**
     * remove the top card from the deck
     *
     * @return
     * 		the top card in the deck, which is also removed,
     * 		or null if the deck was empty
     */
    public Card removeTopCard() {
        synchronized (this.cards) {
            if (cards.isEmpty()) return null;
            return cards.remove(cards.size()-1);
        }
    }

    /**
     * @return
     * 		the top card in the deck, without removing it; null
     * 		if the deck was empty
     */
    public Card peekAtTopCard() {
        synchronized (this.cards) {
            if (cards.isEmpty()) return null;
            return cards.get(cards.size()-1);
        }
    }

    /**
     * creates a printable version of the object, a list
     * of two-character names for each card in the deck
     * (starting at the bottom of the deck), surrounded by
     * brackets
     *
     * @return
     * 		a printable version of the deck
     */
    @Override
    public String toString() {
        // the eventual return value
        String rtnVal = "";

        // synchronize to avoid iterating while the
        // deck is being modified
        synchronized (this.cards) {
            // loop through, printing the short name of each
            // card (using '--' for null)
            for (Card c : cards) {
                if (c == null) {
                    rtnVal += " --";
                }
                else {
                    rtnVal += " " + c.getShortName();
                }
            }
        }

        // surround by brackets and retuirn
        rtnVal = "[" + rtnVal + " ]";
        return rtnVal;
    }
}

