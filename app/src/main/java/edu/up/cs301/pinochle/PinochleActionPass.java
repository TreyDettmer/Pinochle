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
public class PinochleActionPass extends GameAction {

    private static final long serialVersionUID = 954564560121874743L;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public PinochleActionPass(GamePlayer player) {
        super(player);
    }
}
