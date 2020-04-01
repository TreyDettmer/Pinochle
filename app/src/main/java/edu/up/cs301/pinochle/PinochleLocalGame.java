package edu.up.cs301.pinochle;

import android.util.Log;

import java.util.Collections;

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
    private PinochleGameState gameState;

    public PinochleLocalGame()
    {
        Log.i("PinochleLocalGame", "Creating game");
        gameState = new PinochleGameState();
    }

    @Override
    protected String checkIfGameOver() {
        return null;
    }

    @Override
    protected void sendUpdatedStateTo(GamePlayer p)
    {
        // if there is no state to send, ignore
        if (gameState == null) {
            return;
        }
        p.sendInfo(gameState);
    }

    @Override
    protected boolean canMove(int playerIdx) {
        return playerIdx == gameState.getTurn();
    }

    @Override
    protected boolean makeMove(GameAction action) {
        int playerIdx = getPlayerIdx(action.getPlayer());
        if (action instanceof PinochleActionBid) {
            PinochleActionBid actionBid = (PinochleActionBid) action;
            int bid = actionBid.getBid();
            if (bid != 10 && bid != 20) return false;
            int maxBid = gameState.getMaxBid();
            bid += maxBid;
            gameState.setBid(playerIdx, bid);
            int nextPlayer;
            do {
                nextPlayer = gameState.nextPlayerTurn();
            } while (gameState.getPassed(nextPlayer));
            return true;
        } else if (action instanceof PinochleActionChooseTrump) {

        } else if (action instanceof PinochleActionExchangeCards) {

        } else if (action instanceof PinochleActionGoSet) {

        } else if (action instanceof PinochleActionPass) {
            gameState.setPassed(playerIdx);
            int nextPlayer;
            do {
                nextPlayer = gameState.nextPlayerTurn();
            } while (gameState.getPassed(nextPlayer));
            if (gameState.countPassed() == 3) {
                int maxBid = gameState.getMaxBid();
                if (maxBid == 0) {
                    gameState.setBid(gameState.getFirstBidder(), 250);
                }
                gameState.setWonBid(gameState.getTurn());
                gameState.nextPhase();
            }
            return true;
        } else if (action instanceof PinochleActionPlayTrick) {

        }
        return false;
    }
}
