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
public class PinochleActionBid extends GameAction {

    private static final long serialVersionUID = 2107100271012188849L;

    public static final int BID_10 = 10;
    public static final int BID_20 = 20;

    private int bid;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public PinochleActionBid(GamePlayer player, int bid) {
        super(player);
    }

    public int getBid() {
        return bid;
    }

}
