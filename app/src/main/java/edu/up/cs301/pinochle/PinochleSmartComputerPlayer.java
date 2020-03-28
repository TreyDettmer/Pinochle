package edu.up.cs301.pinochle;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.game.GameFramework.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;

/*
 * Description
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleSmartComputerPlayer extends GameComputerPlayer {

    private int bidAllowance;
    private int pointDifference;
    private int pointsToWin;
    private Card[] missingCards;
    private Card[] losingCard;
    private Deck theoreticalHand;

    public PinochleSmartComputerPlayer(String name)
    {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {

    }

    private int findPointDifferece(PinochleGameState gameState) {
        int pointDifference = 0;

        return pointDifference;
    }

    private Card[] findLosingCards(Deck currentHand) {
        Card[] losingCards = new Card[4];

        return losingCards;
    }

    private Card[] findMissingCards(Deck currentHand) {
        Card[] missingCards = new Card[4];

        return missingCards;
    }

    private Deck findTheoreticalHand(Deck currentHand) {
        Deck theoreticalHand = new Deck();

        return theoreticalHand;
    }
}
