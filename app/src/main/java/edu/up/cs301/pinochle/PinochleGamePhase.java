package edu.up.cs301.pinochle;

import java.io.Serializable;

public enum PinochleGamePhase implements Serializable {
    BIDDING,
    CHOOSE_TRUMP,
    EXCHANGE_CARDS,
    MELDING,
    VOTE_GO_SET,
    TRICK_TAKING,
    ACKNOWLEDGE_SCORE,
    CHECK_GAME_OVER
}
