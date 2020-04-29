package edu.up.cs301.pinochle;

import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;

/**
 * Action to increase their bid
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleActionBid extends GameAction {

    private static final long serialVersionUID = 2107100271012188849L;

    //Only allowed increases in bids
    public static final int BID_10 = 10;
    public static final int BID_20 = 20;

    //Bid increase of the player
    private int bid;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     * @param bid the increase in bid, ex: 10 or 20 points
     */
    public PinochleActionBid(GamePlayer player, int bid) {
        super(player);
        this.bid = bid;
    }

    /**
     * Getter for the player's bid increase
     *
     * @return increase in bid for the player
     */
    public int getBid() {
        return bid;
    }

    /**
     * Converts to string for debugging
     *
     * @return String of the action
     */
    public String toString() {
        return String.format("bid=%s", bid);
    }

}
