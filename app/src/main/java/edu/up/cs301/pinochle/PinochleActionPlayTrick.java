package edu.up.cs301.pinochle;


import edu.up.cs301.card.Card;
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
public class PinochleActionPlayTrick extends GameAction {

    private static final long serialVersionUID = 14235348543893323L;

    private Card trick;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public PinochleActionPlayTrick(GamePlayer player, Card trick) {
        super(player);
        this.trick = trick;
    }

    public Card getTrick() {
        return trick;
    }

    public String toString() {
        return String.format("trick=%s", trick.toString());
    }

}