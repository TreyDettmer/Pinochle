package edu.up.cs301.pinochle;

import java.io.Serializable;

/**
 * Enum of possible game phases
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
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
