package edu.up.cs301.pinochle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Meld;
import edu.up.cs301.card.Suit;
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
    private PinochleMainActivity myActivity;
    private AnimationSurface surface;
    private int backgroundColor;
    private int teammatePlayerNum;
    private int playerToLeftIndex;
    private int playerToRightIndex;
    private static RectF handFirstCardRect;
    private static RectF biddingButtonRect;
    private static RectF trumpSuitChoiceRect;
    private static RectF exchangeButtonRect;
    private static RectF voteYesButtonRect;
    private static RectF voteNoButtonRect;
    private static int handCardOffset;
    private static Deck myHand;
    private static ArrayList<Meld> melds;
    static Paint buttonPaint;
    static Paint biddingTextPaint;
    private static Paint trumpChoiceTextPaint;
    private static Paint blackPaint;
    private static Paint highlightPaint;
    private static Paint exchangeButtonTextPaint;
    private static Bitmap[] suits;
    private static ArrayList<Card> exchangeCards;
    private static ArrayList<Path> highlightMarkers;

    public PinochleHumanPlayer(String name)
    {
        super(name);
        initializeGUI();
        exchangeCards = new ArrayList<>();
        highlightMarkers = new ArrayList<>();


        if (playerNum == 0)
        {
            playerToRightIndex = 3;
            playerToLeftIndex = 1;
        }
        else if (playerNum == 1)
        {
            playerToRightIndex = 0;
            playerToLeftIndex = 2;
        }
        else if (playerNum == 2)
        {
            playerToLeftIndex = 3;
            playerToRightIndex = 1;
        }
        else
        {
            playerToLeftIndex = 0;
            playerToRightIndex = 2;
        }


    }

    public void initializeGUI()
    {
        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        backgroundColor = Color.rgb(1,143,39);
        buttonPaint = new Paint();
        biddingTextPaint = new Paint();
        trumpChoiceTextPaint = new Paint();
        trumpChoiceTextPaint.setColor(Color.WHITE);
        trumpChoiceTextPaint.setTextSize(60);
        buttonPaint.setColor(Color.rgb(97,97,97));
        biddingTextPaint.setColor(Color.WHITE);
        biddingTextPaint.setTextSize(50);
        highlightPaint = new Paint();
        highlightPaint.setColor(Color.YELLOW);
        highlightPaint.setStyle(Paint.Style.FILL);
        exchangeButtonTextPaint = new Paint();
        exchangeButtonTextPaint.setTextSize(40);
        exchangeButtonTextPaint.setColor(Color.WHITE);
        handFirstCardRect = new RectF(490,770,640,1000);
        biddingButtonRect = new RectF(660,640,820,750);
        trumpSuitChoiceRect = new RectF(550,600,700,750);
        exchangeButtonRect = new RectF(1100,600,1300,700);
        voteYesButtonRect = new RectF(1000,600,1150,700);
        voteNoButtonRect = new RectF(1180,600,1330,700);

        handCardOffset = 60;
        suits = new Bitmap[4];

    }


    @Override
    public void receiveInfo(GameInfo info) {
        if (info instanceof PinochleGameState)
        {
            state = (PinochleGameState) info;
            teammatePlayerNum = state.getTeammate(playerNum);
            PinochleGamePhase phase = state.getPhase();
            myActivity.leftPlayerNameTextView.setText(allPlayerNames[playerToLeftIndex]);
            myActivity.rightPlayerNameTextView.setText(allPlayerNames[playerToRightIndex]);
            myActivity.topPlayerNameTextView.setText(allPlayerNames[teammatePlayerNum]);
            updateGUI(phase);

            switch (phase) {
                case BIDDING:
                    myActivity.phaseTextView.setText("Phase: Bid");

                    break;
                case CHOOSE_TRUMP:
                    myActivity.phaseTextView.setText("Phase: Choose Trump");
                    break;
                case EXCHANGE_CARDS:
                    myActivity.phaseTextView.setText("Phase: Card Exchange");
                    myActivity.trumpSuitTextView.setText("Trump: " + state.getTrumpSuit().getName());
                    break;
                case MELDING:
                    myActivity.phaseTextView.setText("Phase: Calculate Melds");
                    game.sendAction(new PinochleActionCalculateMelds(this));
                    break;
                case VOTE_GO_SET:
                    melds = Meld.checkMelds(myHand, state.getTrumpSuit());
                    myActivity.phaseTextView.setText("Phase: Go Set?");
                    break;
                case TRICK_TAKING:
                    myActivity.phaseTextView.setText("Phase: Trick");

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
        myActivity = (PinochleMainActivity)activity;

        // Load the layout resource for the new configuration
        activity.setContentView(R.layout.pinochle_human_player);
        myActivity.initializeGui();
        initSuitImages();


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

    /**
     * updates the GUI
     *
     */
    protected void updateGUI(PinochleGamePhase phase)
    {
        if (state != null)
        {
            int turn = state.getTurn();
            if (turn == playerToLeftIndex)
            {
                myActivity.leftPlayerInfoTextView.setText("[my turn]");
            }
            else if (turn == teammatePlayerNum)
            {
                myActivity.topPlayerInfoTextView.setText("[my turn]");
            }
            else if (turn == playerToRightIndex)
            {
                myActivity.rightPlayerInfoTextView.setText("[my turn]");
            }
            else
            {
                myActivity.humanPlayerInfoTextView.setText("[your turn]");
            }
            if (phase == PinochleGamePhase.BIDDING || phase == PinochleGamePhase.CHOOSE_TRUMP)
            {
                if (state.getPassed(playerToRightIndex))
                {
                    myActivity.rightPlayerInfoTextView.setText("[passed]");
                }
                else
                {
                    if (turn != playerToRightIndex)
                    {
                        if (state.getBids()[playerToRightIndex] != 0) {
                            myActivity.rightPlayerInfoTextView.setText("[Bid " + state.getBids()[playerToRightIndex] + "]");
                        }
                    }
                }
                if (state.getPassed(teammatePlayerNum))
                {
                    myActivity.topPlayerInfoTextView.setText("[passed]");
                }
                else
                {
                    if (turn != teammatePlayerNum)
                    {
                        if (state.getBids()[teammatePlayerNum] != 0) {
                            myActivity.topPlayerInfoTextView.setText("[Bid " + state.getBids()[teammatePlayerNum] + "]");
                        }
                    }
                }
                if (state.getPassed(playerToLeftIndex))
                {
                    myActivity.leftPlayerInfoTextView.setText("[passed]");
                }
                else
                {
                    if (turn != playerToLeftIndex)
                    {
                        if (state.getBids()[playerToLeftIndex] != 0) {
                            myActivity.leftPlayerInfoTextView.setText("[Bid " + state.getBids()[playerToLeftIndex] + "]");
                        }
                    }
                }
                if (state.getPassed(playerNum))
                {
                    myActivity.humanPlayerInfoTextView.setText("[passed]");
                }
                else
                {
                    if (turn != playerNum)
                    {
                        if (state.getBids()[playerNum] != 0) {
                            myActivity.humanPlayerInfoTextView.setText("[Bid " + state.getBids()[playerNum] + "]");
                        }
                    }
                }



            }
            else if (state.getPhase() == PinochleGamePhase.MELDING)
            {
                myActivity.leftPlayerInfoTextView.setText("[info]");
                myActivity.topPlayerInfoTextView.setText("[info]");
                myActivity.rightPlayerInfoTextView.setText("[info]");
                myActivity.humanPlayerInfoTextView.setText("[info]");
            }

        }

    }

    /**
     * draws bidding GUI
     *
     * @param c
     *          canvas to draw on
     *
     **/
    protected void drawBiddingOptions(Canvas c)
    {
        if (state.getTurn() == playerNum) {
            for (int i = 0; i < 3; i++) {
                RectF rect = new RectF(biddingButtonRect.left + (200 * i), biddingButtonRect.top, biddingButtonRect.right + (200 * i), biddingButtonRect.bottom);
                c.drawRect(rect, buttonPaint);
                if (i == 0) {
                    c.drawText("Bid " + (state.getMaxBid() + 10), 670 + (200 * i), 710, biddingTextPaint);
                }
                else if (i == 1)
                {
                    c.drawText("Bid " + (state.getMaxBid() + 20), 670 + (200 * i), 710, biddingTextPaint);
                }
                else
                {
                    c.drawText("Pass", 670 + (200 * i), 710, biddingTextPaint);
                }
            }
        }
    }

    /**
     * draws the vote to go set prompt
     *
     * @param c
     * 		canvas to draw on
     */
    protected void drawVoteGoSetPrompt(Canvas c)
    {


        if (state.getWonBid() == playerNum || state.getWonBid() == teammatePlayerNum)
        {
            int meldPoints = Meld.totalPoints(melds);
            int maxBid = state.getMaxBid();
            c.drawText("Your team bid " + maxBid + " points.",560,470,biddingTextPaint);
            c.drawText("Your team scored " + 100 + " points.",560,540,biddingTextPaint);
            c.drawText("Do you want to go set?",460,670,biddingTextPaint);
            c.drawRect(voteYesButtonRect,buttonPaint);
            c.drawRect(voteNoButtonRect,buttonPaint);
            c.drawText("Yes",1030,670,biddingTextPaint);
            c.drawText("No",1225,670,biddingTextPaint);

        }

    }


    /**
     * draws the card exchange prompt
     *
     * @param c
     * 		canvas to draw on
     */
    protected void drawExchangeCardsPrompt(Canvas c)
    {
        if (state.getTurn() == playerNum)
        {
            for (Path marker : highlightMarkers)
            {
                c.drawPath(marker,highlightPaint);
            }
            c.drawText("Choose 4 cards to exchange.",460,670,biddingTextPaint);
            c.drawRect(exchangeButtonRect,buttonPaint);
            c.drawText("Exchange",1110,660,exchangeButtonTextPaint);

        }
    }

    /**
     * draws the card suits
     *
     * @param c
     * 		canvas to draw on
     */
    protected void drawTrumpChoice(Canvas c)
    {
        if (state.getTurn() == playerNum)
        {
            c.drawText("Choose Trump Suit", 650, 530, trumpChoiceTextPaint);
            for (int i = 0; i < 4; i++) {
                RectF rect = new RectF(trumpSuitChoiceRect.left + (200 * i), trumpSuitChoiceRect.top, trumpSuitChoiceRect.right + (200 * i), trumpSuitChoiceRect.bottom);
                Rect r = new Rect(0,0, suits[i].getWidth(), suits[i].getHeight());
                c.drawBitmap(suits[i],r,rect,blackPaint);
            }
        }
    }

    /**
     * checks if suit image was touched
     *
     * @param x
     * 		the x coordinate of the touch
     * @param y
     * 		the y coordinate of the touch
     */
    protected void checkIfTouchedSuit(int x, int y)
    {
        if (state.getTurn() == playerNum) {
            RectF clubsRect = new RectF(trumpSuitChoiceRect.left + (200 * 0), trumpSuitChoiceRect.top, trumpSuitChoiceRect.right + (200 * 0), trumpSuitChoiceRect.bottom);
            if (clubsRect.contains(x, y)) {
                game.sendAction(new PinochleActionChooseTrump(this, Suit.Club));
            } else if (new RectF(trumpSuitChoiceRect.left + (200 * 1), trumpSuitChoiceRect.top, trumpSuitChoiceRect.right + (200 * 1), trumpSuitChoiceRect.bottom).contains(x, y)) {
                game.sendAction(new PinochleActionChooseTrump(this, Suit.Diamond));
            } else if (new RectF(trumpSuitChoiceRect.left + (200 * 2), trumpSuitChoiceRect.top, trumpSuitChoiceRect.right + (200 * 2), trumpSuitChoiceRect.bottom).contains(x, y)) {
                game.sendAction(new PinochleActionChooseTrump(this, Suit.Heart));
            } else if (new RectF(trumpSuitChoiceRect.left + (200 * 3), trumpSuitChoiceRect.top, trumpSuitChoiceRect.right + (200 * 3), trumpSuitChoiceRect.bottom).contains(x, y)) {
                game.sendAction(new PinochleActionChooseTrump(this, Suit.Spade));
            }
        }

    }

    protected void checkIfTouchedVoteButton(int x, int y)
    {
        if (state.getWonBid() == playerNum || state.getWonBid() == teammatePlayerNum) {
            if (voteYesButtonRect.contains(x, y)) {
                game.sendAction(new PinochleActionVoteGoSet(this, true));
            } else if (voteNoButtonRect.contains(x, y)) {
                game.sendAction(new PinochleActionVoteGoSet(this, false));
            }
        }
    }


    /**
     * checks if a card in the player's hand was touched
     *
     * @param x
     * 		the x coordinate of the touch
     * @param y
     * 		the y coordinate of the touch
     * @return
     *      the touched card
     */
    protected Card checkIfTouchedCard(int x, int y)
    {
        if (state.getTurn() == playerNum) {

            for (int i = myHand.size() - 1; i >= 0; i--) {
                RectF cardRect = new RectF(handFirstCardRect.left + (handCardOffset * (i + 1)), handFirstCardRect.top, handFirstCardRect.right + (handCardOffset * (i + 1)), handFirstCardRect.bottom);
                if (cardRect.contains(x, y)) {
                    Card chosenCard = myHand.getCards().get(i);
                    if (state.getPhase() == PinochleGamePhase.EXCHANGE_CARDS)
                    {
                        if (!exchangeCards.contains(chosenCard))
                        {
                            exchangeCards.add(chosenCard);
                            if (exchangeCards.size() > 4)
                            {
                                exchangeCards.remove(0);
                                highlightMarkers.remove(0);
                            }
                            Path highlightMarker = new Path();
                            highlightMarker.moveTo(cardRect.left + 10,cardRect.top - 30);
                            highlightMarker.rLineTo(20,20);
                            highlightMarker.rLineTo(20,-20);
                            highlightMarkers.add(highlightMarker);
                        }

                    }
                    return chosenCard;

                }
            }

        }
        return null;
    }

    protected void checkIfExchangeButtonTouched(int x, int y)
    {
        if (state.getPhase() == PinochleGamePhase.EXCHANGE_CARDS)
        {
            if (state.getTurn() == playerNum)
            {
                if (exchangeButtonRect.contains(x,y))
                {
                    if (exchangeCards.size() == 4)
                    {
                        Card[] cardList = new Card[4];
                        for (int i = 0; i < exchangeCards.size();i++)
                        {
                            cardList[i] = exchangeCards.get(i);
                        }
                        game.sendAction(new PinochleActionExchangeCards(this,cardList));
                        highlightMarkers.clear();
                        exchangeCards.clear();
                    }
                    else
                    {
                        flash(Color.RED,100);
                    }
                }
            }
        }
    }

    /**
     * checks if a bid button was touched
     *
     * @param x
     * 		the x coordinate of the touch
     * @param y
     * 		the y coordinate of the touch
     **/
    protected void checkIfBidButtonTouched(int x, int y)
    {

        if (state.getPhase() == PinochleGamePhase.BIDDING)
        {
            if (state.getTurn() == playerNum)
            {
                if (biddingButtonRect.contains(x,y))
                {
                    game.sendAction(new PinochleActionBid(this, PinochleActionBid.BID_10));
                }
                else if (new RectF(biddingButtonRect.left + (200), biddingButtonRect.top, biddingButtonRect.right + (200), biddingButtonRect.bottom).contains(x,y))
                {
                    game.sendAction(new PinochleActionBid(this, PinochleActionBid.BID_20));
                }
                else if (new RectF(biddingButtonRect.left + (200 * 2), biddingButtonRect.top, biddingButtonRect.right + (200 * 2), biddingButtonRect.bottom).contains(x,y))
                {
                    game.sendAction(new PinochleActionPass(this));
                }
            }
        }

    }

    /**
     * initializes the suit images for the trump suit selection
     */
    protected void initSuitImages()
    {
        suits[0] = BitmapFactory.decodeResource(myActivity.getResources(), R.drawable.suit_c);
        suits[1] = BitmapFactory.decodeResource(myActivity.getResources(), R.drawable.suit_d);
        suits[2] = BitmapFactory.decodeResource(myActivity.getResources(), R.drawable.suit_h);
        suits[3] = BitmapFactory.decodeResource(myActivity.getResources(), R.drawable.suit_s);
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
        canvas.drawColor(backgroundColor);
        myHand = state.getPlayerDeck(playerNum);

        Deck teammateHand = state.getPlayerDeck(teammatePlayerNum);
        //for now all other players' hands are teammates hand
        drawPlayerHands(canvas,teammateHand);
        drawHand(canvas,myHand);
        switch (state.getPhase())
        {
            case BIDDING:
                drawBiddingOptions(canvas);
                break;
            case CHOOSE_TRUMP:
                drawTrumpChoice(canvas);
                break;
            case EXCHANGE_CARDS:
                drawExchangeCardsPrompt(canvas);
                break;
            case VOTE_GO_SET:
                drawVoteGoSetPrompt(canvas);
                break;

        }


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
        // ignore everything except down-touch events
        if (event.getAction() != MotionEvent.ACTION_DOWN) return;

        // get the location of the touch on the surface
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (state != null)
        {
            switch(state.getPhase())
            {
                case BIDDING:
                    checkIfBidButtonTouched(x, y);
                    break;
                case CHOOSE_TRUMP:
                    checkIfTouchedSuit(x,y);
                    break;
                case EXCHANGE_CARDS:
                    Card touchedCard = checkIfTouchedCard(x, y);
                    checkIfExchangeButtonTouched(x,y);
                    break;
                case VOTE_GO_SET:
                    checkIfTouchedVoteButton(x,y);
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
    private static void drawHand(Canvas g, Deck hand){

        if (hand != null) {

            for (int i = 0; i < hand.size();i++){//hand.size(); i++) {
                drawCard(g, new RectF(handFirstCardRect.left + (handCardOffset * (i + 1)), handFirstCardRect.top, handFirstCardRect.right + (handCardOffset * (i + 1)), handFirstCardRect.bottom), hand.getCards().get(i));
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
    private void drawPlayerHands(Canvas g, Deck hand){
        int cardOffset = 40;

        if (hand != null)
        {
            RectF initialRect;

            //left
            initialRect = new RectF(70,190,210,270);
            for (int i = 0; i < state.getPlayerDeck(playerToLeftIndex).size();i++)
            {
                drawCard(g, new RectF(initialRect.left, initialRect.top + (cardOffset * (i + 1)), initialRect.right , initialRect.bottom + (cardOffset * (i + 1))), null);
            }

            // right
            initialRect = new RectF(1710,190,1850,270);
            for (int i = 0; i < state.getPlayerDeck(playerToRightIndex).size();i++)
            {
                drawCard(g, new RectF(initialRect.left, initialRect.top + (cardOffset * (i + 1)), initialRect.right , initialRect.bottom + (cardOffset * (i + 1))), null);
            }
            //top
            initialRect = new RectF(640,30,720,170);
            for (int i = 0; i < state.getPlayerDeck(state.getTeammate(playerNum)).size();i++)
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
