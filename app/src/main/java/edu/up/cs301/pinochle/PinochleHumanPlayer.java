package edu.up.cs301.pinochle;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.game.GameFramework.GameHumanPlayer;
import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.animation.AnimationSurface;
import edu.up.cs301.game.GameFramework.animation.Animator;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.R;

/*
 * Description
 *
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @author Steven R. Vegdahl
 * @version March 2020
 */
public class PinochleHumanPlayer extends GameHumanPlayer implements Animator {

    private PinochleGameState state;
    private Activity myActivity;
    private AnimationSurface surface;
    private int backgroundColor;
    private int teammatePlayerNum;
    static Paint p;

    public PinochleHumanPlayer(String name)
    {
        super(name);
        backgroundColor = Color.rgb(0,230,61);

        p = new Paint();
        p.setColor(Color.RED);

        // I already have a method to find someone's teammate. also 0's teammate is 2 and 1's teammate is 3
        /*
        switch (playerNum)
        {
            case 0:
                teammatePlayerNum = 1;
                break;
            case 1:
                teammatePlayerNum = 0;
                break;
            case 2:
                teammatePlayerNum = 3;
                break;
            case 4:
                teammatePlayerNum = 2;
                break;
        }*/


    }

    @Override
    public void receiveInfo(GameInfo info) {
        if (info instanceof PinochleGameState)
        {
            state = (PinochleGameState) info;
            teammatePlayerNum = state.getTeammate(playerNum);
            PinochleGamePhase phase = state.getPhase();
            switch (phase) {
                case BIDDING:
                    break;

            }

        }
        else
        {
            return;
        }
    }

    @Override
    public void setAsGui(GameMainActivity activity)
    {
        // remember the activity
        myActivity = activity;

        // Load the layout resource for the new configuration
        activity.setContentView(R.layout.pinochle_human_player);
        Card.initImages(activity);
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
        if (state == null)
        {
            return;
        }
        Deck hand = state.getPlayerDeck(playerNum);

        Deck teammateHand = state.getPlayerDeck(teammatePlayerNum);
        //for now all other players' hands are teammates hand
        drawPlayerHands(canvas,teammateHand);
        drawHand(canvas,hand);
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

    /**
     * draw the player's hand
     *
     * @param g
     * 		the canvas object
     * @param hand
     * 		the hand
     */
    private static void drawHand(Canvas g, Deck hand){
        int cardOffset = 60;

        RectF initalRect = new RectF(490,770,640,1000);
        if (hand != null) {

            for (int i = 0; i < hand.size();i++){//hand.size(); i++) {
                drawCard(g, new RectF(initalRect.left + (cardOffset * (i + 1)), initalRect.top, initalRect.right + (cardOffset * (i + 1)), initalRect.bottom), hand.getCards().get(i));
            }
        }

    }

    /**
     * draw the player's hand
     *
     * @param g
     * 		the canvas object
     * @param hand
     * 		the hand
     */
    private static void drawPlayerHands(Canvas g, Deck hand){
        int cardOffset = 40;

        if (hand != null)
        {
            RectF initialRect;

            //left
            initialRect = new RectF(70,190,210,270);
            for (int i = 0; i < hand.size();i++)
            {
                drawCard(g, new RectF(initialRect.left, initialRect.top + (cardOffset * (i + 1)), initialRect.right , initialRect.bottom + (cardOffset * (i + 1))), null);
            }

            // right
            initialRect = new RectF(1710,190,1850,270);
            for (int i = 0; i < hand.size();i++)
            {
                drawCard(g, new RectF(initialRect.left, initialRect.top + (cardOffset * (i + 1)), initialRect.right , initialRect.bottom + (cardOffset * (i + 1))), null);
            }
            //top
            initialRect = new RectF(640,30,720,170);
            for (int i = 0; i < hand.size();i++)
            {
                drawCard(g, new RectF(initialRect.left + (cardOffset * (i + 1)), initialRect.top , initialRect.right + (cardOffset * (i + 1)) , initialRect.bottom ), null);
            }



        }

    }


    /**
     * draws a card on the canvas; if the card is null, draw a card-back
     *
     * @param g
     * 		the canvas object
     * @param rect
     * 		a rectangle defining the location to draw the card
     * @param c
     * 		the card to draw; if null, a card-back is drawn
     */
    private static void drawCard(Canvas g, RectF rect, Card c) {
        if (c == null) {
            // null: draw a card-back, consisting of a blue card
            // with a white line near the border. We implement this
            // by drawing 3 concentric rectangles:
            // - blue, full-size
            // - white, slightly smaller
            // - blue, even slightly smaller
            Paint white = new Paint();
            white.setColor(Color.WHITE);
            Paint blue = new Paint();
            blue.setColor(Color.BLUE);
            RectF inner1 = scaledBy(rect, 0.96f); // scaled by 96%
            RectF inner2 = scaledBy(rect, 0.98f); // scaled by 98%
            g.drawRect(rect, blue); // outer rectangle: blue
            g.drawRect(inner2, white); // middle rectangle: white
            g.drawRect(inner1, blue); // inner rectangle: blue
        }
        else {
            // just draw the card
            c.drawOn(g, rect);
        }
    }

    /**
     * scales a rectangle, moving all edges with respect to its center
     *
     * @param rect
     * 		the original rectangle
     * @param factor
     * 		the scaling factor
     * @return
     * 		the scaled rectangle
     */
    private static RectF scaledBy(RectF rect, float factor) {
        // compute the edge locations of the original rectangle, but with
        // the middle of the rectangle moved to the origin
        float midX = (rect.left+rect.right)/2;
        float midY = (rect.top+rect.bottom)/2;
        float left = rect.left-midX;
        float right = rect.right-midX;
        float top = rect.top-midY;
        float bottom = rect.bottom-midY;

        // scale each side; move back so that center is in original location
        left = left*factor + midX;
        right = right*factor + midX;
        top = top*factor + midY;
        bottom = bottom*factor + midY;

        // create/return the new rectangle
        return new RectF(left, top, right, bottom);
    }

    /**
     * Computes the adjusted height. Things are scaled assuming that the width is 3/5 larger
     * than the height. So if the height is less than that, returns width*3/5.
     *
     * @return
     * 	   the adjusted height
     */
    private int getAdjustedHeight() {
        int height = surface.getHeight();
        int width = surface.getWidth();
        int maxHeight = width*3/5;
        if (height > maxHeight) {
            return maxHeight;
        }
        else {
            return height;
        }
    }

    /**
     * Computes the adjusted width. Things are scaled assuming that the width is 5/3 larger
     * than the height. So if the width is less than that, returns height*5/3.
     *
     * @return
     * 	   the adjusted width
     */
    private int getAdjustedWidth() {
        int height = surface.getHeight();
        int width = surface.getWidth();
        int maxWidth = height*5/3;
        if (width > maxWidth) {
            return maxWidth;
        }
        else {
            return width;
        }
    }

}
