package edu.up.cs301.pinochle;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Meld;
import edu.up.cs301.card.Suit;
import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.LocalGame;
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

    public static final int NUM_TEAMS = 2;
    public static final int NUM_PLAYERS = 4;
    public static final int DEAL_CARDS = 12;

    private PinochleGamePhase phase;  // The phase the game is on.
    private int turn;   // Number of player whose turn it is.
    private int[] scoreboard;   // Array of player scores.
    private int[] lastTrickRoundPlayed;
    private int firstBidder;    // Player number of the first bidder.
    private int[] bids; // Bids of each player.
    private boolean[] passed;   // Which players have passed.
    private boolean[] voteGoSet;
    private boolean[] voted;
    private int wonBid; // Number of player who won the bid.
    private Suit trumpSuit; // Trump suit.
    private ArrayList<Meld>[] melds;    // Melds of each player.

    private Deck[] allPlayerDecks;  // Decks of each player.
    private Deck mainDeck;  // Deck of all cards.
    private Deck centerDeck;    // Deck in the center of play.
    private Deck[] tricksDeck;  // Trick decks of each team.
    private Card leadTrick;
    private int lastTrick;  // Number of the player that won the last trick.
    private int trickRound;
    private int previousTrickWinner;

    // Constructor:
    public PinochleGameState() {
        reset();
    }

    // Copy Constructor:
    public PinochleGameState(PinochleGameState gameState) {
        phase = gameState.getPhase();
        turn = gameState.getTurn();
        scoreboard = gameState.getScoreboard();
        firstBidder = gameState.getFirstBidder();
        bids = gameState.getBids();
        lastTrickRoundPlayed = gameState.getLastTrickRoundPlayed();
        passed = gameState.getPassed();
        voteGoSet = gameState.getVoteGoSet();
        voted = gameState.getVoted();
        wonBid = gameState.getWonBid();
        trumpSuit = gameState.getTrumpSuit();
        melds = gameState.getMelds();
        allPlayerDecks = gameState.getAllPlayerDecks();
        mainDeck = gameState.getMainDeck();
        centerDeck = gameState.getCenterDeck();
        tricksDeck = gameState.getAllTricksDecks();
        leadTrick = gameState.getLeadTrick();
        lastTrick = gameState.getLastTrick();
        lastTrick = gameState.getLastTrick();
        trickRound = gameState.getTrickRound();
        previousTrickWinner = gameState.getPreviousTrickWinner();
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
        PinochleGamePhase[] phases = PinochleGamePhase.values();
        phase = phases[(phase.ordinal() + 1) % phases.length];
        System.out.println("New phase: " + phase.name());
    }


    public boolean isLastPlayer(int player) {
        return player == NUM_PLAYERS - 1;
    }
    /**
     * Updates the whose turn it is.
     *
     * @return the index of the player
     */
    public int nextPlayerTurn() {
        turn = (turn + 1) % NUM_PLAYERS;
        //System.out.println("New turn: " + turn);
        return turn;
    }

    public int getNextPlayerTurn() {
        return (turn + 1) % NUM_PLAYERS;
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

    public int[] getTeammates(int team) {
        int[] teammates = new int[NUM_PLAYERS/NUM_TEAMS];
        for (int i = 0; i < NUM_PLAYERS/NUM_TEAMS; i++) {
            teammates[i] = team + 2 * i;
        }
        return teammates;
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
            bids[player] = getMaxBid() + bid;
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

    public void setVoteGoSet(int player) {
        if (isValidPlayer(player)) {
            voteGoSet[player] = true;
        }
    }

    public void setVoted(int player) {
        if (isValidPlayer(player)) {
            voted[player] = true;
        }
    }

    /**
     * Denotes the player that won the bid.
     *
     * @param player the number of the player.
     */
    public void setWonBid(int player) {
        if (isValidPlayer(player)) {
            wonBid = player;
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
     * Adds cards to a player's deck.
     *
     * @param player the number of the player.
     * @param cards the card sto be added.
     */
    public void addCardsToPlayer(int player, Card... cards) {
        if (isValidPlayer(player)) {
            allPlayerDecks[player].add(cards);
        }
    }

    /**
     * Removes cards from a player's deck.
     *
     * @param player the number of the player.
     * @param card the card to be removed.
     */
    public void removeCardsFromPlayer(int player, Card... card) {
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
     * @param player the card's owner player id
     * @param card the card to be played.
     */
    public void addTrickToCenter(int player, Card card) {
        card.setPlayer(player);
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
     * Adds cards from the trick to player's trick deck.
     *
     * @param player the number of the player.
     */
    public void addTrickToPlayer(int player) {
        if (isValidPlayer(player)) {
            tricksDeck[player].add(centerDeck.getCards().toArray(new Card[0]));
        }
    }

    public boolean playerHasSuit(int player, Suit suit) {
        Deck deck = allPlayerDecks[player];
        for (Card card : deck.getCards()) {
            if (card.getSuit().equals(suit)) return true;
        }
        return false;
    }

    public void setLeadTrick(Card leadTrick) {
        this.leadTrick = leadTrick;
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

    public void nextTrickRound() {
        trickRound++;
    }

    public void setPreviousTrickWinner(int previousTrickWinner) {
        this.previousTrickWinner = previousTrickWinner;
    }

    /**
     * New round if teams have yet to win the game
     */
    public void newRound() {
        phase = PinochleGamePhase.BIDDING;
        firstBidder = (firstBidder + 1) % PinochleGameState.NUM_PLAYERS;
        turn = firstBidder;
        bids = new int[NUM_PLAYERS];
        passed = new boolean[NUM_PLAYERS];
        wonBid = -1;
        trumpSuit = null;
        voteGoSet = new boolean[NUM_PLAYERS];
        voted = new boolean[NUM_PLAYERS];
        melds = new ArrayList[NUM_PLAYERS];
        Arrays.fill(melds, new ArrayList<Meld>());
        allPlayerDecks = new Deck[NUM_PLAYERS];
        for (int i = 0; i < allPlayerDecks.length; i++) {
            allPlayerDecks[i] = new Deck();
        }
        mainDeck = new Deck();
        mainDeck.reset();
        mainDeck.shuffle();
        for (int i = 0; i < allPlayerDecks.length; i++) {
            allPlayerDecks[i].add(dealCards());
        }
        centerDeck = new Deck();
        tricksDeck = new Deck[NUM_PLAYERS];
        for (int i = 0; i < tricksDeck.length; i++) {
            tricksDeck[i] = new Deck();
        }
        lastTrickRoundPlayed = new int[NUM_PLAYERS];
        Arrays.fill(lastTrickRoundPlayed, -1);
        leadTrick = null;
        lastTrick = -1;
        trickRound = 0;
        previousTrickWinner = -1;
    }

    /**
     * Resets the Game State.
     */
    public void reset() {
        newRound();
        firstBidder = 0;
        turn = firstBidder;
        scoreboard = new int[NUM_TEAMS];

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
    public PinochleGamePhase getPhase() {
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

    public boolean canGoSet(int team) {
        int bidWinner = getWonBid();
        int getMaxBid = getMaxBid();
        int bidWinnerTeam = getTeam(bidWinner);
        if (team != bidWinnerTeam) return false;
        int totalPoints = getScoreboard()[bidWinnerTeam];
        if ((getMaxBid - totalPoints) > 250) return true;
        return false;
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

    public int getMaxBid() {
        int max = 0;
        for (int bid : bids) {
            if (bid > max) max = bid;
        }
        return max;
    }

    /**
     * Returns who passed
     *
     * @return boolean array of who passed
     */
    public boolean[] getPassed() {
        return passed.clone();
    }

    public boolean[] getVoteGoSet() {
        return voteGoSet.clone();
    }

    public boolean[] getVoted() {
        return voted.clone();
    }

    public boolean getVoted(int player) {
        return voted[player];
    }

    public boolean allHasVoted() {
        for (boolean hasVoted : voted) {
            if (!hasVoted) return false;
        }
        return true;
    }

    /**
     * Returns if the player has passed.
     *
     * @return boolean if passed.
     */
    public boolean getPassed(int player) {
        return passed[player];
    }

    public int countPassed() {
        int count = 0;
        for (boolean pass : passed) {
            if (pass) count++;
        }
        return count;
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
     * @return the array of Decks for each player's tricks Deck.
     */
    private Deck[] getAllTricksDecks(){
        Deck[] clone = new Deck[tricksDeck.length];
        for (int i = 0; i < tricksDeck.length; i++) {
            clone[i] = new Deck(tricksDeck[i]);
        }
        return clone;
    }

    /**
     * Returns the tricks deck of the player.
     *
     * @param player the number of the player.
     * @return the tricks deck of that player.
     */
    public Deck getTricksDeck(int player) {
        if (isValidPlayer(player))
            return new Deck(tricksDeck[player]);
        else return null;
    }

    public Card getLeadTrick() {
        if (leadTrick == null) return null;
        return new Card(leadTrick);
    }

    /**
     * Returns the team that won the last trick.
     *
     * @return the number of the team.
     */
    public int getLastTrick() {
        return lastTrick;
    }

    public int getTrickRound() {
        return trickRound;
    }

    public int getTrickWinner() {
        return 0;//TODO
    }

    public int getLastTrickRoundPlayed(int player) {
        if (isValidPlayer(player)) {
            return lastTrickRoundPlayed[player];
        }
        return -1;
    }

    public void setLastTrickRoundPlayed(int player) {
        if (isValidPlayer(player)) {
            lastTrickRoundPlayed[player] = trickRound;
        }
    }

    public int[] getLastTrickRoundPlayed() {
        return lastTrickRoundPlayed.clone();
    }

    public int getPreviousTrickWinner() {
        return previousTrickWinner;
    }

    public int getWinner() {
        for (int i = 0; i < scoreboard.length; i++) {
            System.out.println(String.format("Team %s has %s points", i, scoreboard[i]));
            if (scoreboard[i] >= 1500) {
                return i;
            }
        }
        return -1;
    }
}