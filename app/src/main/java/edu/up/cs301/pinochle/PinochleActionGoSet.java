package edu.up.cs301.pinochle;

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
public class PinochleActionGoSet extends GameAction {

    private static final long serialVersionUID = 54356363464368849L;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public PinochleActionGoSet(GamePlayer player) {
        super(player);
    }
}
