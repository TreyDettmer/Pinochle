package edu.up.cs301.Pinochle;

import android.util.Log;

import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;

public class PinLocalGame extends LocalGame {

    //the game's state
    PinGameState state;

    public PinLocalGame()
    {
        Log.i("PinLocalGame", "creating game");
        state = new PinGameState();
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
