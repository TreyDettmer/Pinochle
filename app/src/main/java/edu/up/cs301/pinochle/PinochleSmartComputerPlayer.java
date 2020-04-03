package edu.up.cs301.pinochle;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Meld;
import edu.up.cs301.card.Rank;
import edu.up.cs301.card.Suit;
import edu.up.cs301.game.GameFramework.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.IllegalMoveInfo;

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
    private Card[] losingCards;
    private Deck theoreticalDeck;
    private Deck deck;
    private PinochleGameState state;

    public PinochleSmartComputerPlayer(String name)
    {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if (info instanceof IllegalMoveInfo) {
            System.out.println("Illegal move");
        }
        int phase;

        if (info instanceof PinochleGameState){
            state = (PinochleGameState) info;
            phase = state.getPhase();

            deck = state.getPlayerDeck(playerNum);
            theoreticalDeck = findTheoreticalDeck(deck);
            losingCards = findLosingCards(deck);




            switch(phase) {
                // If it is the dealing phase:
                case 0:
                    game.sendAction(new PinochleActionDealCards(this));
                    break;

            }
        }

    }

    private int findPointDifferece(PinochleGameState state) {
        int pointDifference = 0;

        return pointDifference;
    }

    private Card[] findLosingCards(Deck deck) {

        Card[] losingCards = new Card[4];
        /*
        ArrayList<Card> cards = deck.getCards();

        ArrayList<Card> nines = new ArrayList<>();
        ArrayList<Card> tens = new ArrayList<>();
        ArrayList<Card> jacks = new ArrayList<>();
        ArrayList<Card> queens = new ArrayList<>();
        ArrayList<Card> kings = new ArrayList<>();
        ArrayList<Card> aces = new ArrayList<>();
        ArrayList<Card> losingCardsArray;

        int cardsFound = 0;

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            Rank rank = cards.get(i).getRank();
            if (rank.equals(Rank.NINE)) {
                nines.add(card);
            } else if (rank.equals(Rank.TEN)) {
                tens.add(card);
            } else if (rank.equals(Rank.JACK)) {
                jacks.add(card);
            } else if (rank.equals(Rank.QUEEN)) {
                queens.add(card);
            } else if (rank.equals(Rank.KING)) {
                kings.add(card);
            } else {
                aces.add(card);
            }
        }



        while(cardsFound < 4) {
            if (nines.size() != 0) {
                for (int i = 0; i < nines.size(); i++) {
                    if(i >= 3){
                        losingCards[i] = nines.get(i);
                        cardsFound++;
                        break;
                    } else {
                        losingCards[i] = nines.get(i);
                        cardsFound++;
                    }
                }
            } else if (tens.size() != 0) {
                for (int i = 0; i < tens.size(); i++) {
                    if(i >= 3){
                        losingCards[i] = tens.get(i);
                        cardsFound++;
                        break;
                    } else {
                        losingCards[i] = tens.get(i);
                        cardsFound++;
                    }
                }
            } else if (jacks.size() != 0) {
                for (int i = 0; i < jacks.size(); i++) {
                    if(i >= 3){
                        losingCards[i] = jacks.get(i);
                        cardsFound++;
                        break;
                    } else {
                        losingCards[i] = jacks.get(i);
                        cardsFound++;
                    }
                }
            } else if (queens.size() != 0) {
                for (int i = 0; i < queens.size(); i++) {
                    if(i >= 3){
                        losingCards[i] = queens.get(i);
                        cardsFound++;
                        break;
                    } else {
                        losingCards[i] = queens.get(i);
                        cardsFound++;
                    }
                }
            } else if (kings.size() != 0) {
                for (int i = 0; i < kings.size(); i++) {
                    if(i >= 3){
                        losingCards[i] = kings.get(i);
                        cardsFound++;
                        break;
                    } else {
                        losingCards[i] = kings.get(i);
                        cardsFound++;
                    }
                }
            } else {
                for (int i = 0; i < aces.size(); i++) {
                    if(i >= 3){
                        losingCards[i] = aces.get(i);
                        cardsFound++;
                        break;
                    } else {
                        losingCards[i] = aces.get(i);
                        cardsFound++;
                    }
                }
            }
        }

         */
        return losingCards;
    }

    private Card[] findMissingCards(Deck deck) {
        Card[] missingCards = new Card[4];

        return missingCards;
    }

    private Deck findTheoreticalDeck(Deck deck) {
        Deck theoreticalHand = new Deck();

        return theoreticalHand;
    }
}
