package edu.up.cs301.pinochle;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Meld;
import edu.up.cs301.card.Rank;
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

    private int bidAllowance;
    private int pointDifference;
    private int pointsToWin;
    private Card[] missingCards;
    private Card[] losingCards;
    private Deck theoreticalDeck;
    private Deck deck;
    private PinochleGameState state;
    private boolean hasFoundTheoretical = false;

    // Temporary:
    private Random rnd = new Random();

    /**
     * Constructor:
     *
     * @param name the name of the player.
     */
    public PinochleSmartComputerPlayer(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if (info instanceof IllegalMoveInfo) {
            System.out.println("Illegal move");
        }
        PinochleGamePhase phase;

        // TODO: Temporary:
        int num;
        ArrayList<Card> cards;

        if (info instanceof PinochleGameState) {
            state = (PinochleGameState) info;
            phase = state.getPhase();

            deck = state.getPlayerDeck(playerNum);
            deck.sort();
            theoreticalDeck = findTheoreticalDeck(deck);
            //losingCards = findLosingCards(deck);

            if (state.getTurn() == playerNum)
            {
                sleep(2);
            }
            switch (phase) {
                // If it is the dealing phase:
                case BIDDING:
                    // TODO: Temporary:

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

                case CHOOSE_TRUMP:
                    // TODO: Temporary:
                    losingCards = findLosingCards(deck);

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
                    // If the player won the bid, the player exchanges the losing cards.
                    if (state.getWonBid() == playerNum) {
                        game.sendAction(new PinochleActionExchangeCards(this, losingCards));

                        // Else the player exchanges the highest cards in its deck.
                    } else {
                        // The cards of the player's deck.
                        cards = deck.getCards();

                        for (int i = 0; i < 4; i++ ) {
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
                    // TODO: Temporary:

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


                case TRICK_TAKING:
                    if (state.getTurn() == playerNum) {
                        if (state.getCenterDeck().getCards().size() >= 4) {
                            game.sendAction(new PinochleActionPlayTrick(this,null));
                            break;
                        }
                    }
                    // TODO: Temporary:

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



                    //checks if the player can win the trick with a card matching the leading suit. If they can then they must play the that card.
                    Card winnableCard = state.playerHasWinnableCard(playerNum);
                    if (winnableCard != null)
                    {
                        playCard = winnableCard;
                    }
                    else {

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

    private int findPointDifferece(PinochleGameState state) {
        int pointDifference = 0;

        return pointDifference;
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

    private Card[] findMissingCards(Deck deck) {
        Card[] missingCards = new Card[4];

        return missingCards;
    }

    private Deck findTheoreticalDeck(Deck deck) {
        Deck theoreticalHand = new Deck();

        return theoreticalHand;
    }
}
