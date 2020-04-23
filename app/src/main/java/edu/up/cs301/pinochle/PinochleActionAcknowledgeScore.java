package edu.up.cs301.pinochle;

import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;

/**
 * Acknowledges the score for the round
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleActionAcknowledgeScore extends GameAction {

    private static final long serialVersionUID = 885344576533789L;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public PinochleActionAcknowledgeScore(GamePlayer player) {
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
