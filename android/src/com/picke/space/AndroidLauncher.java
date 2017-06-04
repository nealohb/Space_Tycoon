package com.picke.space;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.picke.utils.Communicator;
import com.picke.utils.ConfirmInterface;
import com.picke.utils.Language;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.Calendar;
import java.util.Locale;

public class AndroidLauncher extends AndroidApplication implements Communicator {
	
	RelativeLayout layout;
	View gameView;

	private InterstitialAd interstitial;
	
	boolean followUp, shouldChangeBannerOrientation, unityAdsDisabled, readyToPurchase, removedAds, displayAdmob;
	boolean displayBannerAds = true;
	
	Main main;
	
	RelativeLayout.LayoutParams adParams;
	
	Context context;
	
	AdView bannerView;

	static final String[] testDevices = {
		"37A6BA99490A90270338DF9E63D7D0C4",
		"AD2CDCFE72DC1DA93B5AB405BBCC1C7B",
		"4D5D8D5B4683117B8C92F553AD6DCDF8",
		"DBBEAB4E43A6D95AEACF2E368F4C50B5",
		"26A67047F3F751F4B6598A27F1B52B06",
		"A78EDD4DA7E2CCC84DB0BB07199D41D5",
	};
	
	static final String ANALYTICS_ID = "";
	static final String ADMOB_BANNER_ID = "";
	static final String ADMOB_INTERSTITIAL_ID = "";
	static final String UNITY_ID = "";
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        
        context = this.getApplicationContext();
        
        // Create the layout
        layout = new RelativeLayout(this);

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        
        main = new Main(this);

        
        String license = 
        		  "";

        
        // Create the libgdx View
        gameView = initializeForView(main);
        
        // Create AdMob view
        bannerView = new AdView(this);
        bannerView.setAdUnitId(ADMOB_BANNER_ID);
        bannerView.setAdSize(AdSize.SMART_BANNER);
        bannerView.setAdListener(new AdListener(){
			@Override
			public void onAdLoaded(){
				bannerView.setVisibility(View.GONE);
				bannerView.setVisibility(View.VISIBLE);
			}
		});
        
        // Setup the AdMob view
        adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        // Add the libgdx view
        layout.addView(gameView);
        
        // Add the AdMob view
        layout.addView(bannerView, adParams);

        // Hook it all up
        setContentView(layout);

		// Unity ads
//		if(UnityAds.isSupported())
//			UnityAds.initialize(this, UNITY_ID, unityListener);
//		else
//			unityAdsDisabled = true;
        
