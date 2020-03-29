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

    private int phase;  // Number of the phase the game is on.
    private int turn;   // Number of player whose turn it is.
    private int[] scoreboard;   // Array of player scores.
    private int firstBidder;    // Player number of the first bidder.
    private int[] bids; // Bids of each player.
    private boolean[] passed;   // Which players have passed.
    private int wonBid; // Number of player who won the bid.
    private Suit trumpSuit; // Trump suit.
    private ArrayList<Meld>[] melds;    // Melds of each player.

    private Deck[] allPlayerDecks;  // Decks of each player.
    private Deck mainDeck;  // Deck of all cards.
    private Deck centerDeck;    // Deck in the center of play.
    private Deck[] teamTricksDeck;  // Trick decks of each team.
    private int lastTrick;  // Number of the team that won the last trick.

    // Constructor:
    public PinochleGameState() {
        scoreboard = new int[NUM_TEAMS];
        firstBidder = -1;
        bids = new int[NUM_PLAYERS];
        passed = new boolean[NUM_PLAYERS];
        melds = new ArrayList[NUM_PLAYERS];
        Arrays.fill(melds, new ArrayList<Meld>());
        allPlayerDecks = new Deck[NUM_PLAYERS];
        mainDeck = new Deck();
        centerDeck = new Deck();
        teamTricksDeck = new Deck[NUM_PLAYERS];
        lastTrick = -1;


        // initialize the main deck and deal
    }

    // Copy Constructor:
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
        allPlayerDecks = gameState.getAllPlayerDecks();
        mainDeck = gameState.getMainDeck();
        centerDeck = gameState.getCenterDeck();
        teamTricksDeck = gameState.getAllTricksDecks();
        lastTrick = gameState.getLastTrick();
    }

    /**
     * Returns whether the player is valid.
     *
     * @param player the number for the player.
     * @return true if the player is valid, false if otherwise.
     */
    public boolean isValidPlayer(int player) {
        if (player < NUM_PLAYERS) {
            return true;
        } else {
            Log.d("PinochleGameState", "Invalid player");
            return false;
        }
    }

    /**
     * Returns whether the team is valid.
     *
     * @param team the number for the team.
     * @return true if the team is vaid, false if otherwise.
     */
    public boolean isValidTeam(int team) {
        if (team < NUM_TEAMS) {
            return true;
        } else {
            Log.d("PinochleGameState", "Invalid team");
            return false;
        }
    }

    /**
     * Updates the phase of the game.
     */
    public void nextPhase() {
        phase = (phase + 1) % NUM_PHASES;
    }

    /**
     * Updates the whose turn it is.
     */
    public void nextPlayerTurn() {
        turn = (turn + 1) % NUM_PLAYERS;
    }

    /**
     * Sets the turn to that of a specific player.
     *
     * @param player the number of the player.
     */
    public void setPlayerTurn(int player) {
        if (isValidPlayer(player)) {
            turn = player;
        } else {
            Log.d("PinochleGameState", "Invalid player");
        }
    }

    /**
     * Returns the number of the player's teammate.
     *
     * @param player the number of the player.
     * @return the teammates player number.
     */
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

    /**
     * Returns whether the player is a teammate to a specified one.
     *
     * @param player the number of the player.
     * @return true if the player is a teammate to the specified one,
     *         otherwise false.
     */
    public boolean isSecondTeammate(int player) {
        return player >= NUM_PLAYERS / NUM_TEAMS;
    }

    /**
     * Adds points to a team's score.
     *
     * @param team the number of the team.
     * @param score the points added to the team's score.
     */
    public void addScore(int team, int score) {
        if (team < scoreboard.length) {
            scoreboard[team] += score;
        }
    }

    /**
     * Removes points from a team's score.
     *
     * @param team the number of the team.
     * @param score the points removed from the team's score.
     */
    public void subtractScore(int team, int score) {
        if (team < scoreboard.length) {
            scoreboard[team] -= score;
        }
    }

    /**
     * Sets the player to be the first bidder.
     *
     * @param player the number of the player.
     */
    public void setFirstBidder(int player) {
        if (isValidPlayer(player)) {
            firstBidder = player;
        }
    }

    /**
     * Sets the bid of the player.
     *
     * @param player the number of the player.
     * @param bid the bid of the player.
     */
    public void setBid(int player, int bid) {
        if (isValidPlayer(player)) {
            bids[player] = bid;
        }
    }

    /**
     * Adds to the bid of the player.
     *
     * @param player the number of the player.
     * @param bid the amount to raise the bid.
     */
    public void addBid(int player, int bid) {
        if (isValidPlayer(player)) {
            bids[player] += bid;
        }
    }

    /**
     * Denotes if a player has passed.
     *
     * @param player the number of the player.
     */
    public void setPassed(int player) {
        if (isValidPlayer(player)) {
            passed[player] = true;
        }
    }

    /**
     * Denotes the player that won the bid.
     *
     * @param player the number of the player.
     */
    public void setWonBid(int player) {
        if (isValidPlayer(player)) {
            passed[player] = true;
        }
    }

    /**
     * Sets the suit of the trump suit.
     *
     * @param trump the selected suit.
     */
    public void setTrumpSuit(Suit trump) {
        trumpSuit = trump;
    }

    /**
     * Sets the melds a player has.
     *
     * @param player the number of the player.
     * @param melds the melds a player has in their hand.
     */
    public void setPlayerMelds(int player, ArrayList<Meld> melds) {
        if (isValidPlayer(player)) {
            this.melds[player] = (ArrayList<Meld>) melds.clone();
        }
    }

    /**
     * Adds a specific card to a player's deck.
     *
     * @param player the number of the player.
     * @param card the card to be added.
     */
    public void addCardToPlayer(int player, Card card) {
        if (isValidPlayer(player)) {
            allPlayerDecks[player].add(card);
        }
    }

    /**
     * Removes a specific card from a player's deck.
     *
     * @param player the number of the player.
     * @param card the card to be removed.
     */
    public void removeCardFromPlayer(int player, Card card) {
        if (isValidPlayer(player)) {
            allPlayerDecks[player].remove(card);
        }
    }

    /**
     * Returns the cards that have been dealt and removes them
     * from the main deck.
     *
     * @return an array of cards that are dealt.
     */
    public Card[] dealCards() {
        Card[] deal = new Card[DEAL_CARDS];
        for (int i = 0; i < deal.length; i++) {
            deal[i] = mainDeck.removeTopCard();
        }
        return deal;
    }

    /**
     * Adds the specific card to the center deck.
     *
     * @param card the card to be played.
     */
    public void addCardToCenter(Card card) {
        centerDeck.add(card);
    }

    /**
     * Clears the center deck.
     *
     * @return an empty array of cards.
     */
    public Card[] removeAllCardsFromCenter() {
        return centerDeck.clear();
    }

    /**
     * Adds cards from the trick to team's trick deck.
     *
     * @param team the number of the team.
     * @param trick the cards of the trick won.
     */
    public void addTrickToTeam(int team, Card[] trick) {
        if (isValidTeam(team)) {
            teamTricksDeck[team].add(trick);
        }
    }

    /**
     * Denotes the team that won the last trick.
     *
     * @param team the number of the team.
     */
    public void setLastTrick(int team) {
        if (isValidTeam(team)) {
            lastTrick = team;
        }
    }

    /**
     * Resets the Game State.
     */
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
        allPlayerDecks = new Deck[NUM_PLAYERS];
        mainDeck = new Deck();
        centerDeck = new Deck();
        teamTricksDeck = new Deck[NUM_TEAMS];
        lastTrick = -1;
    }

    /**
     * Returns the team the player is on.
     *
     * @param player the number of the player.
     * @return the number of the team.
     */
    public int getTeam(int player) {
        if (player <= 1) return 0;
        else return 1;
    }

    /**
     * Returns the player whose turn it is.
     *
     * @return the number of the player whose turn it is.
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Returns the phase of the game.
     *
     * @return the number of the phase.
     */
    public int getPhase() {
        return phase;
    }

    /**
     * Returns the scoreboard.
     *
     * @return the scoreboard array.
     */
    public int[] getScoreboard() {
        return scoreboard.clone();
    }

    /**
     * Returns the first bidder.
     *
     * @return the number of the first bidder.
     */
    public int getFirstBidder() {
        return firstBidder;
    }

    /**
     * Returns the player bids.
     *
     * @return the array of player bids.
     */
    public int[] getBids() {
        return bids.clone();
    }

    /**
     * Returns who has passed.
     *
     * @return the array of players that have passed.
     */
    public boolean[] getPassed() {
        return passed.clone();
    }

    /**
     * Returns who won the bid.
     *
     * @return the number of the player that won the bid.
     */
    public int getWonBid() {
        return wonBid;
    }

    /**
     * Returns the trump suit.
     *
     * @return the trump suit.
     */
    public Suit getTrumpSuit() {
        return trumpSuit;
    }

    public ArrayList<Meld>[] getMelds() {
        return melds.clone();
    }

    /**
     * Returns all the player decks.
     *
     * @return the array of all player decks.
     */
    private Deck[] getAllPlayerDecks() {
        Deck[] clone = new Deck[allPlayerDecks.length];
        for (int i = 0; i < allPlayerDecks.length; i++) {
            clone[i] = new Deck(allPlayerDecks[i]);
        }
        return clone;
    }

    /**
     * Returns the deck of the player.
     *
     * @param player the number of the player.
     * @return the deck of the player.
     */
    public Deck getPlayerDeck(int player) {
        if (isValidPlayer(player))
            return new Deck(allPlayerDecks[player]);
        else return null;
    }

    /**
     * Returns the main deck.
     *
     * @return the main deck.
     */
    public Deck getMainDeck() {
        return new Deck(mainDeck);
    }

    /**
     * Returns the center deck.
     *
     * @return the center deck.
     */
    public Deck getCenterDeck() {
        return new Deck(centerDeck);
    }

    /**
     * Returns all the tricks decks.
     *
     * @return the array of Decks for team's tricks Deck.
     */
    private Deck[] getAllTricksDecks(){
        Deck[] clone = new Deck[teamTricksDeck.length];
        for (int i = 0; i < teamTricksDeck.length; i++) {
            clone[i] = new Deck(teamTricksDeck[i]);
        }
        return clone;
    }

    /**
     * Returns the tricks deck of the team.
     *
     * @param team the number of the team.
     * @return the tricks deck of that team.
     */
    public Deck getTeamTricksDeck(int team) {
        if (isValidTeam(team))
            return new Deck(teamTricksDeck[team]);
        else return null;
    }

    /**
     * Returns the team that won the last trick.
     *
     * @return the number of the team.
     */
    public int getLastTrick() {
        return lastTrick;
    }
}
