package edu.up.cs301.pinochle;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Meld;
import edu.up.cs301.card.Suit;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;

/*
 * Game State
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleGameState extends GameState {
    private static final long serialVersionUID = 545884920868735343L;
    private static final int NUM_PHASES = 6;
    private static final int NUM_TEAMS = 2;
    private static final int NUM_PLAYERS = 4;
    private static final int DEAL_CARDS = 12;


    private int phase;
    private int turn;
    private int[] scoreboard;
    private int firstBidder;
    private int[] bids;
    private boolean[] passed;
    private int wonBid;
    private Suit trumpSuit;
    private ArrayList<Meld>[] melds;

    private Deck[] playerDeck;
    private Deck mainDeck;
    private Deck centerDeck;
    private Deck[] teamTricksDeck;
    private int lastTrick;

    public PinochleGameState() {
        scoreboard = new int[NUM_TEAMS];
        firstBidder = -1;
        bids = new int[NUM_PLAYERS];
        passed = new boolean[NUM_PLAYERS];
        melds = new ArrayList[NUM_PLAYERS];
        Arrays.fill(melds, new ArrayList<Meld>());
        playerDeck = new Deck[NUM_PLAYERS];
        mainDeck = new Deck();
        centerDeck = new Deck();
        teamTricksDeck = new Deck[NUM_PLAYERS];
        lastTrick = -1;
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

    public boolean isValidPlayer(int player) {
        if (player < NUM_PLAYERS) {
            return true;
        } else {
            Log.d("PinochleGameState", "Invalid player");
            return false;
        }
    }

    public boolean isValidTeam(int team) {
        if (team < NUM_TEAMS) {
            return true;
        } else {
            Log.d("PinochleGameState", "Invalid team");
            return false;
        }
    }

    public void nextPhase() {
        phase = (phase + 1) % NUM_PHASES;
    }

    public void nextPlayerTurn() {
        turn = (turn + 1) % NUM_PLAYERS;
    }

    public void setPlayerTurn(int player) {
        if (isValidPlayer(player)) {
            turn = player;
        } else {
            Log.d("PinochleGameState", "Invalid player");
        }
    }

    public int getTeammate(int player) {
        if (isValidPlayer(player)) {
            if (player < (NUM_PLAYERS / NUM_TEAMS)) {
                return player + 2;
            } else {
                return player - 2;
            }
        } else {
            Log.d("PinochleGameState", "Invalid player");
            return -1;
        }
    }

    public boolean isSecondTeammate(int player) {
        return player >= NUM_PLAYERS / NUM_TEAMS;
    }

    public void addScore(int team, int score) {
        if (team < scoreboard.length) {
            scoreboard[team] += score;
        }
    }

    public void subtractScore(int team, int score) {
        if (team < scoreboard.length) {
            scoreboard[team] -= score;
        }
    }

    public void setFirstBidder(int player) {
        if (isValidPlayer(player)) {
            firstBidder = player;
        }
    }

    public void setBid(int player, int bid) {
        if (isValidPlayer(player)) {
            bids[player] = bid;
        }
    }

    public void addBid(int player, int bid) {
        if (isValidPlayer(player)) {
            bids[player] += bid;
        }
    }

    public void setPassed(int player) {
        if (isValidPlayer(player)) {
            passed[player] = true;
        }

    }

    public void setWonBid(int player) {
        if (isValidPlayer(player)) {
            passed[player] = true;
        }
    }

    public void setTrumpSuit(Suit trump) {
        trumpSuit = trump;
    }

    public void setPlayerMelds(int player, ArrayList<Meld> melds) {
        if (isValidPlayer(player)) {
            this.melds[player] = (ArrayList<Meld>) melds.clone();
        }
    }

    public void addCardToPlayer(int player, Card card) {
        if (isValidPlayer(player)) {
            playerDeck[player].add(card);
        }
    }


    public void removeCardFromPlayer(int player, Card card) {
        if (isValidPlayer(player)) {
            playerDeck[player].remove(card);
        }
    }

    public Card[] dealCards() {
        Card[] deal = new Card[DEAL_CARDS];
        for (int i = 0; i < deal.length; i++) {
            deal[i] = mainDeck.removeTopCard();
        }
        return deal;
    }

    public void addCardToCenter(Card card) {
        centerDeck.add(card);
    }

    public Card[] removeAllCardsFromCenter() {
        return centerDeck.clear();
    }

    public void addTrickToTeam(int team, Card[] trick) {
        if (isValidTeam(team)) {
            teamTricksDeck[team].add(trick);
        }
    }

    public void setLastTrick(int team) {
        if (isValidTeam(team)) {
            lastTrick = team;
        }
    }

    public void reset() {
        phase = 0;
        turn = 0;
        scoreboard = new int[NUM_TEAMS];
        firstBidder = 0;
        bids = new int[NUM_PLAYERS];
        passed = new boolean[NUM_PLAYERS];
        wonBid = -1;
        trumpSuit = null;
        melds = new ArrayList[NUM_PLAYERS];
        Arrays.fill(melds, new ArrayList<Meld>());
        playerDeck = new Deck[NUM_PLAYERS];
        mainDeck = new Deck();
        centerDeck = new Deck();
        teamTricksDeck = new Deck[NUM_PLAYERS];
        lastTrick = -1;
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

    public boolean[] getPassed() {
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
