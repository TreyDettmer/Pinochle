package edu.up.cs301.pinochle;

import edu.up.cs301.game.GameFramework.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.NotYourTurnInfo;

/*
 * Description
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleDumbComputerPlayer extends GameComputerPlayer {
    
    public PinochleDumbComputerPlayer(String name)
    {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {

        int phase;

        if (info instanceof PinochleGameState) {

            phase = ((PinochleGameState) info).getPhase();

            switch(phase){

                case 0:
                    break;

                case 1:
                    break;

                case 2:
                    break;

                case 3:
                    break;

                case 4:
                    break;


            }

        } else {
            return;
        }
    }
}
