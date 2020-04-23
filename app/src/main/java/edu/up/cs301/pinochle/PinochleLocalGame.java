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
 * PinochleLocalGame - Enforces the rules and manages the game
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

    /**
     * Checks if the game is over
     */
    @Override
    protected String checkIfGameOver() {

        String winningMessage = "Team %d wins the game with %d points!";
        if (gameState.getPhase() == PinochleGamePhase.CHECK_GAME_OVER) {
            //Check for teams with more than 1500 points
            ArrayList<Integer> winningCandidates = new ArrayList<>();
            for (int i = 0; i < gameState.getScoreboard().length; i++) {
                Log.i("PinochleLocalGame", String.format("Team %s has %s points", i, gameState.getScoreboard()[i]));
                if (gameState.getScoreboard()[i] >= 1500) {
                    winningCandidates.add(i);
                }
            }
            Log.i("PinochleLocalGame", winningCandidates.toString());
            //No possible winners
            if (winningCandidates.size() == 0) {
                Log.i("PinochleLocalGame", "Starting new game...");
                gameState.newRound();
                Log.i("PinochleLocalGame", "Phase: " + gameState.getPhase().ordinal());
                Log.i("PinochleLocalGame", "Turn: " + gameState.getTurn());
                sendAllUpdatedState();
                return null;
            //Only one possible winner
            } else if (winningCandidates.size() == 1) {
                int teamIdx = winningCandidates.get(0);
                Log.i("PinochleLocalGame", String.format(winningMessage, teamIdx, gameState.getScoreboard()[teamIdx]));
                return String.format(winningMessage, teamIdx, gameState.getScoreboard()[teamIdx]);
            //Both teams have above 1500 points
            //The team that won the bid wins
            } else {
                int bidWinner = gameState.getTeam(gameState.getWonBid());
                Log.i("PinochleLocalGame", "Bid winner team: " + bidWinner);
                int teamIdx = 0;
                for (int i = 0; i < winningCandidates.size(); i++) {
                    if (winningCandidates.get(i) == bidWinner) teamIdx = winningCandidates.get(i);
                }
                Log.i("PinochleLocalGame", String.format(winningMessage, teamIdx, gameState.getScoreboard()[teamIdx]));
                return String.format(winningMessage, teamIdx, gameState.getScoreboard()[teamIdx]);
            }

        }

        return null;
    }

    /**
    * After a player has played a round and the game has updated the state,
    * the game sends the updated version of the game state to a player
    *
    * @param p player to send state to
    */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p)
    {
        // if there is no state to send, ignore
        if (gameState == null) {
            return;
        }
        PinochleGameState copiedGameState = new PinochleGameState(gameState);

        //nullify all cards in other players' hands
        for (int i = 0; i < 4; i++)
        {
            if (i != getPlayerIdx(p)) {
                copiedGameState.getPlayerDeck(i).nullifyDeck();
                copiedGameState.setPlayerMelds(i,null);
            }
        }
        p.sendInfo(copiedGameState);
    }

    /**
     * Checks if players can move
     *
     * @param playerIdx index of the player
     * @return if player can move
    */
    @Override
    protected boolean canMove(int playerIdx) {
        //is players turn and not in the game over phase
        return (playerIdx == gameState.getTurn() && gameState.getPhase() != PinochleGamePhase.CHECK_GAME_OVER);
    }

    /**
     * Processes the action the player sends
     *
     * @param action GameAction that the player sent
     * @return if move was valid
     */
    @Override
    protected boolean makeMove(GameAction action) {
        PinochleGamePhase phase = gameState.getPhase();
        int playerIdx = getPlayerIdx(action.getPlayer());
        int teammateIdx = gameState.getTeammate(playerIdx);
        int teamIdx = gameState.getTeam(playerIdx);
        if (action instanceof PinochleActionBid) {
            if (phase != PinochleGamePhase.BIDDING) return false;
            if (gameState.getPassed(playerIdx)) return false;
            Log.i("PinochleLocalGame", String.format("Player %d, Teammate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));
            PinochleActionBid actionBid = (PinochleActionBid) action;
            int bid = actionBid.getBid();
            //Bid must be 10 or 20 points above the previous bid
            if (bid != 10 && bid != 20) return false;
            if (gameState.getMaxBid() == 0) gameState.addBid(playerIdx, 240);
            gameState.addBid(playerIdx, bid);
            int nextPlayer;
            //Find the next player who hasn't passed
            do {
                nextPlayer = gameState.nextPlayerTurn();
            } while (gameState.getPassed(nextPlayer));
            return true;
        } else if (action instanceof PinochleActionPass) {
            if (phase != PinochleGamePhase.BIDDING) return false;
            Log.i("PinochleLocalGame", String.format("Player %d, Teammate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));
            gameState.setPassed(playerIdx);
            //Find the next player who hasn't passed
            int nextPlayer;
            do {
                nextPlayer = gameState.nextPlayerTurn();
            } while (gameState.getPassed(nextPlayer));
            Log.i("PinochleLocalGame", "Next player not passed: " + gameState.getTurn());
            //Three players have passed, the other player wins the bid
            if (gameState.countPassed() == 3) {
                int maxBid = gameState.getMaxBid();
                Log.i("PinochleLocalGame", "Max bid: " + maxBid);
                //If everyone passed and does not bid, first bidder automatically bids 250
                if (maxBid == 0) {
                    gameState.setBid(gameState.getFirstBidder(), 250);
                    gameState.setWonBid(gameState.getFirstBidder());
                    gameState.setPlayerTurn(gameState.getFirstBidder());
                    gameState.setFirstBidder((gameState.getFirstBidder() + 1) % PinochleGameState.NUM_PLAYERS);
                } else {
                    gameState.setWonBid(gameState.getTurn());
                }
                gameState.nextPhase();
            }
            return true;
        } else if (action instanceof PinochleActionChooseTrump) {
            if (phase != PinochleGamePhase.CHOOSE_TRUMP) return false;
            Log.i("PinochleLocalGame", String.format("Player %d, Teammate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));
            Log.i("PinochleLocalGame", "Won bid: " + gameState.getWonBid());
            //Player that won the bid chooses the trump of the round
            if (gameState.getWonBid() != playerIdx) return false;
            PinochleActionChooseTrump actionChooseTrump = (PinochleActionChooseTrump) action;
            Suit trump = actionChooseTrump.getTrump();
            gameState.setTrumpSuit(trump);
            gameState.setPlayerTurn(teammateIdx);
            gameState.nextPhase();
            Log.i("PinochleLocalGame", "Next turn: " + gameState.getTurn());
            Log.i("PinochleLocalGame", "Next phase: " + gameState.getPhase());

            return true;
        } else if (action instanceof PinochleActionExchangeCards) {
            if (phase != PinochleGamePhase.EXCHANGE_CARDS) return false;
            Log.i("PinochleLocalGame", String.format("Player %d, Teammate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));
            int bidWinner = gameState.getWonBid();
            //Partner of bid winner exchanges cards first, then bid winner
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
        } else if (action instanceof PinochleActionCalculateMelds) {
            if (phase != PinochleGamePhase.MELDING) return false;
            if (gameState.getMelds()[playerIdx].size() != 0) return false;
            Log.i("PinochleLocalGame", String.format("Player %d, Teammate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));
            //Get list of melds that player has
            ArrayList<Meld> melds = Meld.checkMelds(gameState.getPlayerDeck(playerIdx), gameState.getTrumpSuit());
            //Find the total point value of the melds
            int meldPoints = Meld.totalPoints(melds);
            gameState.setPlayerMelds(playerIdx, melds);
            gameState.addMeldScore(teamIdx, meldPoints);
            Log.i("PinochleLocalGame", "Player " + playerIdx + " has " + meldPoints + " meld points.");
            gameState.nextPlayerTurn();
            //After all players have their melds calculated, the game checks if the bid winner teammates are likely
            //to go set. That is if the points earned from melds are 250 less than their bid.
            if (gameState.isLastPlayer(playerIdx)) {
                if (gameState.canGoSet(gameState.getTeam(gameState.getWonBid()))) {
                    gameState.nextPhase();
                } else {
                    //Player who won the bid plays the first trick
                    gameState.setPlayerTurn(gameState.getWonBid());
                    gameState.setPreviousTrickWinner(gameState.getWonBid());
                    gameState.setPhase(PinochleGamePhase.TRICK_TAKING);
                }
            }
            return true;
        } else if (action instanceof PinochleActionVoteGoSet) {
            if (phase != PinochleGamePhase.VOTE_GO_SET) return false;
            Log.i("PinochleLocalGame", String.format("Player %d, Teammate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), ((PinochleActionVoteGoSet) action).getVote()));
            PinochleActionVoteGoSet actionVoteGoSet = (PinochleActionVoteGoSet) action;
            int bidWinner = gameState.getWonBid();
            int bidWinnerTeammate = gameState.getTeammate(bidWinner);
            int bidWinnerTeam = gameState.getTeam(bidWinner);
            boolean returnValue;
            //Only the bid winner teammates can actually vote to go set
            //The other team must vote False, because they cannot go set
            if (teamIdx == bidWinnerTeam) {
                if (actionVoteGoSet.getVote()) {
                    gameState.setVoteGoSet(playerIdx);
                }
                returnValue = true;
            } else returnValue = !actionVoteGoSet.getVote();
            //Tallying up the votes
            if (gameState.isLastPlayer(playerIdx)) {
                //Only the votes of the bid winner teammmates matter
                boolean vote0 = gameState.getVoteGoSet(bidWinner);
                boolean vote1 = gameState.getVoteGoSet(bidWinnerTeammate);
                if (vote0 && vote1) {
                    //Restart the round, the bid winner team loses their bid from the total score
                    Log.i("PinochleLocalGame", "Team " + bidWinnerTeam + ": Going set...");
                    gameState.goSet(bidWinnerTeam);
                    gameState.setPhase(PinochleGamePhase.ACKNOWLEDGE_SCORE);
                    gameState.setPlayerTurn(0);
                } else {
                    //Continue on with the game
                    gameState.setPlayerTurn(gameState.getWonBid());
                    gameState.setPreviousTrickWinner(gameState.getWonBid());
                    gameState.nextPhase();
                }
            } else gameState.nextPlayerTurn();
            return returnValue;
        } else if (action instanceof PinochleActionPlayTrick) {
            if (phase != PinochleGamePhase.TRICK_TAKING) return false;

            PinochleActionPlayTrick actionPlayTrick = (PinochleActionPlayTrick) action;
            Card trick = actionPlayTrick.getTrick();

            //This action is called after the last player plays their trick to cleanup the trick round
            if (trick == null) {
                if (gameState.getCenterDeck().size() >= 4) {
                    int trickWinner = gameState.getTrickWinner();
                    int trickWinnerTeam = gameState.getTeam(gameState.getTrickWinner());
                    Log.i("PinochleLocalGame", "Trick winner: " + trickWinner);
                    for (Card c : gameState.getCenterDeck().getCards())
                    {
                        Log.i("PinochleLocalGame", c.toString() + " played by Player " + c.getPlayer());
                    }
                    Log.i("PinochleLocalGame", "Winning card: " + gameState.getTrickWinningCard().toString());
                    //The trick winner plays the first next trick
                    gameState.setPreviousTrickWinner(trickWinner);
                    //Tricks points are added to their trick score.
                    gameState.addTrickScore(trickWinnerTeam, gameState.getTrickPoints());
                    gameState.addTrickToPlayer(playerIdx);
                    gameState.removeAllCardsFromCenter();
                    //If it's the last trick round
                    if (gameState.getTrickRound() == 11) {
                        //Cleanup trick round
                        gameState.calculateFinalScore();
                        gameState.nextPhase();
                        gameState.setPlayerTurn(0);
                    } else {
                        gameState.nextTrickRound();
                        gameState.setPlayerTurn(trickWinner);
                    }
                }
                return true;
            }

            //The last trick winner plays the lead trick
            if (gameState.getPreviousTrickWinner() == playerIdx) {
                gameState.setLeadTrick(trick);
            }

            Card leadTrick = gameState.getLeadTrick();

            Suit leadTrickSuit = leadTrick.getSuit();
            Suit trumpSuit = gameState.getTrumpSuit();
            Suit trickSuit = trick.getSuit();

            Log.i("PinochleLocalGame", "Trick: " + trick);

            //The player must play a trick with the lead trick suit if they have it.
            if (!trickSuit.equals(leadTrickSuit) && gameState.playerHasSuit(playerIdx, leadTrickSuit)) {
                return false;
            }

            //If the player does not have a lead trick suit, then they must player a trump suit if they have it.
            if (!trickSuit.equals(leadTrickSuit) && !trickSuit.equals(trumpSuit) && gameState.playerHasSuit(playerIdx, trumpSuit)) {
                return false;
            }

            //Ensures the player plays a card that will win the trick (if they have a card that would do so).
            Card playersWinnableCard = gameState.playerHasWinnableCard(playerIdx);
            if (playersWinnableCard != null) {
                //Player can play a trick with a higher ranking than the winnableCard if its the same suit as the winnableCard
                if (!trick.getSuit().equals(playersWinnableCard.getSuit()) || (trick.getRank().ordinal() < playersWinnableCard.getRank().ordinal())) {
                    return false;
                }
            }

            int initSize = gameState.getPlayerDeck(playerIdx).size();
            gameState.addTrickToCenter(playerIdx, trick);
            gameState.removeCardsFromPlayer(playerIdx, trick);
            //If the players deck was unchanged, the move is invalid.
            if (initSize == gameState.getPlayerDeck(playerIdx).size()) return false;

            Log.i("PinochleLocalGame", String.format("Player %d, Teammate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));
            Log.i("PinochleLocalGame", "Trick round: " + gameState.getTrickRound());
            Log.i("PinochleLocalGame", "Last Player: " +  (gameState.getPreviousTrickWinner() - 1 + players.length) % players.length);
            Log.i("PinochleLocalGame", "Leading trick: " + leadTrick);
            Log.i("PinochleLocalGame", "Trump: " + gameState.getTrumpSuit());
            Log.i("PinochleLocalGame", "Trick is valid");
            Log.i("PinochleLocalGame", "Player " + playerIdx + ": Deck size: " + gameState.getPlayerDeck(playerIdx).size());
            gameState.nextPlayerTurn();

            return true;
        } else if (action instanceof PinochleActionAcknowledgeScore) {
            if (phase != PinochleGamePhase.ACKNOWLEDGE_SCORE) return false;
            Log.i("PinochleLocalGame", String.format("Player %d, Teammate %d: %s, %s", playerIdx, teammateIdx, action.getClass().getName(), action.toString()));

            //All players click a button to acknowledge their score
            if (gameState.isLastPlayer(playerIdx)) {
                gameState.nextPhase();
                gameState.setPlayerTurn(gameState.getFirstBidder());
            }
            gameState.nextPlayerTurn();
            return true;
        }
        return false;
    }

}
