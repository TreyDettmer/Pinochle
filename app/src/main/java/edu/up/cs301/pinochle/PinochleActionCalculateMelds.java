package edu.up.cs301.pinochle;

import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;

/**
 * Action to calculate a player's melds
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleActionCalculateMelds extends GameAction {

    private static final long serialVersionUID = 543454354545265L;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public PinochleActionCalculateMelds(GamePlayer player) {
        super(player);
    }

    /**
     * Converts to string for debugging
     *
     * @return String of the action
     */
    public String toString() {
        return "";
    }
}
