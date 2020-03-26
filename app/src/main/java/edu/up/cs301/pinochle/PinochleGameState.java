package edu.up.cs301.pinochle;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Meld;
import edu.up.cs301.card.Suit;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;

/*
 * Description
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleGameState extends GameState {
    private static final long serialVersionUID = 545884920868735343L;

    private int phase;
    private int turn;
    private int[] scoreboard;
    private int firstBidder;
    private int[] bids;
    private int[] passed;
    private int wonBid;
    private Suit trumpSuit;
    private ArrayList<Meld>[] melds;

    private Deck[] playerDeck;
    private Deck mainDeck;
    private Deck centerDeck;
    private Deck[] teamTricksDeck;
    private int lastTrick;

    public PinochleGameState() {
        scoreboard = new int[2];
        bids = new int[4];
        passed = new int[4];
        melds = new ArrayList[4];
        for (int i = 0; i < 4; i++) {
            melds[i] = new ArrayList<Meld>();
        }
        playerDeck = new Deck[4];
        teamTricksDeck = new Deck[4];
    }

    public PinochleGameState(PinochleGameState gameState) {
        phase = gameState.getPhase();
        turn = gameState.getTurn();
        scoreboard = gameState.getScoreboard();
        firstBidder = gameState.getFirstBidder();
        bids = gameState.getBids();
        passed = gameState.getPassed();
        wonBid = gameState.getWonBid();
        trumpSuit = gameState.getTrumpSuit();
        melds = gameState.getMelds();
        playerDeck = gameState.getPlayerDeck();
        mainDeck = gameState.getMainDeck();
        centerDeck = gameState.getCenterDeck();
        teamTricksDeck = gameState.getTeamTricksDeck();
        lastTrick = gameState.getLastTrick();
    }

    public int getTeam(int player) {
        if (player <= 1) return 0;
        else return 1;
    }

    public int getTurn() {
        return turn;
    }

    public int getPhase() {
        return phase;
    }

    public int[] getScoreboard() {
        return scoreboard.clone();
    }

    public int getFirstBidder() {
        return firstBidder;
    }

    public int[] getBids() {
        return bids.clone();
    }

    public int[] getPassed() {
        return passed.clone();
    }

    public int getWonBid() {
        return wonBid;
    }

    public Suit getTrumpSuit() {
        return trumpSuit;
    }

    public ArrayList<Meld>[] getMelds() {
        return melds.clone();
    }

    public Deck[] getPlayerDeck() {
        Deck[] clone = new Deck[playerDeck.length];
        for (int i = 0; i < playerDeck.length; i++) {
            clone[i] = new Deck(playerDeck[i]);
        }
        return clone;
    }

    public Deck getMainDeck() {
        return new Deck(mainDeck);
    }

    public Deck getCenterDeck() {
        return new Deck(centerDeck);
    }

    public Deck[] getTeamTricksDeck() {
        Deck[] clone = new Deck[teamTricksDeck.length];
        for (int i = 0; i < teamTricksDeck.length; i++) {
            clone[i] = new Deck(teamTricksDeck[i]);
        }
        return clone;
    }

    public int getLastTrick() {
        return lastTrick;
    }
}
