package edu.up.cs301.pinochle;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;

/**
 * Action to play a trick during trick-taking
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleActionPlayTrick extends GameAction {

    private static final long serialVersionUID = 14235348543893323L;

    //Trick played
    private Card trick;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     * @param trick trick that is played
     */
    public PinochleActionPlayTrick(GamePlayer player, Card trick) {
        super(player);
        this.trick = trick;
    }

    /**
     * Getter for the player's trick
     *
     * @return trick of the player
     */
    public Card getTrick() {
        return trick;
    }

    /**
     * Converts to string for debugging
     *
     * @return String of the action
     */
    public String toString() {
        return String.format("trick=%s", trick.toString());
    }

}