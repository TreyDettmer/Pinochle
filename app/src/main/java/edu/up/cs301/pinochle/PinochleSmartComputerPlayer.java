package edu.up.cs301.pinochle;

import java.util.ArrayList;

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

        if (info instanceof PinochleGameState) {
            state = (PinochleGameState) info;
            phase = state.getPhase();

            deck = state.getPlayerDeck(playerNum);
            deck.sort();
            theoreticalDeck = findTheoreticalDeck(deck);
            losingCards = findLosingCards(deck);


            switch (phase) {
                // If it is the dealing phase:
                case BIDDING:
                    break;

                case CHOOSE_TRUMP:
                    break;

                // If it is the exchanging phase:
                case EXCHANGE_CARDS:
                    // If the player won the bid, the player exchanges the losing cards.
                    if (state.getWonBid() == playerNum) {
                        game.sendAction(new PinochleActionExchangeCards(this, losingCards));

                        // Else the player exchanges the highest cards in its deck.
                    } else {
                        // The cards of the player's deck.
                        ArrayList<Card> cards = deck.getCards();

                        // Card array for the highest ranking cards.
                        Card[] winningCards = new Card[4];

                        /*
                         * For loop used to go through the last 4 cards of the
                         * player's deck and adds them to the winning card array.
                         */
                        for (int i = deck.size() - 1; i >= 8; i--) {
                            winningCards[i] = cards.get(i);
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

                case TRICK_TAKING:
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
