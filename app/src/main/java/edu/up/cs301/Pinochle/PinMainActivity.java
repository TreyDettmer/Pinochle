package edu.up.cs301.Pinochle;

import android.graphics.Color;

import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.gameConfiguration.GameConfig;
import edu.up.cs301.game.GameFramework.gameConfiguration.GamePlayerType;

public class PinMainActivity extends GameMainActivity {
    public static final int PORT_NUMBER = 4753;

    /** a slapjack game for two players. The default is human vs. computer */
    @Override
    public GameConfig createDefaultConfig() {
        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();
        playerTypes.add(new GamePlayerType("human player") {
            public GamePlayer createPlayer(String name) {
                return new PinHumanPlayer(name);
            }});
        playerTypes.add(new GamePlayerType("computer player dumb") {
            public GamePlayer createPlayer(String name) {
                return new PinDumbComputerPlayer(name);
            }});

        // Create a game configuration class for SlapJack
        GameConfig defaultConfig = new GameConfig(playerTypes, 2, 4, "Pinochle", PORT_NUMBER);

        // Add the default players
        defaultConfig.addPlayer("Human", 0);
        defaultConfig.addPlayer("Computer", 1);

        // Set the initial information for the remote player
        defaultConfig.setRemoteData("Guest", "", 1);

        //done!
        return defaultConfig;
    }//createDefaultConfig

    @Override
    public LocalGame createLocalGame() {
        return new PinLocalGame();
    }
}
