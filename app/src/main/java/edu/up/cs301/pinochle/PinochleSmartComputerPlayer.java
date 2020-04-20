package edu.up.cs301.pinochle;

import java.util.ArrayList;
import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Meld;
import edu.up.cs301.card.Suit;
import edu.up.cs301.game.GameFramework.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.IllegalMoveInfo;

/*
 * Smart computer player that attempts to play intelligently and make
 * calculated decisions during the game.
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleSmartComputerPlayer extends GameComputerPlayer {

    private int bidAllowance;   // The maximum amount the player can bid.
    private Suit trumpSuit; // The determined Trump suit for the player.
    private Card[] losingCards; // The determined losing cards of the player's deck.
    private Deck deck;  // The player's deck.
    private PinochleGameState state;    // The game's state.
    private boolean deckAnalysisComplete = false;

    private static final int STARTING_BID = 250;
    private static final int BID_20 = 20;

    /**
     * Constructor:
     *
     * @param name the name of the player.
     */
    public PinochleSmartComputerPlayer(String name) {
        super(name);
    }

    /**
     * Receives information about the game and its current state.
     *
     * @param info the information of the current game.
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        if (info instanceof IllegalMoveInfo) {
            System.out.println("Illegal move");
        }
        PinochleGamePhase phase;
        ArrayList<Card> cards;

        if (info instanceof PinochleGameState) {
            state = (PinochleGameState) info;
            phase = state.getPhase();

            deck = state.getPlayerDeck(playerNum);
            deck.sort();

            if (state.getTurn() == playerNum) {
                sleep(2);
            }
            switch (phase) {
                // If it is the dealing phase:
                case BIDDING:

                    /*
                     * Checks if the melds have been calculated, as it needs to occur only once.
                     * Additional one off functions are completed.
                     */
                    if (!deckAnalysisComplete) {
                        // List of all the melds for the deck
                        ArrayList<Meld> melds;
                        // The maximum points found for the deck based on the trump suit.
                        int maxPoints = 0;
                        Suit[] suits = Suit.values();

                        // Finds the losing cards of the deck for the player
                        losingCards = findLosingCards(deck);

                        /*
                         * Calculates the melds of the deck with each possible suit configuration.
                         * For each meld of the deck and calculates the points and adds them.
                         */
                        for (Suit suit : suits) {

                            int points = 0; // Points for the set of melds.
                            // Gets the ArrayList of all the melds for that instance.
                            melds = Meld.checkMelds(deck, suit);

                            /*
                             * Loops through each meld in the meld ArrayList and adds
                             * all the points for each meld.
                             */
                            for (int i = 0; i < melds.size(); i++) {
                                points += melds.get(i).getPoints();
                            }

                            /*
                             * If the points calculated are greater than the max points,
                             * the max points are changed, and the trump suit is selected
                             * based on the suit that generated the melds for the deck.
                             */
                            if (points > maxPoints) {
                                maxPoints = points;
                                trumpSuit = suit;
                            }
                        }

                        // Sets the bidAllowance to the max points plus
                        bidAllowance = maxPoints + STARTING_BID;
                        deckAnalysisComplete = true;
                    }

                    int maxBid = state.getMaxBid();

                    /*
                     * If the difference between the bidAllowance and maxBid is greater than 20,
                     * the player will bid 20.
                     */
                    if (bidAllowance - maxBid > BID_20) {

                        game.sendAction(new PinochleActionBid(this, PinochleActionBid.BID_20));

                        /*
                         * Else if the difference between the bidAllowance and maxBid is less than
                         * or equal to 20 and greater than 0, the player will bid 10.
                         */
                    } else if (bidAllowance - maxBid <= BID_20 && bidAllowance - maxBid > 0) {

                        game.sendAction(new PinochleActionBid(this, PinochleActionBid.BID_10));

                        // Otherwise the player will pass.
                    } else {

                        game.sendAction(new PinochleActionPass(this));
                    }
                    break;

                // If it is the trump suit selection phase:
                case CHOOSE_TRUMP:
                    game.sendAction(new PinochleActionChooseTrump(this, trumpSuit));
                    break;

                // If it is the exchanging phase:
                case EXCHANGE_CARDS:
                    // If the player won the bid, the player exchanges the losing cards.
                    if (state.getWonBid() == playerNum) {
                        game.sendAction(new PinochleActionExchangeCards(this, losingCards));

                        // Else the player exchanges the highest cards in its deck.
                    } else {
                        // The cards of the player's deck.
                        cards = deck.getCards();

                        for (int i = 0; i < 4; i++) {
                            cards.remove(losingCards[i]);
                        }

                        // Card array for the highest ranking cards.
                        Card[] winningCards = new Card[4];

                        /*
                         * For loop used to go through the last 4 cards of the
                         * player's deck and adds them to the winning card array.
                         */
                        for (int i = 0; i < 4; i++) {
                            winningCards[i] = cards.get(cards.size() - 1 - i);
                        }

                        // Exchanges the winning cards.
                        game.sendAction(new PinochleActionExchangeCards(this, winningCards));
                    }
                    break;

                // If it is the melding phase:
                case MELDING:
                    // Calculates the melds.
                    game.sendAction(new PinochleActionCalculateMelds(this));
                    break;

                // If it is the go set phase:
                case VOTE_GO_SET:

                    // If the player can go set, it will.
                    if (state.canGoSet(state.getTeam(playerNum))) {
                        game.sendAction(new PinochleActionVoteGoSet(this, true));

                    // Otherwise the player won't.
                    } else {
                        game.sendAction(new PinochleActionVoteGoSet(this, false));
                    }
                    break;

                // IF it is the trick-taking phase:
                case TRICK_TAKING:
                    if (state.getTurn() == playerNum) {
                        if (state.getCenterDeck().getCards().size() >= 4) {
                            game.sendAction(new PinochleActionPlayTrick(this, null));
                            break;
                        }
                    }

                    // The cards from the shorted player's deck
                    cards = deck.getCards();

                    // The card to play.
                    Card playCard = null;

                    // If the lead suit has been determined
                    boolean hasLeadSuit = false;
                    boolean hasTrumpSuit;

                    // If there is a lead trick, sets the hasLeadSuit to true
                    if (state.getLeadTrick() != null) {
                        hasLeadSuit = state.playerHasSuit(playerNum, state.getLeadTrick().getSuit());
                    }

                    // Determines if it has the trump suit.
                    hasTrumpSuit = state.playerHasSuit(playerNum, state.getTrumpSuit());

                    /*
                     * For loop used to go through each card and determine if it
                     * is playable based on its suit. The loop starts from the end
                     * of the array and iterates to the start, as it searches for
                     * the highest card.
                     */
                    for (int i = cards.size() - 1; i > 0; i--) {

                        Card card = cards.get(i);

                        // Checks if it has the suit of the leading trick.
                        if (hasLeadSuit) {

                            // Checks if the card's suit is the same as the leading trick suit.
                            if (card.getSuit() == state.getLeadTrick().getSuit()) {

                                // Sets the card to play to the card of the loop.
                                playCard = card;
                                break;
                            }
                            // Else checks if it has the trump suit.
                        } else if (hasTrumpSuit) {

                            // Checks if the card's suit is the same as the trump suit.
                            if (card.getSuit() == state.getTrumpSuit()) {

                                // Sets the card to play to the card of the loop.
                                playCard = card;
                                break;
                            }
                            // Otherwise, it plays the next random card.
                        } else {

                            // Sets the card to play to the card of the loop.
                            playCard = card;
                            break;
                        }
                    }

                    // Sends the card to be played.
                    game.sendAction(new PinochleActionPlayTrick(this, playCard));
                    break;

                // If it is the scoring phase:
                case ACKNOWLEDGE_SCORE:
                    // Acknowledges the player score.
                    game.sendAction(new PinochleActionAcknowledgeScore(this));
                    break;
            }
        }

    }

    /**
     * Returns the cards from the player's deck considered to lose tricks.
     *
     * @param deck the deck of the player.
     * @return Card[] of the cards considered to lose tricks.
     */
    private Card[] findLosingCards(Deck deck) {

        // Card array for the losing cards.
        Card[] losingCards = new Card[4];

        // Sorts the deck based on rank.
        deck.sort();

        // ArrayList of the cards in the deck.
        ArrayList<Card> cards = deck.getCards();

        /*
         * For loop used to go through the first 4 cards of the
         * player's deck and adds them to the losing card array.
         */
        for (int i = 0; i < 4; i++) {
            losingCards[i] = cards.get(i);
        }

        // Returns the losing cards.
        return losingCards;
    }
}
