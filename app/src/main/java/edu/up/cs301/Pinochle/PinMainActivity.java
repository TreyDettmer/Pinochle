package edu.up.cs301.Pinochle;

import android.graphics.Color;

import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.gameConfiguration.GameConfig;
import edu.up.cs301.game.GameFramework.gameConfiguration.GamePlayerType;

public class PinMainActivity extends GameMainActivity {
    public static final int PORT_NUMBER = 4752;

    /** a slapjack game for two players. The default is human vs. computer */
    @Override
    public GameConfig createDefaultConfig() {

        return null;
    }//createDefaultConfig

    @Override
    public LocalGame createLocalGame() {
        return new PinLocalGame();
    }
}
