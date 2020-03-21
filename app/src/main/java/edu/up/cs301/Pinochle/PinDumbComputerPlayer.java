package edu.up.cs301.Pinochle;

import edu.up.cs301.game.GameFramework.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;

public class PinDumbComputerPlayer extends GameComputerPlayer {
    public PinDumbComputerPlayer(String name)
    {
        super(name);
    }
    @Override
    protected void receiveInfo(GameInfo info) {

    }
}
