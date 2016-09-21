package com.gkcrop.scratchthatlogoquiz;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.winsontan520.WScratchView;
import java.util.Iterator;
import java.util.List;

 
public class GameActivity extends Activity
    implements com.winsontan520.WScratchView.OnScratchScreenListener, android.view.animation.Animation.AnimationListener
{
    
    private Animation animMove;
    private ImageView animationBackground;
    private RelativeLayout animationOverlayLayout;
    private int category;
    private TextView categoryName;
    private int currentScratchValue;
    private int failureSound;
    private Typeface gameFont;
    private boolean gameIsFinished;
    private ImageView hand;
    private LinearLayout helperButtonsPanel;
    private Button hintsButton;
    private LevelManager levelManager;
    private TextView levelNumberView;
    private MemoryStorage memoryStorage;
    private int minimalLevelScore;
    private Button nextLevelButton;
    private int pointsForCurrentLevel;
    private ImageView scratchImage;
    private WScratchView scratchView;
    private SoundPool sounds;
    private int staringLevelPoints;
    private TextView startPoint;
    private int startingScratchValue;
    private TextView totalPointsView;
    private int winSound;


    private void initSounds()
    {
        setVolumeControlStream(3);
        sounds = new SoundPool(4, 3, 0);
        winSound = sounds.load(this,R.raw.win, 1);
        failureSound = sounds.load(this, R.raw.failure, 1);
    }

    private void playSound(int i)
    {
        if (memoryStorage.isSoundEnabled())
        {
            sounds.play(i, 1.0F, 1.0F, 0, 0, 1.0F);
        }
    }

    private void prepareLevel()
    {
        Log.d("GameActivity", "################################ PREPARE LEVEL ################################");
        gameIsFinished = false;
        staringLevelPoints = getResources().getInteger(R.integer.levelMaxPoints);
        startingScratchValue = getResources().getInteger(R.integer.scratchAmount);
        minimalLevelScore = getResources().getInteger(R.integer.minimalLevelScore);
        pointsForCurrentLevel = staringLevelPoints;
        currentScratchValue = startingScratchValue;
        scratchView.setScratchable(true);
        levelManager.resetHintedButtonsList();
        levelManager.prepareNextLevel();
        levelNumberView.setText(levelManager.getCurrentLevelLabel());
        updateGameButtonLabels();
        scratchView.resetView();
        scratchImage.setImageDrawable(levelManager.getPictureForCurrentLevel());
        totalPointsView.setText(memoryStorage.getTotalScoreLabel());
        updateCurrentLevelPointsView();
    }

    private void setGameFinishedState()
    {
        if (levelManager.allLevelsPlayed())
        {
            nextLevelButton.setText(getResources().getString(R.string.finishCategory));
        }
        gameIsFinished = true;
        scratchView.setVisibility(4);
        helperButtonsPanel.setVisibility(4);
        nextLevelButton.setVisibility(0);
        int i = 0;
        do
        {
label0:
            {
                if (i < 6)
                {
                    Button button = (Button)findViewById(getResources().getIdentifier((new StringBuilder()).append("s").append(i).toString(), "id", getPackageName()));
                    if (!button.getText().equals(levelManager.getSolutionLabel()))
                    {
                        break label0;
                    }
                    button.setBackgroundResource(R.drawable.win_btn);
                }
                return;
            }
            i++;
        } while (true);
    }

    private void setupViews()
    {
        scratchView = (WScratchView)findViewById(R.id.scratch_view);
        scratchView.setOnScratchScreenListener(this);
        startPoint = (TextView)findViewById(R.id.startPoint);
        categoryName = (TextView)findViewById(R.id.categoryName);
        totalPointsView = (TextView)findViewById(R.id.allPoints);
        levelNumberView = (TextView)findViewById(R.id.levelNumber);
        scratchImage = (ImageView)findViewById(R.id.scratchImage);
        helperButtonsPanel = (LinearLayout)findViewById(R.id.helperButtonsPanel);
        nextLevelButton = (Button)findViewById(R.id.nextLevelButton);
        hintsButton = (Button)findViewById(R.id.hintsButton);
        hand = (ImageView)findViewById(R.id.hand);
        animationBackground = (ImageView)findViewById(R.id.animationBackground);
        animationOverlayLayout = (RelativeLayout)findViewById(R.id.animationOverlayLayout);
        ((TextView)findViewById(R.id.btn_game_rating)).setTypeface(gameFont);
        ((TextView)findViewById(R.id.hintsButton)).setTypeface(gameFont);
        nextLevelButton.setTypeface(gameFont);
        categoryName.setTypeface(gameFont);
        for (int i = 0; i < 6; i++)
        {
            ((Button)findViewById(getResources().getIdentifier((new StringBuilder()).append("s").append(i).toString(), "id", getPackageName()))).setTypeface(gameFont);
        }

    }

    private void showHintsForGameExceededDialog()
    {
        final Dialog dialog = new Dialog(this, R.style.CutomDialog);
        dialog.setContentView(R.layout.popup_info_1btn);
        dialog.setCancelable(true);
        ((TextView)dialog.findViewById(R.id.info)).setText(getString(R.string.hintsUsedForLevel));
        ((Button)dialog.findViewById(R.id.yes)).setOnClickListener(new android.view.View.OnClickListener() {

        	
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showMessageDialog(Boolean boolean1)
    {
        final Dialog dialog = new Dialog(this, R.style.CutomDialog);
        dialog.setContentView(R.layout.popup);
        dialog.setCancelable(true);
        TextView textview = (TextView)dialog.findViewById(R.id.txt1);
        TextView textview1 = (TextView)dialog.findViewById(R.id.txt2);
        if (boolean1.booleanValue())
        {
            playSound(winSound);
            textview.setText(getString(R.string.great1));
            textview1.setText(getString(R.string.great2));
        } else
        {
            playSound(failureSound);
            textview.setText(getString(R.string.ups1));
            textview1.setText(getString(R.string.ups2));
        }
        textview.setTypeface(gameFont);
        textview1.setTypeface(gameFont);
        ((TextView)dialog.findViewById(R.id.pointByStep)).setText(String.valueOf(pointsForCurrentLevel));
        ((Button)dialog.findViewById(R.id.nextBtn)).setOnClickListener(new android.view.View.OnClickListener() {

              public void onClick(View view)
            {
                setGameFinishedState();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateCurrentLevelPointsView()
    {
        startPoint.setText(String.valueOf(pointsForCurrentLevel));
    }

    private void updateGameButtonLabels()
    {
        int i = 0;
        for (Iterator iterator = levelManager.getGameButtonLabels().iterator(); iterator.hasNext();)
        {
            String s = (String)iterator.next();
            Button button = (Button)findViewById(getResources().getIdentifier((new StringBuilder()).append("s").append(i).toString(), "id", getPackageName()));
            button.setText(s);
            button.setBackgroundResource(R.drawable.standard_btn);
            button.setTextColor(getResources().getColor(R.color.btnMain));
            i++;
        }

    }

    public void disableAnimation(View view)
    {
        Log.d("GameActivity", "#### disableAnimation");
        hand.clearAnimation();
        animationOverlayLayout.setVisibility(8);
        animationBackground.setVisibility(8);
        scratchView.setVisibility(0);
        memoryStorage.setTutorialAnimationPlayed();
    }

    public void onAnimationEnd(Animation animation)
    {
        Log.d("GameActivity", "#### onAnimationEnd");
        if (animation == animMove)
        {
            animationOverlayLayout.bringToFront();
            hand.startAnimation(animMove);
        }
    }

    public void onAnimationRepeat(Animation animation)
    {
    }

    public void onAnimationStart(Animation animation)
    {
    }

    public void onBackPressed()
    {
    	super.onBackPressed();
    	 if (currentScratchValue != startingScratchValue && !gameIsFinished)
         {
             memoryStorage.saveScore(category, levelManager.getCurrentLevelIndex(), 0);
         }

     }

    public void onButtonClickRate(View view)
    {
        String s = getPackageName();
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse((new StringBuilder()).append("https://play.google.com/store/apps/details?id=").append(s).toString())));
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        gameFont = Typeface.createFromAsset(getAssets(), "fonts/VarelaRound-Regular.ttf");
        setContentView(R.layout.activity_game);
        setupViews();
        memoryStorage = new MemoryStorage(getApplicationContext());
        hintsButton.setText((new StringBuilder()).append("").append(memoryStorage.getHintsNumberForToday()).append(" ").append(getResources().getString(R.string.hints)).toString());
        Bundle bundle1 = getIntent().getExtras();
        try
        {
            category = bundle1.getInt("category");
        }
        catch (Exception exception)
        {
            category = 0;
        }
        levelManager = new LevelManager(getApplicationContext(), memoryStorage, category);
        categoryName.setText(levelManager.getCategoryName());
        prepareLevel();
        if (memoryStorage.isSoundEnabled())
        {
            initSounds();
        }
        if (memoryStorage.isTutorialAnimationNotPlayed())
        {
            Log.d("GameActivity", "#### TutorialAnimationNotPlayed");
            scratchView.setVisibility(4);
            animationBackground.setVisibility(0);
            animationOverlayLayout.setVisibility(0);
            animMove = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
            animMove.setAnimationListener(this);
            animationOverlayLayout.bringToFront();
            hand.startAnimation(animMove);
        }
        ((AdView)findViewById(R.id.adView)).loadAd((new AdRequest.Builder()).build());
    }

    protected void onDestroy()
    {
        super.onDestroy();
        if (sounds != null)
        {
            sounds.release();
            sounds = null;
        }
    }

    public void onGameButtonClick(View view)
    {
        if (gameIsFinished)
        {
            return;
        }
        Integer.valueOf((new StringBuilder()).append("").append(view.getTag()).toString()).intValue();
        Button button = (Button)view;
        String s = button.getText().toString();
        if (levelManager.getSolutionLabel().equals(s))
        {
            button.setBackgroundResource(R.drawable.win_btn);
            showMessageDialog(Boolean.valueOf(true));
        } else
        {
            currentScratchValue = 0;
            pointsForCurrentLevel = 0;
            updateCurrentLevelPointsView();
            button.setBackgroundResource(R.drawable.lose_btn);
            showMessageDialog(Boolean.valueOf(false));
        }
        memoryStorage.saveScore(category, levelManager.getCurrentLevelIndex(), pointsForCurrentLevel);
        totalPointsView.setText(memoryStorage.getTotalScoreLabel());
    }

    public void onHintsButtonClick(View view)
    {
        if (!gameIsFinished) 
        {
        	if (levelManager.allHintsForLevelUsed())
            {
                showHintsForGameExceededDialog();
                return;
            }
        	 if (memoryStorage.getHintsNumberForToday() != 0)
             {
                 List list = levelManager.getNextHintButtonIndices();
                 hintsButton.setText((new StringBuilder()).append("").append(memoryStorage.getHintsNumberForToday()).append(" ").append(getResources().getString(R.string.hints)).toString());
                 Iterator iterator = list.iterator();
                 while (iterator.hasNext()) 
                 {
                     Integer integer = (Integer)iterator.next();
                     Button button = (Button)findViewById(getResources().getIdentifier((new StringBuilder()).append("s").append(integer).toString(), "id", getPackageName()));
                     button.setBackgroundResource(R.drawable.button_navy_blue);
                     button.setTextColor(getResources().getColor(R.color.btnHinted));
                 }
             }
        }
        else
        {
        	return;
        }
    }

    public void onNextLevelButtonClick(View view)
    {
        if (levelManager.allLevelsPlayed())
        {
            finish();
            return;
        } else
        {
            scratchView.setVisibility(0);
            helperButtonsPanel.setVisibility(0);
            nextLevelButton.setVisibility(4);
            prepareLevel();
            return;
        }
    }

    public void onScratchScreen(double d)
    {
        if (currentScratchValue > 0)
        {
            currentScratchValue = Integer.valueOf((int)((double)currentScratchValue - d)).intValue();
            pointsForCurrentLevel = (staringLevelPoints * currentScratchValue) / startingScratchValue;
        }
        if (pointsForCurrentLevel <= minimalLevelScore)
        {
            currentScratchValue = 0;
            pointsForCurrentLevel = minimalLevelScore;
            scratchView.setScratchable(false);
        }
        updateCurrentLevelPointsView();
    }




}
