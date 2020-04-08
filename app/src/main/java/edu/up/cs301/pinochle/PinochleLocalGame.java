package edu.up.cs301.pinochle;

import android.util.Log;

import java.lang.reflect.Array;
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

        String winningMessage = "Team %d wins the game with %d points!";
        if (gameState.getPhase() == PinochleGamePhase.CHECK_GAME_OVER) {
            ArrayList<Integer> winningCandidates = new ArrayList<>();
            for (int i = 0; i < gameState.getScoreboard().length; i++) {
                System.out.println(String.format("Team %s has %s points", i, gameState.getScoreboard()[i]));
                if (gameState.getScoreboard()[i] >= 1500) {
                    winningCandidates.add(i);
                }
            }
            System.out.println(winningCandidates.toString());
            if (winningCandidates.size() == 0) {
                System.out.println("Starting new game...");
                gameState.newRound();
                System.out.println("Phase: " + gameState.getPhase().ordinal());
                System.out.println("Turn: " + gameState.getTurn());
                sendAllUpdatedState();
                return null;
            } else if (winningCandidates.size() == 1) {
                int teamIdx = winningCandidates.get(0);
                System.out.println(String.format(winningMessage, teamIdx, gameState.getScoreboard()[teamIdx]));
                return String.format(winningMessage, teamIdx, gameState.getScoreboard()[teamIdx]);
            } else {
                int bidWinner = gameState.getTeam(gameState.getWonBid());
                System.out.println("Bid winner team: " + bidWinner);
                int teamIdx = 0;
                for (int i = 0; i < winningCandidates.size(); i++) {
                    if (winningCandidates.get(i) == bidWinner) teamIdx = winningCandidates.get(i);
                }
                System.out.println(String.format(winningMessage, teamIdx, gameState.getScoreboard()[teamIdx]));
                return String.format(winningMessage, teamIdx, gameState.getScoreboard()[teamIdx]);
            }

        }

        return null;
    }

    @Override
    protected void sendUpdatedStateTo(GamePlayer p)
    {
        // if there is no state to send, ignore
        if (gameState == null) {
            return;
        }
        PinochleGameState copiedGameState = new PinochleGameState(gameState);

        //nullify all cards in other players' hands
        for (int i = 0; i < 4;i++)
        {
            if (i != getPlayerIdx(p))
            {
                copiedGameState.getPlayerDeck(i).nullifyDeck();
                copiedGameState.setPlayerMelds(i,null);
            }
        }
        p.sendInfo(copiedGameState);
    }

    @Override
    protected boolean canMove(int playerIdx) {
        return (playerIdx == gameState.getTurn() && gameState.getPhase() != PinochleGamePhase.CHECK_GAME_OVER);
    }

    @Override
    protected boolean makeMove(GameAction action) {
        PinochleGamePhase phase = gameState.getPhase();
        int playerIdx = getPlayerIdx(action.getPlayer());
        int teammateIdx = gameState.getTeammate(playerIdx);
        int teamIdx = gameState.getTeam(playerIdx);
        if (action instanceof PinochleActionBid) {
            if (phase != PinochleGamePhase.BIDDING) return false;
            if (gameState.getPassed(playerIdx)) return false;
            System.out.println(String.format("Player %d, Teamate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));
            PinochleActionBid actionBid = (PinochleActionBid) action;
            int bid = actionBid.getBid();
            if (bid != 10 && bid != 20) return false;
            if (gameState.getMaxBid() == 0) gameState.addBid(playerIdx, 250);
            gameState.addBid(playerIdx, bid);
            int nextPlayer;
            do {
                nextPlayer = gameState.nextPlayerTurn();
            } while (gameState.getPassed(nextPlayer));
            return true;
        } else if (action instanceof PinochleActionCalculateMelds) {
            if (phase != PinochleGamePhase.MELDING) return false;
            if (gameState.getMelds()[playerIdx].size() != 0) return false;
            System.out.println(String.format("Player %d, Teamate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));

            ArrayList<Meld> melds = Meld.checkMelds(gameState.getPlayerDeck(playerIdx), gameState.getTrumpSuit());
            int meldPoints = Meld.totalPoints(melds);
            gameState.setPlayerMelds(playerIdx, melds);
            gameState.addScore(teamIdx, meldPoints);
            System.out.println("Player " + playerIdx + " has " + meldPoints + " meld points.");
            gameState.nextPlayerTurn();
            if (gameState.isLastPlayer(playerIdx)) {
                if (gameState.canGoSet(gameState.getTeam(gameState.getWonBid()))) {
                    gameState.nextPhase();
                } else {
                    gameState.setPlayerTurn(gameState.getWonBid());
                    gameState.setPreviousTrickWinner(gameState.getWonBid());
                    System.out.println("Previous Trick Winner: " + gameState.getPreviousTrickWinner());
                    gameState.setPhase(PinochleGamePhase.TRICK_TAKING);
                }
            }
            return true;
        } else if (action instanceof PinochleActionChooseTrump) {
            if (phase != PinochleGamePhase.CHOOSE_TRUMP) return false;
            System.out.println(String.format("Player %d, Teamate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));

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
        } else if (action instanceof PinochleActionExchangeCards) {
            if (phase != PinochleGamePhase.EXCHANGE_CARDS) return false;
            System.out.println(String.format("Player %d, Teamate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));

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
            if (phase != PinochleGamePhase.VOTE_GO_SET) return false;
            System.out.println(String.format("Player %d, Teammate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), ((PinochleActionVoteGoSet) action).getVote()));

            PinochleActionVoteGoSet actionVoteGoSet = (PinochleActionVoteGoSet) action;
            int bidWinner = gameState.getWonBid();
            int bidWinnerTeammate = gameState.getTeammate(bidWinner);
            int bidWinnerTeam = gameState.getTeam(bidWinner);

            boolean returnValue;
            if (teamIdx == bidWinnerTeam) {
                if (actionVoteGoSet.getVote()) {
                    gameState.setVoteGoSet(playerIdx);
                }
                returnValue = true;
            } else returnValue = !actionVoteGoSet.getVote();
            if (gameState.isLastPlayer(playerIdx)) {
                boolean vote0 = gameState.getVoteGoSet(bidWinner);
                boolean vote1 = gameState.getVoteGoSet(bidWinnerTeammate);
                if (vote0 && vote1) {
                    System.out.println("Team " + bidWinnerTeam + ": Going set...");
                    gameState.goSet(bidWinnerTeam);
                    gameState.setPhase(PinochleGamePhase.ACKNOWLEDGE_SCORE);
                    gameState.setPlayerTurn(0);
                } else {
                    gameState.setPlayerTurn(gameState.getWonBid());
                    gameState.setPreviousTrickWinner(gameState.getWonBid());
                    System.out.println("Previous Trick Winner: " + gameState.getPreviousTrickWinner());
                    gameState.nextPhase();
                }


            } else gameState.nextPlayerTurn();
            return returnValue;
        } else if (action instanceof PinochleActionPass) {
            if (phase != PinochleGamePhase.BIDDING) return false;
            System.out.println(String.format("Player %d, Teamate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));

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
            if (phase != PinochleGamePhase.TRICK_TAKING) return false;


            PinochleActionPlayTrick actionPlayTrick = (PinochleActionPlayTrick) action;
            Card trick = actionPlayTrick.getTrick();

            if (gameState.getPreviousTrickWinner() == playerIdx) {
                gameState.setLeadTrick(trick);
            }

            Card leadTrick = gameState.getLeadTrick();

            Suit leadTrickSuit = leadTrick.getSuit();
            Suit trumpSuit = gameState.getTrumpSuit();
            Suit trickSuit = trick.getSuit();


            if (!trickSuit.equals(leadTrickSuit) && gameState.playerHasSuit(playerIdx, leadTrickSuit)) {
                return false;
            }
            if (!trickSuit.equals(leadTrickSuit) && !trickSuit.equals(trumpSuit) && gameState.playerHasSuit(playerIdx, trumpSuit)) {
                return false;
            }

            int initSize = gameState.getPlayerDeck(playerIdx).size();
            gameState.addTrickToCenter(playerIdx, trick);
            gameState.removeCardsFromPlayer(playerIdx, trick);

            if (initSize == gameState.getPlayerDeck(playerIdx).size()) return false;

            System.out.println(String.format("Player %d, Teamate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));

            System.out.println("Trick round: " + gameState.getTrickRound());
            System.out.println("Last Player: " +  (gameState.getPreviousTrickWinner() - 1 + players.length) % players.length);
            System.out.println("Leading trick: " + leadTrick);
            System.out.println("Trump: " + gameState.getTrumpSuit());
            System.out.println("Trick is valid");
            System.out.println("Player " + playerIdx + ": Deck size: " + gameState.getPlayerDeck(playerIdx).size());


            if (playerIdx == (gameState.getPreviousTrickWinner() - 1 + players.length) % players.length) {
                int trickWinner = gameState.getTrickWinner();
                System.out.println("Trick winner: " + trickWinner);
                gameState.setPreviousTrickWinner(trickWinner);
                if (gameState.getTrickRound() == 11) {
                    gameState.setLastTrick(trickWinner);
                    gameState.removeAllCardsFromCenter();
                    gameState.calculateFinalScore();
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
            if (phase != PinochleGamePhase.ACKNOWLEDGE_SCORE) return false;
            System.out.println(String.format("Player %d, Teamate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));

            if (gameState.isLastPlayer(playerIdx)) {
                gameState.nextPhase();
                gameState.setPlayerTurn(0);
            }
            gameState.nextPlayerTurn();
            return true;
        }
        return false;
    }

}
