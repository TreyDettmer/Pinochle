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
 * Game State - holds all game information
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleGameState extends GameState {
    public static final int NUM_TEAMS = 2;
    public static final int NUM_PLAYERS = 4;
    public static final int DEAL_CARDS = 12;
    private static final long serialVersionUID = 545884920868735343L;
    private PinochleGamePhase phase;  // The phase the game is on.
    private int turn;   // Number of player whose turn it is.
    private int[] scoreboard;   // Array of player scores.
    private int[] meldsScoreboard;   // Array of player scores.
    private int[] tricksScoreboard;   // Array of player scores.
    private int[] lastTrickRoundPlayed;
    private int firstBidder;    // Player number of the first bidder.
    private int[] bids; // Bids of each player.
    private boolean[] passed;   // Which players have passed.
    private boolean[] voteGoSet;
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

    /**
     * Constructor
     */
    public PinochleGameState() {
        reset();
    }

    /**
     * Copy constructor
     */
    public PinochleGameState(PinochleGameState gameState) {
        phase = gameState.getPhase();
        turn = gameState.getTurn();
        scoreboard = gameState.getScoreboard();
        meldsScoreboard = gameState.getMeldsScoreboard();
        tricksScoreboard = gameState.getTricksScoreboard();
        firstBidder = gameState.getFirstBidder();
        bids = gameState.getBids();
        passed = gameState.getPassed();
        voteGoSet = gameState.getVoteGoSet();
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
     * Adds points to a team's score.
     *
     * @param team the number of the team.
     * @param score the points added to the team's score.
     */
    private void addScore(int team, int score) {
        if (isValidTeam(team)) {
            scoreboard[team] += score;
        }
    }

    /**
     * Adds points to a team's meld score.
     *
     * @param team the number of the team.
     * @param score the points added to the team's score.
     */
    public void addMeldScore(int team, int score) {
        if (isValidTeam(team)) {
            meldsScoreboard[team] += score;
        }
    }

    /**
     * Adds points to a team's trick score.
     *
     * @param team the number of the team.
     * @param score the points added to the team's score.
     */
    public void addTrickScore(int team, int score) {
        if (isValidTeam(team)) {
            tricksScoreboard[team] += score;
        }
    }

    /**
     * Adds the specific card to the center deck.
     *
     * @param player the card's owner player id
     * @param card the card to be played.
     */
    public void addTrickToCenter(int player, Card card) {
        if (isValidPlayer(player)) {
            card.setPlayer(player);
            centerDeck.add(card);
        }
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

    /**
     * Adds points from the melding and trick-taking phase to a teams score
     * only if they have met or exceeded the bid if their team won the bid or
     * if they have won at least 10 points from the trick taking phase if they are the other team
     *
     */
    public void calculateFinalScore() {
        for (int i = 0; i < NUM_TEAMS; i++) {
            int meldPoints = getMeldsScoreboard()[i];
            int trickPoints = getTricksScoreboard()[i];
            //Team that won the bid
            if (i == getTeam(wonBid)) {
                //Combined meld and trick-taking points must exceed their bid to add to their score
                if (meldPoints + trickPoints >= getMaxBid()) {
                    addScore(i, meldPoints + trickPoints);
                } else {
                    //Bid is subtracted from the total score, and they receive no points for the round
                    goSet(i);
                }
            } else {
                //Other team only has to win 10 points from the trick-taking phase to win points from the round
                if (trickPoints >= 10) {
                    addScore(i, meldPoints + trickPoints);
                }
            }
        }
    }

    /**
     * Checks if a team is eligible to go set
     *
     * @param team the team to check
     * @return if a team can go set
     */
    public boolean canGoSet(int team) {
        if (isValidTeam(team)) {
            int bidWinner = getWonBid();
            int bidWinnerTeam = getTeam(bidWinner);
            if (team != bidWinnerTeam) {
                return false;
            }

            int totalPoints = getMeldsScoreboard()[bidWinnerTeam];
            System.out.println("Team " + team + " bid difference: " + (getMaxBid() - totalPoints));
            if ((getMaxBid() - totalPoints) > 250) return true;
        }
        return false;
    }

    /**
     * Count how many players passed in bidding
     *
     * @return count of players that passed
     */
    public int countPassed() {
        int count = 0;
        for (boolean pass : passed) {
            if (pass) count++;
        }
        return count;
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
     * Returns the player bids.
     *
     * @return the array of player bids.
     */
    public int[] getBids() {
        return bids.clone();
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
     * Returns the first bidder.
     *
     * @return the number of the first bidder.
     */
    public int getFirstBidder() {
        return firstBidder;
    }

    /**
     * Player is the last player of the round
     *
     * @param player player to check
     * @return the index of the player
     */
    public boolean isLastPlayer(int player) {
        if (isValidPlayer(player)) {
            return player == NUM_PLAYERS - 1;
        }
        return false;
    }

    /**
     * Returns the team that won the last trick.
     *
     * @return the number of the team.
     */
    public int getLastTrick() {
        return lastTrick;
    }

    /**
     * Gets the lead trick card
     *
     * @return Card of the lead trick
     */
    public Card getLeadTrick() {
        if (leadTrick == null) return null;
        return new Card(leadTrick);
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
     * Returns the max bid, or the bid winning amount
     *
     * @return the max bid amount
     */
    public int getMaxBid() {
        int max = 0;
        for (int bid : bids) {
            if (bid > max) max = bid;
        }
        return max;
    }

    /**
     * Returns the array of player melds
     *
     * @return player melds indexed by player id
     */
    public ArrayList<Meld>[] getMelds() {
        return melds.clone();
    }

    /**
     * Returns the melds scoreboard.
     *
     * @return the melds scoreboard array.
     */
    public int[] getMeldsScoreboard() {
        return meldsScoreboard.clone();
    }

    /**
     * Returns who passed
     *
     * @return boolean array of who passed
     */
    public boolean[] getPassed() {
        return passed.clone();
    }

    /**
     * Returns if the player has passed.
     *
     * @return boolean if passed.
     */
    public boolean getPassed(int player) {
        if (isValidPlayer(player)) {
            return passed[player];
        }
        return false;
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
     * Returns the deck of the player.
     *
     * @param player the number of the player.
     * @return the deck of the player.
     */
    public Deck getPlayerDeck(int player) {
        if (isValidPlayer(player)) {
            return new Deck(allPlayerDecks[player]);
        }
        return null;
    }

    /**
     * Returns the player who won the last trick
     *
     * @return player id for the player who won the last trick
     */
    public int getPreviousTrickWinner() {
        return previousTrickWinner;
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
     * For the team who won the bid that goes set, their bid is subtracted from their total score
     *
     * @param team that won the bid who is going set
     */
    public void goSet(int team) {
        if (isValidTeam(team)) {
            if (team == getTeam(wonBid)) {
                subtractScore(team, getMaxBid());
            }
        }
    }

    /**
     * Returns the team the player is on.
     *
     * @param player the number of the player.
     * @return the number of the team.
     */
    public int getTeam(int player) {
        if (isValidPlayer(player)) {
            return player % NUM_TEAMS;
        }
        return -1;
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
     * Returns the trick points of the cards in the center.
     *
     * @return point value
     */
    public int getTrickPoints() {
        int points = 0;
        if (trickRound == 11) return 10;
        for (Card card : centerDeck.getCards()) {
            switch (card.getRank()) {
                case TEN: case KING: case ACE:
                    points += 10;
                    break;
            }
        }
        return points;
    }

    /**
     * Returns the round during of trick-taking phase
     *
     * @return round of the trick-taking phase
     */
    public int getTrickRound() {
        return trickRound;
    }

    /**
     * Returns the tricks scoreboard.
     *
     * @return the scoreboard array.
     */
    public int[] getTricksScoreboard() {
        return tricksScoreboard.clone();
    }

    /**
     * Returns the winner of the trick
     *
     * @return player id for the winner of the trick
     */
    public int getTrickWinner() {
        Card winningCard = getTrickWinningCard();
        return winningCard.getPlayer();
    }

    /**
     * Returns the current winning card in the center deck.
     *
     * @return the winning card in the center deck
     */
    public Card getTrickWinningCard() {
        if (centerDeck.getCards().size() == 0) return null;
        Card winningCard = centerDeck.getCards().get(0);
        for (Card card : centerDeck.getCards()) {
            if (card.getSuit().equals(trumpSuit)) {
                if (winningCard.getSuit().equals(trumpSuit)) {
                    if (card.getRank().ordinal() > winningCard.getRank().ordinal()) winningCard = card;
                } else {
                    winningCard = card;
                }
            } else if (card.getSuit().equals(winningCard.getSuit())) {
                if (card.getRank().ordinal() > winningCard.getRank().ordinal()) winningCard = card;
            }

        }
        return winningCard;
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
     * Returns the trump suit.
     *
     * @return the trump suit.
     */
    public Suit getTrumpSuit() {
        return trumpSuit;
    }

    /**
     * Returns the array of players with their votes to go set
     *
     * @return the array of votes indexed by player id
     */
    public boolean[] getVoteGoSet() {
        return voteGoSet.clone();
    }

    /**
     * Gets a player's vote to go set
     *
     * @return if they voted true/false
     */
    public boolean getVoteGoSet(int player) {
        if (isValidPlayer(player)) {
            return voteGoSet[player];
        }
        return false;
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
     * Updates the phase of the game.
     */
    public void nextPhase() {
        PinochleGamePhase[] phases = PinochleGamePhase.values();
        phase = phases[(phase.ordinal() + 1) % phases.length];
        System.out.println("New phase: " + phase.name());
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

    /**
     * New round if teams have yet to win the game
     * Clears variables that are specific to the round
     * Ex: not the scoreboard
     *
     */
    public void newRound() {
        phase = PinochleGamePhase.BIDDING;
        firstBidder = (firstBidder + 1) % PinochleGameState.NUM_PLAYERS;
        turn = firstBidder;
        meldsScoreboard = new int[NUM_TEAMS];
        tricksScoreboard = new int[NUM_TEAMS];
        bids = new int[NUM_PLAYERS];
        passed = new boolean[NUM_PLAYERS];
        wonBid = -1;
        trumpSuit = null;
        voteGoSet = new boolean[NUM_PLAYERS];
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
     * Iterates the trick round to the next
     *
     */
    public void nextTrickRound() {
        trickRound++;
    }

    public boolean playerHasSuit(int player, Suit suit) {
        if (isValidPlayer(player)) {
            Deck deck = allPlayerDecks[player];
            for (Card card : deck.getCards()) {
                if (card.getSuit().equals(suit)) return true;
            }
        }
        return false;
    }

    /**
     * Finds a card in the players deck that could beat the cards in the center of the deck
     *
     * @param player player whose hand to check

     * @return Card the winnable card, null if there is no such card
     */
    public Card playerHasWinnableCard(int player) {
        Deck deck = allPlayerDecks[player];
        //Finds the highest ranking card in the trick pile
        Card winningCard = getTrickWinningCard();
        if (winningCard == null) return null;
        //Player has lead trick suits
        if (playerHasSuit(player, leadTrick.getSuit())) {
            //If the winning card is a trump suit and the lead trick suit is not the trump suit,
            //then there is no winning card because the player must play a lead trick suit
            if (winningCard.getSuit().equals(trumpSuit) && !leadTrick.getSuit().equals(trumpSuit)) return null;
            else if (winningCard.getSuit().equals(leadTrick.getSuit())){
                //Find the minimum ranking card to beat the cards in the center deck
                Card min = null;
                for (Card card : deck.getCards()) {
                    if (card.getSuit().equals(leadTrick.getSuit())) {
                        //Card must beat the winning card in the center
                        if (card.getRank().ordinal() > winningCard.getRank().ordinal()) {
                            //Make sure it's the minimum ranking card
                            if (min == null) min = card;
                            else if (card.getRank().ordinal() < min.getRank().ordinal()) min = card;
                        }
                    }
                }
                return min;
            }
            //Player doesn't have the lead trick suit, but has trump suit
        } else if (playerHasSuit(player, trumpSuit)) {
            if (winningCard.getSuit().equals(trumpSuit)) {
                //Find the minimum ranking card to beat the cards in the center deck
                Card min = null;
                for (Card card : deck.getCards()) {
                    if (card.getSuit().equals(trumpSuit)) {
                        //Card must beat the winning card in the center
                        if (card.getRank().ordinal() > winningCard.getRank().ordinal()) {
                            //Make sure it's the minimum ranking card
                            if (min == null) min = card;
                            else if (card.getRank().ordinal() < min.getRank().ordinal()) min = card;
                        }
                    }
                }
                return min;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * Clears the center deck.
     *
     */
    public void removeAllCardsFromCenter() {
        centerDeck.clear();
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
     * Resets the Game State.
     * Resets all variables of the game.
     *
     */
    public void reset() {
        newRound();
        firstBidder = 0;
        turn = firstBidder;
        scoreboard = new int[NUM_TEAMS];

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
     * Sets the lead trick
     *
     * @param leadTrick the lead trick card
     */
    public void setLeadTrick(Card leadTrick) {
        this.leadTrick = leadTrick;
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
     * Sets the phase of the game
     *
     * @param phase the phase to set to
     */
    public void setPhase(PinochleGamePhase phase) {
        this.phase = phase;
        System.out.println("New phase: " + phase.name());
    }

    /**
     * Sets the melds a player has.
     *
     * @param player the number of the player.
     * @param melds the melds a player has in their hand.
     */
    public void setPlayerMelds(int player, ArrayList<Meld> melds) {
        if (isValidPlayer(player)) {
            if (melds != null) {
                this.melds[player] = (ArrayList<Meld>) melds.clone();
            }
            else
            {
                this.melds[player] = null;
            }
        }
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
     * Sets the previous trick winner
     *
     * @param previousTrickWinner player who won the last trick
     */
    public void setPreviousTrickWinner(int previousTrickWinner) {
        this.previousTrickWinner = previousTrickWinner;
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
     * Sets a player's vote to go set
     *
     * @param player player's vote to go set
     */
    public void setVoteGoSet(int player) {
        if (isValidPlayer(player)) {
            voteGoSet[player] = true;
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
     * Removes points from a team's score.
     *
     * @param team the number of the team.
     * @param score the points removed from the team's score.
     */
    public void subtractScore(int team, int score) {
        if (isValidTeam(team)) {
            scoreboard[team] -= score;
        }
    }
}