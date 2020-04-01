package edu.up.cs301.pinochle;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameFramework.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;

/*
 * Description
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

    private static final int BID_10 = 10;
    private static final int BID_20 = 20;
    
    public PinochleDumbComputerPlayer(String name)
    {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {

        int phase;
        int num;

        if (info instanceof PinochleGameState) {

            this.state = (PinochleGameState) info;
            phase = state.getPhase();
            ArrayList<Card> cards;

            switch(phase){

                    // If it is the bidding phase:
                case 1:

                    /*
                     * A random number is generated to determine whether the player
                     * bids 10, 20, or passes.
                     */
                    num = rnd.nextInt(3);

                    // If the random number is 0, the player bids 10.
                    if (num == 0) {
                        // TODO: Need to send BID_10 with the action.
                        game.sendAction(new PinochleActionBid(this, PinochleActionBid.BID_10));

                        // Else if the random number is 1, the player bids 20.
                    } else if (num == 1) {
                        // TODO: Need to send BID_20 with the action.
                        game.sendAction(new PinochleActionBid(this, PinochleActionBid.BID_20));

                        // Otherwise the player passes.
                    } else {

                        game.sendAction(new PinochleActionPass(this));
                    }

                    break;

                    // If it is the trump suit selection phase
                case 2:

                    num = rnd.nextInt(4);

                    if (num == 0) {
                        // TODO: Need to send "Club" with the action.
                        game.sendAction(new PinochleActionChooseTrump(this));

                    } else if (num == 1) {
                        // TODO: Need to send "Diamond" with the action.
                        game.sendAction(new PinochleActionChooseTrump(this));

                    } else if (num == 2) {
                        // TODO: Need to send "Heart" with the action.
                        game.sendAction(new PinochleActionChooseTrump(this));

                    } else {
                        // TODO: Need to send "Spade" with the action.
                        game.sendAction(new PinochleActionChooseTrump(this));
                    }
                    break;

                    // If it is the exchanging phase:
                case 3:
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

                    // TODO: Need to send the Card[] with the action.
                    game.sendAction(new PinochleActionExchangeCards(this));
                    break;

                case 4:
                    // calculate melds
                    break;

                    // If it is the go set phase:
                case 5:

                    // Generates either 1 or 0 (50% chance to vote to go set)
                    num = rnd.nextInt(2);

                    // If the random number is 0, the player votes to go set.
                    if (num == 0) {
                        game.sendAction(new PinochleActionGoSet(this));
                    }
                    // Otherwise, it doesn't.
                    break;

                    // If it is the trick-taking phase:
                case 6:
                    // The cards from the player's deck.
                    cards = state.getPlayerDeck(playerNum).getCards();

                    // The card to play.
                    Card playCard;

                    ArrayList<Card> suitCards = new ArrayList<>();
                    ArrayList<Card> trumpCards = new ArrayList<>();
                    ArrayList<Card> backUpCards = new ArrayList<>();

                    boolean validCard = false;

                    // While loop used to find a valid card to play.
                    // TODO: Check if the while loop is necessary and if helper method is needed.
                    while(!validCard) {

                        /*
                         * For loop used to go through each card in the player's deck
                         * and sort them into array lists, depending on if they match the
                         * suit of the trick, the trump suit, or neither.
                         *
                         * If the suit of the trick and trump suit are the same, a card with
                         * the suit of both gets added to both array lists.
                         */
                        for (Card card : cards) {
                            // TODO: Need to fix the if statements to make them more readable.

                            // Checks if the card is the same suit as the suit of the trick.
                            if (card.getSuit().equals(state.getCenterDeck().getCards().get(0).getSuit())){
                               suitCards.add(card);
                            }

                            // Checks if the card is the same suit as the trump suit.
                            if (card.getSuit().equals(state.getTrumpSuit())) {
                                trumpCards.add(card);
                            }

                            // Checks if the suit of the card is neither the suit of the trick or the trump suit.
                            if (!card.getSuit().equals(state.getTrumpSuit()) && !card.getSuit().equals(state.getCenterDeck().getCards().get(0).getSuit())){
                                backUpCards.add(card);
                            }
                        }

                        /*
                         * If there are cards with the same suit as the trick, one is randomly
                         * selected from the array lis to be played.
                         */
                        if (suitCards.size() != 0) {
                            // Selects a random card.
                            num = rnd.nextInt(suitCards.size());
                            playCard = suitCards.get(num);

                            // TODO: Need to send the card to play with the action.
                            game.sendAction(new PinochleActionPlayTrick(this));

                            // Removes the card from the array to avoid duplicates.
                            suitCards.remove(playCard);

                            // If the card is also in the trump suit array, it is removed from there.
                            if (trumpCards.equals(suitCards)) {
                                trumpCards.remove(playCard);
                            }

                            // Ends the loop.
                            validCard = true;

                            /*
                             * Else if there are cards with the same suit as the trump suit, one is
                             * randomly selected from the array list to be played.
                             */
                        } else if (trumpCards.size() != 0) {
                            // Selects a random card.
                            num = rnd.nextInt(trumpCards.size());
                            playCard = trumpCards.get(num);

                            // TODO: Need to send the card to play with the action.
                            game.sendAction(new PinochleActionPlayTrick(this));

                            // Removes the card from the array to avoid duplicates.
                            trumpCards.remove(playCard);

                            // Ends the loop.
                            validCard = true;

                            /*
                             * Otherwise, a random card is selected to be played.
                             */
                        } else {
                            // Selects a random card.
                            num = rnd.nextInt(backUpCards.size());
                            playCard = backUpCards.get(num);

                            // TODO: Need to send the card to play with the action.
                            game.sendAction(new PinochleActionPlayTrick(this));

                            // Removes the card from the array to avoid duplicates.
                            backUpCards.remove(playCard);

                            // Ends the loop.
                            validCard = true;
                        }
                    }
                    break;
            }

        } else {
            return;
        }
    }
}
