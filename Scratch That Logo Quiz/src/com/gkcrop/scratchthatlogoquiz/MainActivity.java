package com.gkcrop.scratchthatlogoquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends Activity {

	private TextView mainScreenTotalPoints;
	MemoryStorage memoryStorage;
	Button btnSound;
	private AdView mAdView;
	private InterstitialAd mInterstitial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnSound=(Button)findViewById(R.id.sound);
		
		 mInterstitial = new InterstitialAd(getApplicationContext());
		 mInterstitial.setAdUnitId(getResources().getString(R.string.admob_intertestial_id));
		 mInterstitial.loadAd(new AdRequest.Builder().build());
		
		 mInterstitial.setAdListener(new AdListener() {
	    	  @Override
	    	public void onAdLoaded() {
	    		// TODO Auto-generated method stub
	    		super.onAdLoaded();
	    		if (mInterstitial.isLoaded()) {
		            mInterstitial.show();
			  }
	    	}
		});
		memoryStorage = new MemoryStorage(getApplicationContext());
		mainScreenTotalPoints = (TextView)findViewById(R.id.mainScreenTotalPoints);
		Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/VarelaRound-Regular.ttf");
		mainScreenTotalPoints.setTypeface(typeface);
		mainScreenTotalPoints.setText(memoryStorage.getTotalScoreLabel());
		
		mAdView = (AdView) findViewById(R.id.adView);
	    mAdView.loadAd(new AdRequest.Builder().build());
	    

		if (memoryStorage.isSoundEnabled())
		{
			btnSound.setText(getString(R.string.mute));
			btnSound.setBackgroundResource(R.drawable.mute_01);
			return;
		} else
		{
			btnSound.setText(getString(R.string.sound));
			btnSound.setBackgroundResource(R.drawable.mute_02);
			return;
		}
	}

	public void onButtonClickPlay(View view)
	{
		startActivity(new Intent(this, CategoryActivity.class));
	}

	public void onButtonClickHowToPlay(View view)
	{
		startActivity(new Intent(this, HowTo.class));
	}

	public void onButtonClickSound(View view)
	{
		if (memoryStorage.isSoundEnabled())
		{
			btnSound.setText(getString(R.string.sound));
			btnSound.setBackgroundResource(R.drawable.mute_02);
			memoryStorage.setStateSound(false);
			return;
		} else
		{
			btnSound.setText(getString(R.string.mute));
			btnSound.setBackgroundResource(R.drawable.mute_01);
			memoryStorage.setStateSound(true);
			return;
		}
	}

	public void onButtonClickMoreApp(View view)
	{
		startActivity(new Intent(
				Intent.ACTION_VIEW,
				Uri.parse(getString(R.string.play_more_apps))));
	}

	public void onButtonClickRate(View view)
	{
		String s = getPackageName();
		startActivity(new Intent("android.intent.action.VIEW", Uri.parse((new StringBuilder()).append("https://play.google.com/store/apps/details?id=").append(s).toString())));
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mainScreenTotalPoints.setText(memoryStorage.getTotalScoreLabel());
	}

}
