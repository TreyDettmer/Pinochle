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
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Meld;
import edu.up.cs301.card.Suit;
import edu.up.cs301.game.GameFramework.GameHumanPlayer;
import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.animation.AnimationSurface;
import edu.up.cs301.game.GameFramework.animation.Animator;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.IllegalMoveInfo;
import edu.up.cs301.game.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.cs301.game.GameFramework.utilities.Logger;
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
    private static RectF okButtonRect;
    private static RectF voteYesButtonRect;
    private static RectF voteNoButtonRect;
    private static RectF centerDeckRect;
    private static int handCardOffset;
    private static Deck myHand;
    private static ArrayList<Meld> melds;
    static Paint buttonPaint;
    static Paint biddingTextPaint;
    private static Paint trumpChoiceTextPaint;
    private static Paint blackPaint;
    private static Paint highlightPaint;
    private static Paint exchangeButtonTextPaint;
    private static Paint okButtonTextPaint;

    private static Bitmap[] suits;
    private static ArrayList<Card> exchangeCards;
    private static ArrayList<Path> highlightMarkers;
    private boolean voted;
    private boolean voteGoSet;
    private boolean acknowledgePressed;

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
        trumpChoiceTextPaint.setTextAlign(Paint.Align.CENTER);
        buttonPaint.setColor(Color.rgb(97,97,97));
        biddingTextPaint.setColor(Color.WHITE);
        biddingTextPaint.setTextSize(50);
        biddingTextPaint.setTextAlign(Paint.Align.CENTER);
        highlightPaint = new Paint();
        highlightPaint.setColor(Color.YELLOW);
        highlightPaint.setStyle(Paint.Style.FILL);
        exchangeButtonTextPaint = new Paint();
        exchangeButtonTextPaint.setTextSize(40);
        exchangeButtonTextPaint.setColor(Color.WHITE);
        exchangeButtonTextPaint.setTextAlign(Paint.Align.CENTER);
        okButtonTextPaint = new Paint(exchangeButtonTextPaint);
        handFirstCardRect = new RectF(490,770,640,1000);
        biddingButtonRect = new RectF(650,640,830,750);
        trumpSuitChoiceRect = new RectF(550,600,700,750);
        exchangeButtonRect = new RectF(860,600,1060,700);
        okButtonRect = new RectF(860,600,1060,700);
        voteYesButtonRect = new RectF(700,640,860,740);
        voteNoButtonRect = new RectF(1060,640,1220,740);
        centerDeckRect = new RectF(800,400,950,630);

        handCardOffset = 60;
        suits = new Bitmap[4];

    }


    @Override
    public void receiveInfo(GameInfo info) {
        if (info instanceof IllegalMoveInfo) {
            surface.flash(Color.RED, 50);
            String message = null;
            switch (state.getPhase()) {
                case TRICK_TAKING:
                    Card leadTrick = state.getLeadTrick();
                    if (leadTrick == null) return;
                    Suit leadTrickSuit = leadTrick.getSuit();
                    Suit trumpSuit = state.getTrumpSuit();
                    if (state.playerHasSuit(playerNum, leadTrickSuit)) {
                        message = "You have a " + leadTrickSuit + " (lead trick suit), so you have to play it.";
                        break;
                    }
                    if (state.playerHasSuit(playerNum, trumpSuit)) {
                        message = "You have a " + trumpSuit + "  (Trump suit) , so you have to play it.";
                        break;
                    }

            }
            if (message != null) showSnackbar(message);
        }
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
                    myActivity.phaseTextView.setText("Phase: Bidding");
                    myActivity.trumpSuitTextView.setText("");
                    break;
                case CHOOSE_TRUMP:
                    myActivity.phaseTextView.setText("Phase: Choose Trump");
                    myActivity.trumpSuitTextView.setText("");
                    break;
                case EXCHANGE_CARDS:
                    myActivity.phaseTextView.setText("Phase: Card Exchange");
                    myActivity.trumpSuitTextView.setText("Trump: " + state.getTrumpSuit().getName());
                    break;
                case MELDING:
                    myActivity.phaseTextView.setText("Phase: Calculate Melds");
                    game.sendAction(new PinochleActionCalculateMelds(this));
                    voted = false;
                    break;
                case VOTE_GO_SET:
                    myActivity.phaseTextView.setText("Phase: Go Set?");

                    melds = Meld.checkMelds(myHand, state.getTrumpSuit());

                    if (state.getWonBid() != playerNum && state.getWonBid() != teammatePlayerNum)
                    {
                        game.sendAction(new PinochleActionVoteGoSet(this,false));
                        break;
                    }
                    if (voted) {
                        game.sendAction(new PinochleActionVoteGoSet(this, voteGoSet));
                    }

                    break;
                case TRICK_TAKING:
                    myActivity.phaseTextView.setText("Phase: Trick-taking");
                    if (state.getTurn() == playerNum) {
                        if (state.getCenterDeck().getCards().size() == 4) {
                            sleep(1);
                            game.sendAction(new PinochleActionPlayTrick(this, null));
                            break;
                        }
                    }
                    acknowledgePressed = false;
                    break;
                case ACKNOWLEDGE_SCORE:
                    myActivity.phaseTextView.setText("Phase: Scoring");
                    myActivity.trumpSuitTextView.setText("");
                    if (acknowledgePressed) {
                        game.sendAction(new PinochleActionAcknowledgeScore(this));
                    }
                    break;

            }

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
            if (phase == PinochleGamePhase.VOTE_GO_SET || phase == PinochleGamePhase.ACKNOWLEDGE_SCORE) {
                myActivity.humanPlayerInfoTextView.setVisibility(View.INVISIBLE);
                myActivity.leftPlayerNameTextView.setVisibility(View.INVISIBLE);
                myActivity.leftPlayerInfoTextView.setVisibility(View.INVISIBLE);
                myActivity.topPlayerNameTextView.setVisibility(View.INVISIBLE);
                myActivity.topPlayerInfoTextView.setVisibility(View.INVISIBLE);
                myActivity.rightPlayerNameTextView.setVisibility(View.INVISIBLE);
                myActivity.rightPlayerInfoTextView.setVisibility(View.INVISIBLE);
            } else {
                myActivity.humanPlayerInfoTextView.setVisibility(View.VISIBLE);
                myActivity.leftPlayerNameTextView.setVisibility(View.VISIBLE);
                myActivity.leftPlayerInfoTextView.setVisibility(View.VISIBLE);
                myActivity.topPlayerNameTextView.setVisibility(View.VISIBLE);
                myActivity.topPlayerInfoTextView.setVisibility(View.VISIBLE);
                myActivity.rightPlayerNameTextView.setVisibility(View.VISIBLE);
                myActivity.rightPlayerInfoTextView.setVisibility(View.VISIBLE);
            }
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
            else if (state.getPhase() == PinochleGamePhase.TRICK_TAKING)
            {
                if (state.getTurn() != playerToLeftIndex) {myActivity.leftPlayerInfoTextView.setText("[info]");}
                if (state.getTurn() != playerToRightIndex) {myActivity.rightPlayerInfoTextView.setText("[info]");}
                if (state.getTurn() != teammatePlayerNum) {myActivity.topPlayerInfoTextView.setText("[info]");}
                if (state.getTurn() != playerNum) {myActivity.humanPlayerInfoTextView.setText("[info]");}

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
                    if (state.getMaxBid() == 0)
                    {
                        c.drawText("Bid " + (250), 740 + (200 * i), 710, biddingTextPaint);
                    }
                    else {
                        c.drawText("Bid " + (state.getMaxBid() + 10), 740 + (200 * i), 710, biddingTextPaint);
                    }
                }
                else if (i == 1)
                {
                    if (state.getMaxBid() == 0)
                    {
                        c.drawText("Bid " + (260), 740 + (200 * i), 710, biddingTextPaint);
                    }
                    else {
                        c.drawText("Bid " + (state.getMaxBid() + 20), 740 + (200 * i), 710, biddingTextPaint);
                    }
                }
                else
                {
                    c.drawText("Pass", 740 + (200 * i), 710, biddingTextPaint);
                }
            }
        }
    }

    protected void drawMeldingPrompt(Canvas c)
    {
        c.drawText("Calculating melds...", 960, 660, biddingTextPaint);
    }

    protected void drawTrickTakingPrompt(Canvas c)
    {
        int offset = 50;
        for (int i = 0; i < state.getCenterDeck().getCards().size();i++)
        {
            RectF cardRect = new RectF(centerDeckRect.left + (offset * i),centerDeckRect.top,centerDeckRect.right + (offset * i),centerDeckRect.bottom);
            if (state.getCenterDeck().getCards().size() > i) {
                drawCard(c, cardRect, state.getCenterDeck().getCards().get(i));
            }
        }
        System.out.println(state.getCenterDeck().getCards().size());
        if (state.getTurn() == playerNum)
        {
            c.drawText("Choose a card below to play.", 960, 680, biddingTextPaint);
        }
    }

    protected void drawScoringPrompt(Canvas c)
    {
        int team = state.getTeam(playerNum);
        int opposingTeam = (state.getTeam(playerNum) + 1) % PinochleGameState.NUM_TEAMS;
        int bidWinnerTeam = state.getTeam(state.getWonBid());
        int teamScore = state.getScoreboard()[team];
        int opposingScore = state.getScoreboard()[opposingTeam];

        boolean teamWentSet = state.getVoteGoSet(bidWinnerTeam) && state.getVoteGoSet(state.getTeammate(bidWinnerTeam));

        if (teamWentSet) {
            if (team == bidWinnerTeam) c.drawText("Your teammate also voted to go set.", 960, 350, biddingTextPaint);
            else c.drawText("The other team voted to go set.", 960, 350, biddingTextPaint);
        } else {
            int meldScores = state.getMeldsScoreboard()[team];
            int trickScores = state.getTricksScoreboard()[team];
            if (team == bidWinnerTeam && (meldScores + trickScores) < state.getMaxBid()) {
                c.drawText("Your team did not win enough points to meet your bid.", 960, 280, biddingTextPaint);
                c.drawText("Your team went set.", 960, 350, biddingTextPaint);
            } else {
                c.drawText("Congratulations!", 960, 280, biddingTextPaint);
                c.drawText("Your team won enough points to add the points earned to your score!", 960, 350, biddingTextPaint);
            }
        }
        c.drawText("Your team's score: " + teamScore, 960, 420, biddingTextPaint);
        c.drawText("Other team's score: " + opposingScore, 960, 490, biddingTextPaint);

        if (teamScore >= 1500 && opposingScore < 1500) {
            c.drawText("Game over! Your team wins!", 960, 560, biddingTextPaint);
        } else if (teamScore < 1500 && opposingScore >= 1500) {
            c.drawText("Game over! Your team looses.", 960, 560, biddingTextPaint);
        } else if (teamScore >= 1500 && opposingScore >= 1500) {
            if (team == bidWinnerTeam) {
                c.drawText("Game over! Your team wins because your team won the bid! ", 960, 560, biddingTextPaint);
            } else {
                c.drawText("Game over! Your team looses because your team lost the bid! ", 960, 560, biddingTextPaint);
            }
        } else {
            c.drawText("No team has won at least 1500 points. Starting new round...", 960, 560, biddingTextPaint);
        }

        if (!acknowledgePressed) {
            c.drawRect(okButtonRect, buttonPaint);
            c.drawText("OK",960,660, okButtonTextPaint);
        } else {
            c.drawText("Waiting for other players...", 960, 660, biddingTextPaint);
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
        if (state.getWonBid() == playerNum || state.getWonBid() == teammatePlayerNum) {
            int meldPoints = Meld.totalPoints(melds);
            int maxBid = state.getMaxBid();
            c.drawText("Your team bid " + maxBid + " points.", 960, 370, biddingTextPaint);
            c.drawText("Your team scored " + state.getMeldsScoreboard()[state.getTeam(playerNum)] + " points.", 960, 440, biddingTextPaint);
            c.drawText("Do you want to go set?", 960, 510, biddingTextPaint);
            c.drawText("NOTE: The game continues only if both teammate votes yes.", 960, 580, biddingTextPaint);
            if (!voted) {
                c.drawRect(voteYesButtonRect, buttonPaint);
                c.drawRect(voteNoButtonRect, buttonPaint);
                c.drawText("Yes", 780, 710, biddingTextPaint);
                c.drawText("No", 1140, 710, biddingTextPaint);
            } else {
                c.drawText("Waiting for other players...", 960, 670, biddingTextPaint);
            }

        } else {
            c.drawText("Waiting for other players...", 960, 670, biddingTextPaint);
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
            if (playerNum == state.getWonBid()) c.drawText("You won the bid!",960,480,biddingTextPaint);
            else c.drawText("Your teammate won the bid!",960,480,biddingTextPaint);
            c.drawText("Choose four cards to exchange.",960,550,biddingTextPaint);
            c.drawRect(exchangeButtonRect,buttonPaint);
            c.drawText("Exchange",960,660,exchangeButtonTextPaint);

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

            c.drawText("Choose Trump Suit", 960, 530, trumpChoiceTextPaint);
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
            voted = true;
            if (voteYesButtonRect.contains(x, y)) {
                game.sendAction(new PinochleActionVoteGoSet(this, true));
                voteGoSet = true;
            } else if (voteNoButtonRect.contains(x, y)) {
                game.sendAction(new PinochleActionVoteGoSet(this, false));
                voteGoSet = false;

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

                        boolean validCard = true;
                        /*
                        int xPoint = (int)cardRect.left + 20;
                        int yPoint = (int)cardRect.top - 20;
                        for (Path p : highlightMarkers)
                        {
                            RectF bounds = new RectF();
                            p.computeBounds(bounds,false);
                            if (bounds.contains(xPoint,yPoint))
                            {
                                validCard = false;
                                break;
                            }
                        }

                         */
                        if (validCard) {
                            int cardIndex = -1;
                            for (int j = 0; j < exchangeCards.size(); j++) {
                                if (exchangeCards.get(j) == chosenCard) cardIndex = j;
                            }

                            if (cardIndex == -1) {
                                if (exchangeCards.size() < 4) {
                                    exchangeCards.add(chosenCard);
                                    Path highlightMarker = new Path();
                                    highlightMarker.moveTo(cardRect.left + 10,cardRect.top - 30);
                                    highlightMarker.rLineTo(20,20);
                                    highlightMarker.rLineTo(20,-20);
                                    highlightMarkers.add(highlightMarker);
                                } else {
                                    showSnackbar("You've chosen four already. Tap a previously chosen one to deselect it.");
                                }
                            } else {
                                highlightMarkers.remove(cardIndex);
                                exchangeCards.remove(cardIndex);
                            }

                        }


                    }
                    else if (state.getPhase() == PinochleGamePhase.TRICK_TAKING)
                    {
                        game.sendAction(new PinochleActionPlayTrick(this,chosenCard));
                    }
                    return chosenCard;

                }
            }

        }
        return null;
    }

    protected void checkIfAcknowledgeButtonTouched(int x, int y)
    {
        if (state.getPhase() == PinochleGamePhase.ACKNOWLEDGE_SCORE)
        {
            if (exchangeButtonRect.contains(x,y))
            {
                acknowledgePressed = true;
                game.sendAction(new PinochleActionAcknowledgeScore(this));
            }

        }
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
                        showSnackbar("You need to choose four cards.");
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
            case MELDING:
                drawMeldingPrompt(canvas);
                break;
            case VOTE_GO_SET:
                drawVoteGoSetPrompt(canvas);
                break;
            case TRICK_TAKING:
                drawTrickTakingPrompt(canvas);
                break;
            case ACKNOWLEDGE_SCORE:
                drawScoringPrompt(canvas);
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
                    break;
                case TRICK_TAKING:
                    Card touchedCard1 = checkIfTouchedCard(x,y);
                    break;
                case ACKNOWLEDGE_SCORE:
                    checkIfAcknowledgeButtonTouched(x,y);
                    break;
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

    private void showSnackbar(String message) {
        Snackbar.make(myActivity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Sleeps for a particular amount of time. Utility method.
     *
     * @param seconds
     * 			the number of seconds to sleep for
     */
    protected void sleep(double seconds) {
        long milliseconds;

        //Since Thread.sleep takes in milliseconds, convert from seconds to milliseconds
        milliseconds = (long)(seconds * 1000);

        try {

            Thread.sleep(milliseconds);

        }
        catch (InterruptedException e) {

        }
    }

}
