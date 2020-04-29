package edu.up.cs301.pinochle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

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

/**
 * GUI of the human player allows a human player to send move actions to the game
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
    private Button meldsMenuButton;
    private TextView waitingToConnectTextView;
    private TextView leftPlayerInfoTextView;
    private TextView leftPlayerNameTextView;
    private TextView rightPlayerInfoTextView;
    private TextView rightPlayerNameTextView;
    private TextView topPlayerInfoTextView;
    private TextView topPlayerNameTextView;
    private TextView humanPlayerInfoTextView;
    private TextView phaseTextView;
    private TextView bidTextView;
    private TextView trumpSuitTextView;
    private TextView scoreboard;
    private TextView totalTextView;
    private TextView meldsTextView;
    private TextView tricksTextView;
    private TextView team0NameTextView;
    private TextView team0TotalTextView;
    private TextView team0MeldsTextView;
    private TextView team0TricksTextView;
    private TextView team1NameTextView;
    private TextView team1TotalTextView;
    private TextView team1MeldsTextView;
    private TextView team1TricksTextView;
    private RectF handFirstCardRect;
    private RectF biddingButtonRect;
    private RectF trumpSuitChoiceRect;
    private RectF exchangeButtonRect;
    private RectF okButtonRect;
    private RectF voteYesButtonRect;
    private RectF voteNoButtonRect;
    private RectF centerDeckRect;
    private RectF trickWinnerHighlightRect;
    private int handCardOffset;
    private int backgroundColor;
    private int teammatePlayerNum;
    private int playerToLeftIndex;
    private int playerToRightIndex;
    private boolean voted;
    private boolean voteGoSet;
    private boolean acknowledgePressed;
    private boolean highlightingTrickWinner;
    private Paint buttonPaint;
    private Paint biddingTextPaint;
    private Paint trumpChoiceTextPaint;
    private Paint blackPaint;
    private Paint highlightPaint;
    private Paint exchangeButtonTextPaint;
    private Paint okButtonTextPaint;
    private Paint trickWinnerHighlightPaint;
    private Bitmap[] suits;
    private ArrayList<Card> exchangeCards;
    private ArrayList<Path> highlightMarkers;
    private ArrayList<String> melds;
    private ArrayList<Meld> myMelds;
    private Handler handler;
    private Deck myHand;

    public PinochleHumanPlayer(String name)
    {
        super(name);
        initializeGUI();
        exchangeCards = new ArrayList<>();
        highlightMarkers = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * initialize the gui
     */
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
        centerDeckRect = new RectF(900,400,1020,600);
        trickWinnerHighlightRect = new RectF(0,20,400,900);
        trickWinnerHighlightPaint = new Paint();
        trickWinnerHighlightPaint.setARGB(170,155,255,128);
        handCardOffset = 60;
        highlightingTrickWinner = false;
        suits = new Bitmap[4];

    }


    @Override
    public void receiveInfo(GameInfo info) {
        waitingToConnectTextView.setVisibility(View.INVISIBLE);
        scoreboard.setVisibility(View.VISIBLE);
        totalTextView.setVisibility(View.VISIBLE);
        meldsTextView.setVisibility(View.VISIBLE);
        tricksTextView.setVisibility(View.VISIBLE);
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
        if (info instanceof IllegalMoveInfo) {
            String message = null;
            switch (state.getPhase()) {
                case TRICK_TAKING:
                    Card leadTrick = state.getLeadTrick();
                    if (leadTrick == null) return;
                    Suit leadTrickSuit = leadTrick.getSuit();
                    Suit trumpSuit = state.getTrumpSuit();
                    if (state.playerHasSuit(playerNum, leadTrickSuit)) {
                        Card winnableCard = state.playerHasWinnableCard(playerNum);
                        if (winnableCard != null)
                        {
                            message = "You have a " + winnableCard.toString() + " (or higher ranking card) that beats the cards in the center so you have to play it.";
                        }
                        else {
                            message = "You have a " + leadTrickSuit + " (lead trick suit), so you have to play it.";
                        }
                        break;
                    }
                    if (state.playerHasSuit(playerNum, trumpSuit)) {
                        message = "You have a " + trumpSuit + " (Trump suit), so you have to play it.";
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
            leftPlayerNameTextView.setText(allPlayerNames[playerToLeftIndex]);
            rightPlayerNameTextView.setText(allPlayerNames[playerToRightIndex]);
            topPlayerNameTextView.setText(allPlayerNames[teammatePlayerNum]);
            updateGUI(phase);

            switch (phase) {
                case BIDDING:
                    phaseTextView.setText("Phase: Bidding");
                    bidTextView.setText("");
                    trumpSuitTextView.setText("");
                    melds = null;
                    break;
                case CHOOSE_TRUMP:
                    phaseTextView.setText("Phase: Choose Trump");
                    int wonBidTeam = state.getTeam(state.getWonBid());
                    String teamName;
                    if (state.getTeam(playerNum) == wonBidTeam) {
                        teamName = "Your";
                    } else {
                        teamName = "Opposing";
                    }
                    bidTextView.setText("Bid: " + teamName + " team bid " + state.getMaxBid());
                    trumpSuitTextView.setText("");
                    break;
                case EXCHANGE_CARDS:
                    phaseTextView.setText("Phase: Card Exchange");
                    trumpSuitTextView.setText("Trump: " + state.getTrumpSuit().getName());

                    break;
                case MELDING:
                    phaseTextView.setText("Phase: Calculate Melds");
                    game.sendAction(new PinochleActionCalculateMelds(this));
                    voted = false;
                    break;
                case VOTE_GO_SET:
                    phaseTextView.setText("Phase: Go Set?");
                    sendMeldsToActivity();
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
                    phaseTextView.setText("Phase: Trick-taking");
                    if (state.getTrickRound() == 0 && state.getTurn() == state.getWonBid()) {
                        sendMeldsToActivity();
                    }
                    if (state.getTurn() == playerNum) {
                        if (state.getCenterDeck().getCards().size() >= 4) {
                            sleep(1);
                            game.sendAction(new PinochleActionPlayTrick(this, null));
                            break;
                        }
                    }
                    acknowledgePressed = false;
                    break;
                case ACKNOWLEDGE_SCORE:
                    phaseTextView.setText("Phase: Scoring");
                    trumpSuitTextView.setText("");
                    melds = null;
                    if (acknowledgePressed) {
                        game.sendAction(new PinochleActionAcknowledgeScore(this));
                    }
                    break;

            }

        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void setAsGui(GameMainActivity activity)
    {
        // remember the activity
        myActivity = (PinochleMainActivity) activity;

        // Load the layout resource for the new configuration
        activity.setContentView(R.layout.pinochle_human_player);

        myActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        waitingToConnectTextView = myActivity.findViewById(R.id.waitingToConnect);
        leftPlayerInfoTextView = myActivity.findViewById(R.id.leftPlayerInfo);
        leftPlayerNameTextView = myActivity.findViewById(R.id.leftPlayerName);
        rightPlayerInfoTextView = myActivity.findViewById(R.id.rightPlayerInfo);
        rightPlayerNameTextView = myActivity.findViewById(R.id.rightPlayerName);
        topPlayerInfoTextView = myActivity.findViewById(R.id.topPlayerInfo);
        topPlayerNameTextView = myActivity.findViewById(R.id.topPlayerName);
        humanPlayerInfoTextView = myActivity.findViewById(R.id.humanPlayerInfo);
        bidTextView = myActivity.findViewById(R.id.bidTextView);
        trumpSuitTextView = myActivity.findViewById(R.id.trumpSuitTextView);
        phaseTextView = myActivity.findViewById(R.id.phaseTextView);
        meldsMenuButton = myActivity.findViewById(R.id.meldsMenuButton);
        meldsMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu meldsMenu = new PopupMenu(myActivity, view);
                meldsMenu.inflate(R.menu.melds_menu);
                meldsMenu.getMenu().clear();
                if (melds != null) {
                    for (String meld : melds) {
                        meldsMenu.getMenu().add(meld);
                    }
                    meldsMenu.show();
                }
            }
        });
        melds = new ArrayList<>();
        scoreboard = myActivity.findViewById(R.id.scoreboard);
        totalTextView = myActivity.findViewById(R.id.total);
        meldsTextView = myActivity.findViewById(R.id.melds);
        tricksTextView = myActivity.findViewById(R.id.tricks);
        team0NameTextView = myActivity.findViewById(R.id.team0_name);
        team0TotalTextView = myActivity.findViewById(R.id.team0_toal);
        team0MeldsTextView = myActivity.findViewById(R.id.team0_melds);
        team0TricksTextView = myActivity.findViewById(R.id.team0_tricks);
        team1NameTextView = myActivity.findViewById(R.id.team1_name);
        team1TotalTextView = myActivity.findViewById(R.id.team1_total);
        team1MeldsTextView = myActivity.findViewById(R.id.team1_melds);
        team1TricksTextView = myActivity.findViewById(R.id.team1_tricks);

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
     * @param phase the current game phase
     *
     */
    protected void updateGUI(PinochleGamePhase phase)
    {
        if (state != null)
        {
            if (phase != PinochleGamePhase.VOTE_GO_SET && phase != PinochleGamePhase.TRICK_TAKING) {
                meldsMenuButton.setVisibility(View.INVISIBLE);
            }
            if (phase == PinochleGamePhase.VOTE_GO_SET || phase == PinochleGamePhase.ACKNOWLEDGE_SCORE) {
                humanPlayerInfoTextView.setVisibility(View.INVISIBLE);
                leftPlayerNameTextView.setVisibility(View.INVISIBLE);
                leftPlayerInfoTextView.setVisibility(View.INVISIBLE);
                topPlayerNameTextView.setVisibility(View.INVISIBLE);
                topPlayerInfoTextView.setVisibility(View.INVISIBLE);
                rightPlayerNameTextView.setVisibility(View.INVISIBLE);
                rightPlayerInfoTextView.setVisibility(View.INVISIBLE);
            } else {
                humanPlayerInfoTextView.setVisibility(View.VISIBLE);
                leftPlayerNameTextView.setVisibility(View.VISIBLE);
                leftPlayerInfoTextView.setVisibility(View.VISIBLE);
                topPlayerNameTextView.setVisibility(View.VISIBLE);
                topPlayerInfoTextView.setVisibility(View.VISIBLE);
                rightPlayerNameTextView.setVisibility(View.VISIBLE);
                rightPlayerInfoTextView.setVisibility(View.VISIBLE);
            }

            int[] totalScores = state.getScoreboard();
            int[] meldsScores = state.getMeldsScoreboard();
            int[] tricksScores = state.getTricksScoreboard();

            if (state.getTeam(playerNum) == 0) {
                team0NameTextView.setText("Your team");
                team1NameTextView.setText("Opposing team");
            } else {
                team0NameTextView.setText("Opposing team");
                team1NameTextView.setText("Your team");
            }
            team0TotalTextView.setText(String.valueOf(totalScores[0]));
            team0MeldsTextView.setText(String.valueOf(meldsScores[0]));
            team0TricksTextView.setText(String.valueOf(tricksScores[0]));
            team1TotalTextView.setText(String.valueOf(totalScores[1]));
            team1MeldsTextView.setText(String.valueOf(meldsScores[1]));
            team1TricksTextView.setText(String.valueOf(tricksScores[1]));

            //update the info for each player
            int turn = state.getTurn();

            if (turn == playerToLeftIndex)
            {
                leftPlayerInfoTextView.setText("[My turn]");
            }
            else if (turn == teammatePlayerNum)
            {
                topPlayerInfoTextView.setText("[My turn]");
            }
            else if (turn == playerToRightIndex)
            {
                rightPlayerInfoTextView.setText("[My turn]");
            }
            else
            {
                humanPlayerInfoTextView.setText("[Your turn]");
            }
            if (phase == PinochleGamePhase.BIDDING || phase == PinochleGamePhase.CHOOSE_TRUMP)
            {

                if (state.getPassed(playerToRightIndex))
                {
                    rightPlayerInfoTextView.setText("[Passed]");
                }
                else
                {
                    if (turn != playerToRightIndex)
                    {
                        if (state.getBids()[playerToRightIndex] != 0) {
                            rightPlayerInfoTextView.setText("[Bid " + state.getBids()[playerToRightIndex] + "]");
                        } else {
                            rightPlayerInfoTextView.setText("[Waiting]");
                        }
                    }
                }
                if (state.getPassed(teammatePlayerNum))
                {
                    topPlayerInfoTextView.setText("[Passed]");
                }
                else
                {
                    if (turn != teammatePlayerNum)
                    {
                        if (state.getBids()[teammatePlayerNum] != 0) {
                            topPlayerInfoTextView.setText("[Bid " + state.getBids()[teammatePlayerNum] + "]");
                        } else {
                            topPlayerInfoTextView.setText("[Waiting]");
                        }
                    }
                }
                if (state.getPassed(playerToLeftIndex))
                {
                    leftPlayerInfoTextView.setText("[Passed]");
                }
                else
                {
                    if (turn != playerToLeftIndex)
                    {
                        if (state.getBids()[playerToLeftIndex] != 0) {
                            leftPlayerInfoTextView.setText("[Bid " + state.getBids()[playerToLeftIndex] + "]");
                        } else {
                            leftPlayerInfoTextView.setText("[Waiting]");
                        }
                    }
                }
                if (state.getPassed(playerNum))
                {
                    humanPlayerInfoTextView.setText("[Passed]");
                }
                else
                {
                    if (turn != playerNum)
                    {
                        if (state.getBids()[playerNum] != 0) {
                            humanPlayerInfoTextView.setText("[Bid " + state.getBids()[playerNum] + "]");
                        } else {
                            humanPlayerInfoTextView.setText("[Waiting]");
                        }
                    }
                }



            }
            else if (state.getPhase() == PinochleGamePhase.MELDING)
            {
                leftPlayerInfoTextView.setText("[Waiting]");
                topPlayerInfoTextView.setText("[Waiting]");
                rightPlayerInfoTextView.setText("[Waiting]");
                humanPlayerInfoTextView.setText("[Waiting]");
            } else if (state.getPhase() == PinochleGamePhase.TRICK_TAKING) {
                if (state.getTurn() != playerToLeftIndex) leftPlayerInfoTextView.setText("[Waiting]");
                else leftPlayerInfoTextView.setText("[My turn]");
                if (state.getTurn() != playerToRightIndex) rightPlayerInfoTextView.setText("[Waiting]");
                else rightPlayerInfoTextView.setText("[My turn]");
                if (state.getTurn() != teammatePlayerNum) topPlayerInfoTextView.setText("[Waiting]");
                else topPlayerInfoTextView.setText("[My turn]");
                if (state.getTurn() != playerNum) humanPlayerInfoTextView.setText("[Waiting]");
                else humanPlayerInfoTextView.setText("[Your turn]");
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
        } else {
            c.drawText("Waiting for other players...", 960, 710, biddingTextPaint);
        }
    }

    /**
     * draws melding prompt
     *
     * @param c canvas to draw on
     *
     */
    protected void drawMeldingPrompt(Canvas c)
    {
        c.drawText("Calculating melds...", 960, 710, biddingTextPaint);
    }

    /**
     * draws the trick taking prompt
     *
     * @param c canvas to draw on
     *
     */
    protected void drawTrickTakingPrompt(Canvas c)
    {

        RectF leftCardRect = new RectF(centerDeckRect.left - 65,centerDeckRect.top,centerDeckRect.right - 65,centerDeckRect.bottom);
        RectF topCardRect = new RectF(centerDeckRect.left,centerDeckRect.top - 60,centerDeckRect.right,centerDeckRect.bottom - 60);
        RectF rightCardRect = new RectF(centerDeckRect.left + 65,centerDeckRect.top,centerDeckRect.right + 65,centerDeckRect.bottom);
        RectF bottomCardRect = new RectF(centerDeckRect.left,centerDeckRect.top + 60,centerDeckRect.right,centerDeckRect.bottom + 60);

        //draw center deck
        for (int i = 0; i < state.getCenterDeck().getCards().size(); i++)
        {
            int leadTrickPlayerIndex = (state.getPreviousTrickWinner() != -1) ? state.getPreviousTrickWinner() : state.getTurn();
            int cardLocationIndex = i + leadTrickPlayerIndex;
            if (cardLocationIndex >= 4){cardLocationIndex-=4;}


            if (state.getCenterDeck().getCards().size() > i) {
                final Card card = state.getCenterDeck().getCards().get(i);
                if (cardLocationIndex == playerNum) {
                    drawCard(c, bottomCardRect, card);
                }
                else if (cardLocationIndex == playerToLeftIndex)
                {
                    drawCard(c, leftCardRect, card);
                }
                else if (cardLocationIndex == teammatePlayerNum)
                {
                    drawCard(c, topCardRect, card);
                }
                else if (cardLocationIndex == playerToRightIndex)
                {
                    drawCard(c, rightCardRect, card);
                }

            }
        }

        //highlight the trick winner if the trick is finished
        if (state.getCenterDeck().getCards().size() == 4)
        {
            if (highlightingTrickWinner == false)
            {

                if (state.getTrickWinner() == playerToLeftIndex)
                {
                    trickWinnerHighlightRect = new RectF(250,420,530,600);
                }
                else if (state.getTrickWinner() == teammatePlayerNum)
                {
                    trickWinnerHighlightRect = new RectF(820,170,1110,340);
                }
                else if (state.getTrickWinner() == playerToRightIndex)
                {
                    trickWinnerHighlightRect = new RectF(1370,420,1650,600);
                }
                else
                {
                    trickWinnerHighlightRect = new RectF(-2,0,0,2);
                }
                highlightingTrickWinner = true;
            }
            c.drawRect(trickWinnerHighlightRect,trickWinnerHighlightPaint);

        }
        else
        {
            highlightingTrickWinner = false;
        }
        int wonLastTrick = state.getPreviousTrickWinner();

        if (state.getTurn() == playerNum && state.getCenterDeck().getCards().size() != 4)
        {
            if (state.getTrickRound() > 0 && playerNum == wonLastTrick) {
                c.drawText("You won the last trick!", 960, 640, biddingTextPaint);
            }
            c.drawText("Choose a card below to play.", 960, 710, biddingTextPaint);
        } else {
            c.drawText("Waiting for other players...", 960, 710, biddingTextPaint);
        }
    }

    /**
     * draws the scoring prompt
     *
     * @param c canvas to draw on
     *
     */
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
     * @param c canvas to draw on
     *
     */
    protected void drawVoteGoSetPrompt(Canvas c)
    {
        if (state.getWonBid() == playerNum || state.getWonBid() == teammatePlayerNum) {
            int maxBid = state.getMaxBid();
            c.drawText("Your team bid " + maxBid + " points.", 960, 370, biddingTextPaint);
            c.drawText("Your team scored " + state.getMeldsScoreboard()[state.getTeam(playerNum)] + " points.", 960, 440, biddingTextPaint);
            c.drawText("Do you want to go set?", 960, 510, biddingTextPaint);
            c.drawText("NOTE: The round ends if both teammates vote yes.", 960, 580, biddingTextPaint);
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

    protected void sendMeldsToActivity()
    {
        myMelds = state.getMelds()[playerNum];
        if (myMelds != null)
        {
            ArrayList<String> meldStrings = new ArrayList<>();
            int points = 0;
            for (Meld m : myMelds)
            {
                meldStrings.add(m.getName() + ": " + m.getPoints());
                points += m.getPoints();

            }
            meldStrings.add("Total meld points: " + points);
            melds = meldStrings;

        }
        meldsMenuButton.setVisibility(View.VISIBLE);
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

        } else {
            c.drawText("Waiting for other players...", 960, 710, biddingTextPaint);
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
            c.drawText("You won the bid!", 960, 460, trumpChoiceTextPaint);
            c.drawText("Choose the Trump Suit", 960, 530, trumpChoiceTextPaint);
            for (int i = 0; i < 4; i++) {
                RectF rect = new RectF(trumpSuitChoiceRect.left + (200 * i), trumpSuitChoiceRect.top, trumpSuitChoiceRect.right + (200 * i), trumpSuitChoiceRect.bottom);
                Rect r = new Rect(0,0, suits[i].getWidth(), suits[i].getHeight());
                c.drawBitmap(suits[i],r,rect,blackPaint);
            }
        } else {
            c.drawText("Waiting for other players...", 960, 710, biddingTextPaint);
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
                        System.out.println(chosenCard);
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

    @Override
    public void tick(Canvas canvas) {
        canvas.drawColor(backgroundColor);
        if (state == null)
        {
            return;
        }
        //draw all of the hands
        myHand = state.getPlayerDeck(playerNum);
        Deck teammateHand = state.getPlayerDeck(teammatePlayerNum);
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
    private void drawHand(Canvas g, Deck hand){

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

            initialRect = new RectF(1200,30,1280,170);
            for (int i = state.getPlayerDeck(state.getTeammate(playerNum)).size() - 1; i >= 0 ; i--)
            {
                drawCard(g, new RectF(initialRect.left - (cardOffset * (i + 1)), initialRect.top , initialRect.right - (cardOffset * (i + 1)) , initialRect.bottom ), null);
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
