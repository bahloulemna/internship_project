package com.gkcrop.scratchthatlogoquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.HashSet;
import java.util.Set;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class CategoryActivity extends Activity
{

    private TextView categoriesTotalPoints;
    private Set inactiveCategories;
    private LevelManager levelManager;
    private MemoryStorage memoryStorage;

    public CategoryActivity()
    {
    }

    private void refreshScore()
    {
        PreferenceManager.getDefaultSharedPreferences(this);
        categoriesTotalPoints.setText(memoryStorage.getTotalScoreLabel());
    }

    private void showResetGameButtonIfAllCategoriesFinished()
    {
        if (inactiveCategories.size() == Config.names.size())
        {
            findViewById(R.id.btn_reset).setVisibility(0);
        }
    }

    public void onButtonClickCategory(View view)
    {
        int i = Integer.valueOf((String)view.getTag()).intValue();
        if (inactiveCategories.contains(Integer.valueOf(i)))
        {
            return;
        } else
        {
            Intent intent = new Intent(this,GameActivity.class);
            intent.putExtra("category", i);
            startActivity(intent);
            return;
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
        setContentView(R.layout.activity_category);
        memoryStorage = new MemoryStorage(getApplicationContext());
        levelManager = new LevelManager(getApplicationContext(), memoryStorage, 0);
        categoriesTotalPoints = (TextView)findViewById(R.id.categoriesTotalPoints);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/VarelaRound-Regular.ttf");
        categoriesTotalPoints.setTypeface(typeface);
        categoriesTotalPoints.setText(memoryStorage.getTotalScoreLabel());
        ((TextView)findViewById(R.id.btn_rate)).setTypeface(typeface);
        for (int i = 0; i < 6; i++)
        {
            ((Button)findViewById(getResources().getIdentifier((new StringBuilder()).append("categoryBtn_").append(i).toString(), "id", getPackageName()))).setTypeface(typeface);
        }

       ((AdView)findViewById(R.id.adView)).loadAd((new AdRequest.Builder()).build());
    }

    protected void onResume()
    {
        super.onResume();
        refreshScore();
        inactiveCategories = new HashSet();
        for (int i = 0; i < Config.names.size(); i++)
        {
            Button button = (Button)findViewById(getResources().getIdentifier((new StringBuilder()).append("categoryBtn_").append(i).toString(), "id", getPackageName()));
            int j = Integer.valueOf((String)button.getTag()).intValue();
            if (levelManager.allLevelsPlayed(j))
            {
                button.setBackgroundResource(R.drawable.inactive_btn);
                inactiveCategories.add(Integer.valueOf(j));
            }
        }

        showResetGameButtonIfAllCategoriesFinished();
    }

    public void resetGame(View view)
    {
        memoryStorage.resetScoreAndLevels();
        finish();
        startActivity(getIntent());
    }
}
