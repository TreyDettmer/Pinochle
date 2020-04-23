package edu.up.cs301.pinochle;

import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;

/**
 * Action to vote to go set after melding
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleActionVoteGoSet extends GameAction {

    private static final long serialVersionUID = 54356363464368849L;

    //Vote of the player to go set
    private boolean vote;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     * @param vote vote of the player to go set
     */
    public PinochleActionVoteGoSet(GamePlayer player, boolean vote) {
        super(player);
        this.vote = vote;
    }

    /**
     * Getter for the player's vote to go set
     *
     * @return vote of the player to go set
     */
    public boolean getVote() {
        return vote;
    }

    /**
     * Converts to string for debugging
     *
     * @return String of the action
     */
    public String toString() {
        return String.format("vote=%s", true);
    }

}
