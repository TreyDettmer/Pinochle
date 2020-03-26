package edu.up.cs301.pinochle;

import android.util.Log;

import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
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
public class PinochleLocalGame extends LocalGame {

    //the game's state
    PinochleGameState state;

    public PinochleLocalGame()
    {
        Log.i("PinochleLocalGame", "Creating game");
        state = new PinochleGameState();
    }

    @Override
    protected String checkIfGameOver() {
        return null;
    }

    @Override
    protected void sendUpdatedStateTo(GamePlayer p)
    {
        // if there is no state to send, ignore
        if (state == null) {
            return;
        }
    }

    @Override
    protected boolean canMove(int playerIdx) {
        return false;
    }

    @Override
    protected boolean makeMove(GameAction action) {
        return false;
    }
}
