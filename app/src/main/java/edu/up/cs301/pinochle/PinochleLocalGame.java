package edu.up.cs301.pinochle;

import android.util.Log;

import java.util.ArrayList;

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
        return (playerIdx == gameState.getTurn());
    }

    @Override
    protected boolean makeMove(GameAction action) {
        int playerIdx = getPlayerIdx(action.getPlayer());
        int teammateIdx = gameState.getTeammate(playerIdx);
        int teamIdx = gameState.getTeam(playerIdx);
        System.out.println(String.format("Player %d, Teamate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));
        if (action instanceof PinochleActionBid) {
            if (gameState.getPhase() != 1) return false;
            if (gameState.getPassed(playerIdx)) return false;
            PinochleActionBid actionBid = (PinochleActionBid) action;
            int bid = actionBid.getBid();
            if (bid != 10 && bid != 20) return false;
            gameState.addBid(playerIdx, bid);
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
            return true;
        } else if (action instanceof PinochleActionChooseTrump) {
            if (gameState.getPhase() != 2) return false;
            System.out.println("Won bid: " + gameState.getWonBid());
            if (gameState.getWonBid() != playerIdx) return false;
            PinochleActionChooseTrump actionChooseTrump = (PinochleActionChooseTrump) action;
            Suit trump = actionChooseTrump.getTrump();
            gameState.setTrumpSuit(trump);
            gameState.setPlayerTurn(teammateIdx);
            gameState.nextPhase();
            System.out.println("Next turn: " + gameState.getTurn());
            System.out.println("Next phase: " + gameState.getPhase());

            return true;
        } else if (action instanceof PinochleActionDealCards) {
            if (gameState.getPhase() != 0) return false;
            gameState.addCardsToPlayer(playerIdx, gameState.dealCards());
            if (gameState.isLastPlayer(playerIdx)) {
                gameState.setFirstBidder((gameState.getFirstBidder() + 1) % PinochleGameState.NUM_PLAYERS);
                gameState.nextPhase();
            }
            gameState.nextPlayerTurn();
            return true;
        } else if (action instanceof PinochleActionExchangeCards) {
            if (gameState.getPhase() != 3) return false;
            int bidWinner = gameState.getWonBid();
            if (playerIdx != bidWinner && playerIdx != gameState.getTeammate(bidWinner)) return false;
            PinochleActionExchangeCards actionExchangeCards = (PinochleActionExchangeCards) action;
            Card[] exchangedCards = actionExchangeCards.getCards();
            gameState.removeCardsFromPlayer(playerIdx, exchangedCards);
            gameState.addCardsToPlayer(teammateIdx, exchangedCards);
            if (playerIdx == bidWinner) {
                gameState.nextPhase();
                gameState.setPlayerTurn(0);
            } else {
                gameState.setPlayerTurn(bidWinner);
            }
            return true;
        } else if (action instanceof PinochleActionVoteGoSet) {
            if (gameState.getPhase() != 5) return false;
            PinochleActionVoteGoSet actionVoteGoSet = (PinochleActionVoteGoSet) action;
            boolean returnValue;
            if (gameState.canGoSet(teamIdx)) {
                if (actionVoteGoSet.getVote()) {
                    gameState.setVoteGoSet(playerIdx);
                }
                returnValue = true;
            } else returnValue = !actionVoteGoSet.getVote();
            if (gameState.isLastPlayer(playerIdx)) {
                gameState.setPlayerTurn(gameState.getWonBid());
                gameState.setPreviousTrickWinner(gameState.getWonBid());
                gameState.nextPhase();
            } else gameState.nextPlayerTurn();
            return returnValue;
        } else if (action instanceof PinochleActionPass) {
            if (gameState.getPhase() != 1) return false;
            gameState.setPassed(playerIdx);
            int nextPlayer;
            do {
                nextPlayer = gameState.nextPlayerTurn();
            } while (gameState.getPassed(nextPlayer));
            System.out.println("Next play not passed: " + gameState.getTurn());
            if (gameState.countPassed() == 3) {
                int maxBid = gameState.getMaxBid();
                System.out.println("Max bid: " + maxBid);
                if (maxBid == 0) {
                    gameState.setBid(gameState.getFirstBidder(), 250);
                    gameState.setWonBid(gameState.getFirstBidder());
                    gameState.setPlayerTurn(gameState.getFirstBidder());
                } else {
                    gameState.setWonBid(gameState.getTurn());
                }
                gameState.nextPhase();
            }
            return true;
        } else if (action instanceof PinochleActionPlayTrick) {
            if (gameState.getPhase() != 6) return false;
            System.out.println("Trick round: " + gameState.getTrickRound());
            System.out.println("Player " + playerIdx + ": Deck size: " + gameState.getPlayerDeck(playerIdx).size());

            PinochleActionPlayTrick actionPlayTrick = (PinochleActionPlayTrick) action;
            Card trick = actionPlayTrick.getTrick();

            if (gameState.getPreviousTrickWinner() == playerIdx) {
                gameState.setLeadTrick(trick);
                System.out.println("Leading trick: " + trick);
                System.out.println("Trump: " + gameState.getTrumpSuit());
            }

            Card leadTrick = gameState.getLeadTrick();

            Suit leadTrickSuit = leadTrick.getSuit();
            Suit trumpSuit = gameState.getTrumpSuit();
            Suit trickSuit = trick.getSuit();

            System.out.println("Last Player: " +  (gameState.getPreviousTrickWinner() - 1 + PinochleGameState.NUM_PLAYERS) % PinochleGameState.NUM_PLAYERS);

            if (!trickSuit.equals(leadTrickSuit) && gameState.playerHasSuit(playerIdx, leadTrickSuit)) {
                System.out.println("Lead trick suit: " + leadTrickSuit );
                System.out.println("Trick suit: " + trickSuit );
                return false;
            }
            if (!trickSuit.equals(leadTrickSuit) && !trickSuit.equals(trumpSuit) && gameState.playerHasSuit(playerIdx, trumpSuit)) {
                System.out.println("Trump suit: " + trumpSuit );
                System.out.println("Trick suit: " + trickSuit );
                return false;
            }


            gameState.addTrickToCenter(playerIdx, trick);
            gameState.removeCardsFromPlayer(playerIdx, trick);



            if (playerIdx == (gameState.getPreviousTrickWinner() - 1 + PinochleGameState.NUM_PLAYERS) % PinochleGameState.NUM_PLAYERS) {
                int trickWinner = gameState.getTrickWinner();
                System.out.println("Trick winner: " + trickWinner);
                gameState.setPreviousTrickWinner(trickWinner);
                if (gameState.getTrickRound() == 11) {
                    gameState.setLastTrick(trickWinner);
                    gameState.removeAllCardsFromCenter();
                    gameState.nextPhase();
                    gameState.setPlayerTurn(0);
                } else {
                    gameState.addTrickToPlayer(playerIdx);
                    gameState.removeAllCardsFromCenter();
                    gameState.nextTrickRound();
                    gameState.setPlayerTurn(trickWinner);
                }
            } else {
                gameState.nextPlayerTurn();

            }
            return true;
        } else if (action instanceof PinochleActionAcknowledgeScore) {
            if (gameState.getPhase() != 7) return false;
        }
        return false;
    }
}
