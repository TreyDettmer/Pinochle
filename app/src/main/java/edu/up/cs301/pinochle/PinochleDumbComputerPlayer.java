package edu.up.cs301.pinochle;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Suit;
import edu.up.cs301.game.GameFramework.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.IllegalMoveInfo;

/*
 * A dumb computer player that plays based off of random decisions.
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleDumbComputerPlayer extends GameComputerPlayer {

    private Random rnd = new Random();
    private PinochleGameState state;

    public PinochleDumbComputerPlayer(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {

        if (info instanceof IllegalMoveInfo) {
            //System.out.println("Illegal move");
        }
        PinochleGamePhase phase;
        int num;

        if (info instanceof PinochleGameState) {
            sleep(0.2);

            state = (PinochleGameState) info;
            phase = state.getPhase();


            ArrayList<Card> cards;

            switch (phase) {
                // If it is the bidding phase:
                case BIDDING:

                    /*
                     * A random number is generated to determine whether the player
                     * bids 10, 20, or passes.
                     */
                    num = rnd.nextInt(3);

                    // If the random number is 0, the player bids 10.
                    if (num == 0) {
                        game.sendAction(new PinochleActionBid(this, PinochleActionBid.BID_10));

                        // Else if the random number is 1, the player bids 20.
                    } else if (num == 1) {
                        game.sendAction(new PinochleActionBid(this, PinochleActionBid.BID_20));

                        // Otherwise the player passes.
                    } else {
                        game.sendAction(new PinochleActionPass(this));
                    }
                    break;

                // If it is the trump suit selection phase.
                case CHOOSE_TRUMP:

                    // Generates a random number between 0 and excluding 4.
                    num = rnd.nextInt(4);

                    // If the random number is 0, the trump suit is Club.
                    if (num == 0) {
                        game.sendAction(new PinochleActionChooseTrump(this, Suit.Club));

                        // Else if the random number is 1, the trump suit is Diamond.
                    } else if (num == 1) {
                        game.sendAction(new PinochleActionChooseTrump(this, Suit.Diamond));

                        // Else if the random number is 2, the trump suit is Heart.
                    } else if (num == 2) {
                        game.sendAction(new PinochleActionChooseTrump(this, Suit.Heart));

                        // Otherwise, the trump suit is Spade.
                    } else {
                        game.sendAction(new PinochleActionChooseTrump(this, Suit.Spade));
                    }
                    break;

                // If it is the exchanging phase:
                case EXCHANGE_CARDS:
                    // An array of the cards to be exchanged.
                    Card[] exchangeCards = new Card[4];

                    // The cards from the player's deck.
                    cards = state.getPlayerDeck(playerNum).getCards();

                    /*
                     * For loop used to selected four random cards from
                     * the player's deck. A random number is generated
                     * between 0 and the number of cards in the player's hand.
                     * The card at the random index of the player's deck is
                     * added to the exchanging cards and then removed to avoid
                     * duplication.
                     */
                    for (int i = 0; i < 4; i++) {
                        num = rnd.nextInt(cards.size());
                        exchangeCards[i] = cards.get(num);
                        cards.remove(num);
                    }

                    game.sendAction(new PinochleActionExchangeCards(this, exchangeCards));
                    break;

                // If it is the melding phase:
                case MELDING:
                    // Calculates the melds.
                    game.sendAction(new PinochleActionCalculateMelds(this));
                    break;

                // If it is the go set phase:
                case VOTE_GO_SET:

                    // Generates either 1 or 0 (50% chance to vote to go set)
                    num = rnd.nextInt(2);

                    // If the random number is 0, the player votes to go set.
                    if (num == 0 && state.canGoSet(state.getTeam(playerNum))) {
                        game.sendAction(new PinochleActionVoteGoSet(this, true));
                    } else {
                        // Otherwise, it doesn't.
                        game.sendAction(new PinochleActionVoteGoSet(this, false));
                    }
                    break;

                // If it is the trick-taking phase:
                case TRICK_TAKING:
                    // The cards from the player's deck but shuffled to randomize the order.
                    cards = state.getPlayerDeck(playerNum).shuffle().getCards();

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
                     * is playable based on its suit.
                     */
                    for (Card card : cards) {
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
}
