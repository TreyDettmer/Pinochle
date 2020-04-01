package edu.up.cs301.pinochle;

import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;

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
}
