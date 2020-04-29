package edu.up.cs301.pinochle;

import org.junit.Test;

import edu.up.cs301.card.Suit;

import static org.junit.Assert.*;

public class PinochleGameStateTest {

    @Test
    public void getFirstBidder() {
        PinochleGameState gameState = new PinochleGameState();
        gameState.setFirstBidder(2);
        assertEquals(gameState.getFirstBidder(), 2);
    }

    @Test
    public void getMaxBid() {
        PinochleGameState gameState = new PinochleGameState();
        gameState.addBid(0, 4);
        assertEquals(gameState.getMaxBid(), 4);
        gameState.addBid(1, 3);
        gameState.addBid(2, 8);
        gameState.addBid(3, -1);
        assertEquals(gameState.getMaxBid(), 8);
    }

    @Test
    public void getTrickRound() {
        PinochleGameState gameState = new PinochleGameState();
        assertEquals(gameState.getTrickRound(), 0);
        gameState.nextTrickRound();
        assertEquals(gameState.getTrickRound(), 1);
    }

    @Test
    public void getTrumpSuit() {
        PinochleGameState gameState = new PinochleGameState();
        gameState.setTrumpSuit(Suit.Spade);
        assertSame(gameState.getTrumpSuit(), Suit.Spade);
    }

    @Test
    public void getWonBid() {
        PinochleGameState gameState = new PinochleGameState();
        gameState.setWonBid(5);
        assertEquals(gameState.getWonBid(), 5);
    }
}