        // Create the interstitial
//        interstitial = new InterstitialAd(this);
//        interstitial.setAdUnitId(ADMOB_INTERSTITIAL_ID);
//
//        // Create ad request
//        AdRequest.Builder interReq = new AdRequest.Builder();
//		for(int i = 0; i < testDevices.length; i++) // add test devices
//			interReq.addTestDevice(testDevices[i]);
//
//		interstitial.loadAd(interReq.build());
//
//        interstitial.setAdListener(new AdListener() {
//        	public void onAdClosed() {
//        		AdRequest.Builder request = new AdRequest.Builder();
//
//        		for(int i = 0; i < testDevices.length; i++) // add test devices
//        			request.addTestDevice(testDevices[i]);
//
//        		interstitial.loadAd(request.build());
//        	}
//        });
	}

	@Override
	public void showInterstitial() {
		if(displayAdmob || unityAdsDisabled){
			try{
				runOnUiThread(new Runnable(){
					public void run(){
						if(interstitial.isLoaded())
							interstitial.show();
					}
				});
			}
			catch (final Exception ex) {
			}
		}
		else{
			if(UnityAds.isReady()){
				try{
					UnityAds.show(this);
				}
				catch(Exception e){
					unityAdsDisabled = true;
				}
			}
		}
		displayAdmob = !displayAdmob;
	}
	
	@Override
	public void postConstruct(boolean removedAds){
		this.removedAds = removedAds;
		if(removedAds){
			removeBannerView();
			return;
		}
//		loadBannerAd();
	}
	
	private void removeBannerView() {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				layout.removeView(bannerView); // remove the banner view from the layout
			}
		});
	}
	
	@Override
	public void showBanners(final boolean show){
		displayBannerAds = show;
		runOnUiThread(new Runnable(){
			public void run(){
				if(bannerView != null) bannerView.setVisibility(show ? View.VISIBLE : View.GONE);
			}
		});
	}

	void loadBannerAd(){
		runOnUiThread(new Runnable(){
			public void run(){
				AdRequest.Builder request = new AdRequest.Builder();
				
//				Bundle extras = new Bundle();
//        		extras.putBoolean("is_designed_for_families", true);
//        		request.addNetworkExtrasBundle(AdMobAdapter.class, extras);
//        		request.tagForChildDirectedTreatment(true);
        		
				for(int i = 0; i < testDevices.length; i++)
					request.addTestDevice(testDevices[i]);
		        bannerView.loadAd(request.build());
			}
		});
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		
		if(shouldChangeBannerOrientation && !removedAds)
			changeBannerOrientation();
	}
	
	public void changeBannerOrientation(){
		// remove the old view
		removeBannerView();
		shouldChangeBannerOrientation = false;
		
		// create a new view
		bannerView = new AdView(this);
		bannerView.setAdUnitId(ADMOB_BANNER_ID);
		bannerView.setAdSize(AdSize.SMART_BANNER);
		bannerView.setAdListener(new AdListener(){
			@Override
			public void onAdLoaded(){
				if(displayBannerAds){
					bannerView.setVisibility(View.GONE);
					bannerView.setVisibility(View.VISIBLE);
				}
				else
					bannerView.setVisibility(View.GONE);
			}
		});

		loadBannerAd();
		// add the new view
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				layout.addView(bannerView, adParams);
			}
		});
	}
	
	@Override
	public void setOrientation(boolean portrait) {
		if(getResources().getConfiguration().orientation == (portrait ? Configuration.ORIENTATION_PORTRAIT : Configuration.ORIENTATION_LANDSCAPE))
			return;
		
		shouldChangeBannerOrientation = true;
		setRequestedOrientation(portrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	@Override
	public void confirm(final ConfirmInterface confirmInterface, final String question) {
		gameView.post(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(AndroidLauncher.this);
				builder.setMessage(question);
				builder.setPositiveButton(Language.YES,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						confirmInterface.yes();
						dialog.cancel();

					}
				});

				builder.setNegativeButton(Language.NO,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						confirmInterface.no();
						dialog.cancel();

					}
				});
				builder.create();
				builder.show();
			}
		});
	}

	@Override
	public void openURL(String url) {
		Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        startActivity(viewIntent);  
	}

	@Override
	public String getPackage() {
		return getApplicationContext().getPackageName();
	}

	@Override
	public void showToast(final String message, final boolean longDuration) {
		this.runOnUiThread(new Runnable(){
			public void run(){
				Toast.makeText(context, message, (longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
			}
		});
	}

	@Override
	public void setNotification(String title, String text, int minutes, int type) {
		// get a Calendar object with current time
	   	 Calendar cal = Calendar.getInstance();
	   	 // add 5 minutes to the calendar object
	   	 cal.add(Calendar.MINUTE, minutes);
	   	 Intent intent = new Intent(this, AlarmReceiver.class);
	   	 intent.putExtra("title", title);
	   	 intent.putExtra("text", text);
	   	 intent.putExtra("type", type);
	   	 // In reality, you would want to have a static variable for the request code instead of 192837
	   	 PendingIntent sender = PendingIntent.getBroadcast(this, 192213, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	   	 
	   	 // Get the AlarmManager service
	   	 AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
	   	 am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
	}

	@Override
	public void cancelNotification() {
		if (Context.NOTIFICATION_SERVICE != null) {
	        String ns = Context.NOTIFICATION_SERVICE;
	        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
	        nMgr.cancel(AlarmReceiver.NOTIF_ID);
	    }
	}

	@Override
	public String getLanguage() {
		return Locale.getDefault().getLanguage();
	}

	@Override
	public void share(String title, String subject, String text, String imagePath){
		boolean imageExists = (imagePath != null && Gdx.files.external(imagePath).exists());
    	Intent i = new Intent(android.content.Intent.ACTION_SEND );
		i.setType( (imageExists ? "image/png" : "text/plain"));
		i.putExtra(Intent.EXTRA_SUBJECT, subject);
		if(imageExists) i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(Gdx.files.external(imagePath).file()));
		i.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(i, title));
    }
	
	/**
	 * >>> ANALYTICS SECTION <<<
	 *  
	 */
	@Override
	public void sendAnalyticsData(String category, String info) {

	}
	
	/**
	 * 
	 *  >>> IN APP BILLING SECTION <<<
	 *  
	 *  Documentation https://github.com/anjlab/android-inapp-billing-v3
	 */
	private final IUnityAdsListener unityListener = new IUnityAdsListener() {

		@Override
		public void onUnityAdsError(UnityAds.UnityAdsError arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUnityAdsFinish(String placementId, UnityAds.FinishState result) {
		}

		@Override
		public void onUnityAdsReady(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUnityAdsStart(String arg0) {
			// TODO Auto-generated method stub

		}

	};

}
