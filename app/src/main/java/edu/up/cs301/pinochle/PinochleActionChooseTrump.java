package edu.up.cs301.pinochle;

import edu.up.cs301.card.Suit;
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
public class PinochleActionChooseTrump extends GameAction {

    private static final long serialVersionUID = 4107153454512188849L;

    private Suit trump;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public PinochleActionChooseTrump(GamePlayer player, Suit trump) {
        super(player);
        this.trump = trump;
    }

    public Suit getTrump() {
        return trump;
    }

    public String toString() {
        return String.format("trump=%s", trump.toString());
    }
}
