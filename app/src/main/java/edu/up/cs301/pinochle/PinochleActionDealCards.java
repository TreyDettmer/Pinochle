package edu.up.cs301.pinochle;

import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;

public class PinochleActionDealCards extends GameAction {

    private static final long serialVersionUID = 9583245892359265L;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public PinochleActionDealCards(GamePlayer player) {
        super(player);
    }

    public String toString() {
        return "";
    }
}
