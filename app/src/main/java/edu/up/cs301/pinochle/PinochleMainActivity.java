package edu.up.cs301.pinochle;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.gameConfiguration.GameConfig;
import edu.up.cs301.game.GameFramework.gameConfiguration.GamePlayerType;
import edu.up.cs301.game.R;

/**
 * Menu to setup the game
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public class PinochleMainActivity extends GameMainActivity {
    public static final int PORT_NUMBER = 8753;

    /** a pinochle game for four players. The default is human vs. computer */
    @Override
    public GameConfig createDefaultConfig() {
        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();
        playerTypes.add(new GamePlayerType("Human Player") {
            public GamePlayer createPlayer(String name) {
                return new PinochleHumanPlayer(name);
            }});
        playerTypes.add(new GamePlayerType("Dumb Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new PinochleDumbComputerPlayer(name);
            }});
        playerTypes.add(new GamePlayerType("Smart Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new PinochleSmartComputerPlayer(name);
            }});

        // Create a game configuration class for Pinochle
        GameConfig defaultConfig = new GameConfig(playerTypes, 4, 4, "Pinochle", PORT_NUMBER);

        // Add the default players
        defaultConfig.addPlayer("Human", 0);
        defaultConfig.addPlayer("Computer 1", 1);
        defaultConfig.addPlayer("Computer 2", 1);
        defaultConfig.addPlayer("Computer 3", 1);

        // Set the initial information for the remote player
        defaultConfig.setRemoteData("Guest", "", 1);

        //done!
        return defaultConfig;
    }//createDefaultConfig

    @Override
    public LocalGame createLocalGame() {
        return new PinochleLocalGame();
    }

}
