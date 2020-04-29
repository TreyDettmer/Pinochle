package edu.up.cs301.pinochle;

import java.util.ArrayList;
import java.util.Arrays;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;

/**
 * Action that exchanges cards to another player
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleActionExchangeCards extends GameAction {

    private static final long serialVersionUID = 607100275012188749L;

    //Cards that are exchanged
    private Card[] cards;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     * @param cards cards that are exchanged
     */
    public PinochleActionExchangeCards(GamePlayer player, Card[] cards) {
        super(player);
        this.cards = cards;
    }

    /**
     * Getter for the player's cards to exchange
     *
     * @return cards to exchange
     */
    public Card[] getCards() {
        return cards;
    }

    /**
     * Converts to string for debugging
     *
     * @return String of the action
     */
    public String toString() {
        return String.format("cards=%s", Arrays.toString(cards));
    }
}
