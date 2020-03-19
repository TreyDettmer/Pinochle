package edu.up.cs301.Pinochle;

import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;

public class PinLocalGame extends LocalGame {
    @Override
    protected String checkIfGameOver() {
        return null;
    }

    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {

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
