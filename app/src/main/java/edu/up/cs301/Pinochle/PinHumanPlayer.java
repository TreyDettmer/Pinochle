package edu.up.cs301.Pinochle;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import edu.up.cs301.game.GameFramework.GameHumanPlayer;
import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.animation.AnimationSurface;
import edu.up.cs301.game.GameFramework.animation.Animator;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.R;

public class PinHumanPlayer extends GameHumanPlayer implements Animator {

    private PinGameState state;
    private Activity myActivity;
    private AnimationSurface surface;
    private int backgroundColor;
    Paint p;

    public PinHumanPlayer(String name)
    {
        super(name);
        backgroundColor = Color.BLUE;
        p = new Paint();
        p.setColor(Color.RED);
    }

    @Override
    public void receiveInfo(GameInfo info) {

    }

    @Override
    public void setAsGui(GameMainActivity activity)
    {
        // remember the activity
        myActivity = activity;

        // Load the layout resource for the new configuration
        activity.setContentView(R.layout.pin_human_player);

        // link the animator (this object) to the animation surface
        surface = (AnimationSurface) myActivity
                .findViewById(R.id.animation_surface);
        surface.setAnimator(this);

        // if the state is not null, simulate having just received the state so that
        // any state-related processing is done
        if (state != null) {
            receiveInfo(state);
        }
    }

    @Override
    public View getTopView() {
        return null;
    }


    @Override
    protected void initAfterReady() {
        super.initAfterReady();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void gameIsOver(String msg) {
        super.gameIsOver(msg);
    }



    //animator methods
    @Override
    public void tick(Canvas canvas) {
        canvas.drawCircle(100,100,100,p);
    }

    @Override
    public int interval() {
        return 0;
    }

    @Override
    public int backgroundColor() {
        return 0;
    }

    @Override
    public boolean doPause() {
        return false;
    }

    @Override
    public boolean doQuit() {
        return false;
    }

    @Override
    public void onTouch(MotionEvent event) {

    }

}
