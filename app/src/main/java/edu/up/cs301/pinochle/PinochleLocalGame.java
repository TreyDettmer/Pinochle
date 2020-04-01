package edu.up.cs301.pinochle;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Meld;
import edu.up.cs301.card.Suit;
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
        int teammateIdx = gameState.getTeammate(playerIdx);
        int teamIdx = gameState.getTeam(playerIdx);
        if (action instanceof PinochleActionBid) {
            if (gameState.getPhase() != 1) return false;
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
        } else if (action instanceof PinochleActionCalculateMelds) {
            if (gameState.getPhase() != 4) return false;
            if (gameState.getMelds()[playerIdx].size() != 0) return false;
            ArrayList<Meld> melds = Meld.checkMelds(gameState.getPlayerDeck(playerIdx), gameState.getTrumpSuit());
            int meldPoints = Meld.totalPoints(melds);
            gameState.setPlayerMelds(playerIdx, melds);
            gameState.addScore(teamIdx, meldPoints);
            gameState.nextPlayerTurn();
            if (gameState.isLastPlayer(playerIdx)) {
                gameState.nextPhase();
            }
        } else if (action instanceof PinochleActionChooseTrump) {
            if (gameState.getPhase() != 2) return false;
            if (gameState.getWonBid() != playerIdx) return false;
            PinochleActionChooseTrump actionChooseTrump = (PinochleActionChooseTrump) action;
            Suit trump = actionChooseTrump.getTrump();
            gameState.setTrumpSuit(trump);
            return true;
        } else if (action instanceof PinochleActionExchangeCards) {
            if (gameState.getPhase() != 3) return false;
            int bidWinner = gameState.getWonBid();
            if (playerIdx != bidWinner && playerIdx != gameState.getTeammate(bidWinner)) return false;
            PinochleActionExchangeCards actionExchangeCards = (PinochleActionExchangeCards) action;
            Card[] exchangedCards = actionExchangeCards.getCards();
            gameState.addCardsToPlayer(teammateIdx, exchangedCards);
            if (playerIdx == bidWinner) {
                gameState.nextPhase();
                gameState.setPlayerTurn(0);
            } else {
                gameState.setPlayerTurn(bidWinner);
            }
            return true;

        } else if (action instanceof PinochleActionGoSet) {
            if (gameState.getPhase() != 5) return false;
            if (gameState.biddingTeamHadLowPoints()) return true;

        } else if (action instanceof PinochleActionPass) {
            if (gameState.getPhase() != 1) return false;
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
            if (gameState.getPhase() != 6) return false;

        }
        return false;
    }
}
