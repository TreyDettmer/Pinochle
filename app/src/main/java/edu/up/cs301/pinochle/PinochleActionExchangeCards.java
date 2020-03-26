package edu.up.cs301.pinochle;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;

/*
 * Description
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleActionExchangeCards extends GameAction {

    private static final long serialVersionUID = 607100275012188749L;

    private ArrayList<Card> cards;
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public PinochleActionExchangeCards(GamePlayer player) {
        super(player);
    }
}
