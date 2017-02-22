package com.kipsap.jshipbattle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdSize;
import com.kipsap.jshipbattle.util.IabHelper;
import com.kipsap.jshipbattle.util.IabResult;
import com.kipsap.jshipbattle.util.Inventory;
import com.kipsap.jshipbattle.util.Purchase;

public class ShootActivity extends FragmentActivity implements AnimationListener {

    final Context context = this;
    private static final String TAG = "com.kipsap.jshipbattle";
	final int MAX_HITS = 21;
	private OpponentsFleet _oppFleet;
	private MyFleet _myFleet;
	private static final int DELAY = 120000; //standaard time-out scherm: 2 minuten
    private static final int DIALOG1_KEY = 1; //waiting while getting fleet history
	
	int defTimeOut, iNumberOfShotsToday;
	int shipdirectionsUser1, shipdirectionsUser2, myShipdirections, oppShipdirections;
	int iCrossHair, gamestate, sunkship, _myFleetDim, _oppFleetDim, adOrUnderBannerHeight;
	int screenWidth, screenHeight, screenMin, screenMax;
	int soundBOEM, soundPLONS, soundFALLING, soundFALLINGBOEM, soundFALLINGPLONS, 
		soundBOEMBUBBEL, soundFALLINGBOEMBUBBEL, soundTick, soundKNALFALLING;
	int hisNumberOfShots, myNumberOfShots;
    int scoreInFavor, scoreAgainst, oppLastShot, initLLwidth, themeID, styleID;
    
    long shipboardUser1, shipboardUser2, myShipboard, oppShipboard;
    long opphits1, opphits2, myhits1, myhits2, oppLastKnownHits1, oppLastKnownHits2;
    long gameID;
    
    boolean doSounds, doVibrations, bNewChatMessage, bPaidVersion;
    boolean soundloaded, readyforresult, didArating, bInAppBilling;
    boolean myTurn, ilost, iwon, somethingChanged, bMyFleetVisible, bMyFleetAnimating, gameIsOver;
    
	String currentUsr, currentOpp, originalInviter, originalInvitee, lastFireResultString;
	String myCountry, hisCountry;
	
	ImageView oppFSbar, myFSbar, ivMyFlag, ivHisFlag, ivOverboot, ivTellertjes;
    ImageView kruis1, kruis2, kruis3, kruis4, kruis5, kruis6, kruis7;
    ImageView underbanner;//, premiumBanner;
	TextView theScore, myName, hisName;
    View chatView, pastBoardsView;
    ScrollView chatScroll;	
	ImageButton ibtnFire, ibtnAddafriend, ibtnResignRematch, ibtnHistory, ibtnChat;
	Button buttonSlideToTheSide;
	AlertDialog alertDialog;
	private LinearLayout LL, _screenTexts, _knopjeszwik, boemZwik;
	ProgressDialog dialog1;
	
	SetupPlayField _pastField[] = new SetupPlayField[5];

	Intent intent;	

    Timer refreshtimer, waitforfireresulttimer, fallingtimer;
	Handler refreshhandler, waitforfireresulthandler, waitforfallingsoundhandler;
	Vibrator v;    
    private SoundPool soundPool;
    private JGetDataFromWebService jgd;

	IabHelper mHelper;
	IabHelper.OnIabPurchaseFinishedListener mPremiumAppBoughtListener;
	IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener;
	SharedPreferences sharedPrefs;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
 	{
    	super.onCreate(savedInstanceState);   	
    	bMyFleetAnimating = false;
    	somethingChanged = false;
    	soundloaded = false;
    	bNewChatMessage = false;
    	ilost = false;   	
    	sunkship = -3;
    	lastFireResultString = "";
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		readyforresult = true;	
		
		sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
        bPaidVersion = sharedPrefs.getBoolean("bPaidVersion", false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_shootfield);
		
		mHelper = new IabHelper(this, JConstants.base64EncodedPublicKey);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
		{
	       	  public void onIabSetupFinished(IabResult result)
			  {
	       	        if (!result.isSuccess())
	       	        {
	       	        	bInAppBilling = false;
	       	            Log.d(TAG, "In-app Billing setup failed: " + result);
	       	        }
	       	        else
	       	        {
	       	        	bInAppBilling = true;
	        	        Log.d(TAG, "In-app Billing is set up OK");
	       	        }
	       	        mHelper.queryInventoryAsync(mReceivedInventoryListener);
			  }
	    });
		

		mPremiumAppBoughtListener = new IabHelper.OnIabPurchaseFinishedListener()
	    {
	    	public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
	    	{
	    		if (result.isFailure()) 
	    		{
	    			// Handle error
	    			return;
	    		}      
	    		else if (purchase.getSku().equals(JConstants.SKU_PREMIUM_APP))
	    		{
	    			Log.d(TAG, "In-app Billing: purchase finished, premium app bought!");
	    			mHelper.queryInventoryAsync(mReceivedInventoryListener);	    			
	    		}		      
	    	}
	    };


	    mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener()
	    {
	 	   public void onQueryInventoryFinished(IabResult result, Inventory inventory) 
	 	   {
	 	      if (result.isFailure()) 
	 	      {
	 	    	  // Handle failure
	 	      } 
	 	      else 
	 	      {
                  boolean bOldPaidVersion = bPaidVersion;
	 	    	  if (inventory.hasPurchase(JConstants.SKU_PREMIUM_APP))
	 	    	  {
                      bPaidVersion = true;
	 	    	  }
	 	    	  else
	 	    	  {   //NON-PAID VERSION!
                      bPaidVersion = true;  //FALSE !
                      //styleID = 0;		 //UNCOMMENT
                      //themeID = 0;		 //UNCOMMENT
	 	    	  }

				  bPaidVersion = true; // remove me

                  SharedPreferences.Editor editor;
                  editor = sharedPrefs.edit();
                  editor.putBoolean("bPaidVersion", bPaidVersion);
                  editor.putInt("themeID", themeID);
                  editor.putInt("styleID", styleID);
                  editor.commit();

                  if (bOldPaidVersion != bPaidVersion)
                      setUpTheScreen();
	 	      }
	 	   }
	    };

		refreshhandler = new Handler();
		waitforfireresulthandler = new Handler();
		waitforfallingsoundhandler = new Handler();
    	v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    	
    	Typeface army = Typeface.createFromAsset(this.getAssets(), "Army.ttf");
    	
	    intent = getIntent();
        Bundle bundle = intent.getExtras();
		currentUsr = bundle.getString("currentUser");
		currentOpp = bundle.getString("currentOpponent");
		shipboardUser1 = bundle.getLong("shipboardUser1");
		shipboardUser2 = bundle.getLong("shipboardUser2");
		shipdirectionsUser1 = bundle.getInt("shipdirectionsUser1");
		shipdirectionsUser2 = bundle.getInt("shipdirectionsUser2");
		originalInviter = bundle.getString("originalInviter");
		gamestate = bundle.getInt("state");
		gameID = bundle.getLong("gameID");
		
		gameIsOver = ((gamestate == GameInstance.GS_PLAYER1_WON) || (gamestate == GameInstance.GS_PLAYER2_WON)
				|| (gamestate == GameInstance.GS_PLAYER1_RESIGNED) || (gamestate == GameInstance.GS_PLAYER2_RESIGNED));
		
		myTurn = false;// my turn blijft false wanneer gamestate niet player1turn of player2turn is (bijv. player1won of player2won)
		
		// determine which user I am:
		if (originalInviter.equals(currentUsr))
        {
			originalInvitee = currentOpp;
        	myShipboard = shipboardUser1;
        	myShipdirections = shipdirectionsUser1;
        	oppShipboard = shipboardUser2;
        	oppShipdirections = shipdirectionsUser2;
        	opphits1 = bundle.getLong("hits1user1");
        	opphits2 = bundle.getLong("hits2user1");
        	myhits1 = bundle.getLong("hits1user2");
        	myhits2 = bundle.getLong("hits2user2");
        	myTurn = (gamestate == GameInstance.GS_PLAYER1_TURN);
        }
        else if (originalInviter.equals(currentOpp))
        {
        	originalInvitee = currentUsr;
        	myShipboard = shipboardUser2;
        	myShipdirections = shipdirectionsUser2;
        	oppShipboard = shipboardUser1;
        	oppShipdirections = shipdirectionsUser1;
        	opphits1 = bundle.getLong("hits1user2");
        	opphits2 = bundle.getLong("hits2user2");
        	myhits1 = bundle.getLong("hits1user1");
        	myhits2 = bundle.getLong("hits2user1");
        	myTurn = (gamestate == GameInstance.GS_PLAYER2_TURN);
        }

        myCountry = sharedPrefs.getString("countryCode", "UNKNOWN");
		hisCountry = sharedPrefs.getString("countryCode"+currentOpp, "UNKNOWN");
		scoreInFavor = sharedPrefs.getInt("favor"+currentUsr+"&"+currentOpp , 0);
		scoreAgainst = sharedPrefs.getInt("against"+currentUsr+"&"+currentOpp, 0);
		oppLastShot = sharedPrefs.getInt("last"+gameID, -1);
		oppLastKnownHits1 = sharedPrefs.getLong("last1h"+gameID, 0);
		oppLastKnownHits2 = sharedPrefs.getLong("last2h"+gameID, 0);
		bMyFleetVisible = sharedPrefs.getBoolean("bMyFleetVisible", true);
		didArating = sharedPrefs.getBoolean("didArating", false);
		themeID = Math.max(0, sharedPrefs.getInt("themeID", 0));
		styleID = Math.max(0, sharedPrefs.getInt("styleID", 0));
		
		Calendar nu = Calendar.getInstance();
		String dayPrefName = currentUsr +"&shots&" + nu.get(Calendar.YEAR) + "&" + (nu.get(Calendar.MONTH) + 1) + "&" + nu.get(Calendar.DAY_OF_MONTH);
		iNumberOfShotsToday = sharedPrefs.getInt(dayPrefName, 0);
		
		myName = (TextView) findViewById(R.id.myName);
		hisName = (TextView) findViewById(R.id.oppName);	
		theScore = (TextView) findViewById(R.id.tvScore);
		theScore.setTypeface(army);
		
		_knopjeszwik = (LinearLayout) this.findViewById(R.id.knopjeszwik);
		_screenTexts = (LinearLayout) this.findViewById(R.id.screensTexts);
		boemZwik = (LinearLayout) this.findViewById(R.id.boemZwik);
    	LL = (LinearLayout) this.findViewById(R.id.MySlidingLayout);
    	ivTellertjes = (ImageView) this.findViewById(R.id.ivTellertjes);
    	ivOverboot = (ImageView) this.findViewById(R.id.ivOverboot);
		    	
    	_myFleet = (MyFleet) findViewById(R.id.shoot_myfield);	
    	_oppFleet = (OpponentsFleet) findViewById(R.id.shoot_oppfield);

    	kruis1 = (ImageView) findViewById(R.id.kruis1);
    	kruis2 = (ImageView) findViewById(R.id.kruis2);
    	kruis3 = (ImageView) findViewById(R.id.kruis3);
    	kruis4 = (ImageView) findViewById(R.id.kruis4);
    	kruis5 = (ImageView) findViewById(R.id.kruis5);
    	kruis6 = (ImageView) findViewById(R.id.kruis6);
    	kruis7 = (ImageView) findViewById(R.id.kruis7);
    	underbanner = (ImageView) findViewById(R.id.underbanner);
    	
    	ibtnFire = (ImageButton) findViewById(R.id.ibtnFire);    	
    	ibtnAddafriend = (ImageButton) findViewById(R.id.ibtnAddafriend);
    	ibtnResignRematch = (ImageButton) findViewById(R.id.ibtnResignRematch);
    	ibtnHistory = (ImageButton) findViewById(R.id.ibtnHistory);
    	ibtnChat = (ImageButton) findViewById(R.id.ibtnChat);
    	buttonSlideToTheSide = (Button) findViewById(R.id.buttonSlide);

        WindowManager _wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display _display  = _wm.getDefaultDisplay();
        screenWidth = _display.getWidth();
	    screenHeight = _display.getHeight();
	    screenMin = Math.min(screenWidth, screenHeight);
	    screenMax = Math.max(screenWidth, screenHeight);
	    float ondersteratio = (float) (screenMax - screenMin) / (float) screenMin;

		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener()
		{
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) 
			{
				soundloaded = true;
			}
		});
		soundBOEM = soundPool.load(this, R.raw.boom1, 1);
		soundPLONS = soundPool.load(this, R.raw.plonsj, 1);
		soundFALLING = soundPool.load(this, R.raw.falling, 1);
		soundFALLINGBOEM = soundPool.load(this, R.raw.falling_boom1, 1);
		soundFALLINGPLONS = soundPool.load(this, R.raw.falling_plonsj, 1);
		soundBOEMBUBBEL = soundPool.load(this, R.raw.boom_bubbel1, 1);
		soundFALLINGBOEMBUBBEL = soundPool.load(this, R.raw.falling_boom_bubbel1, 1);
		soundTick = soundPool.load(this, R.raw.tick, 1);
		soundKNALFALLING = soundPool.load(this, R.raw.knal_falling, 1);		
	}	 
	 
	 private void drawScreen()
	 {	 	   	 
	   	 int zichtbaarDeelMyFleetIngeklapt = _myFleetDim/14;
	   	 initLLwidth = (_myFleetDim * 5/4);
		
		 LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(_oppFleetDim, _oppFleetDim);
	     _oppFleet.setLayoutParams(lp);
	         
	     LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams((_myFleetDim), (_myFleetDim));	          
	   	 _myFleet.setLayoutParams(lp2);	   	 
	   	
	   	LinearLayout.LayoutParams lp5 = new LinearLayout.LayoutParams(2*_myFleetDim/3, _myFleetDim); 
	   	boemZwik.setLayoutParams(lp5);	     	  
	   	
	   	int screensBreedte = _oppFleetDim - (2 * _myFleetDim)/3 - zichtbaarDeelMyFleetIngeklapt;
	   	 LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(screensBreedte, _myFleetDim); 
	   	 lp3.gravity = Gravity.CENTER_VERTICAL;
	   	_screenTexts.setLayoutParams(lp3);
	   	
	   	LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(_myFleetDim/4, _myFleetDim); // 4 buttons
	   	lp4.leftMargin = (bMyFleetVisible ? 0 : ((int) (-1 * initLLwidth) + zichtbaarDeelMyFleetIngeklapt));	
	   	_knopjeszwik.setLayoutParams(lp4);
	   	
	   	setSlideButtonDrawable();
	   	
	   	int nBattleShipsSunk = _oppFleet.howManyBattleshipsSunk();
    	int nCruisersSunk = _oppFleet.howManyCruisersSunk();
    	int nFrigatesSunk = _oppFleet.howManyFrigatesSunk();
    	int nMinesweepersSunk = _oppFleet.howManyMinesweepersSunk();
    	kruis1.setVisibility(nBattleShipsSunk == 1 ? View.VISIBLE : View.GONE);
    	kruis2.setVisibility(nCruisersSunk == 1 ? View.VISIBLE : View.GONE);
    	kruis3.setVisibility(nFrigatesSunk >= 1 ? View.VISIBLE : View.GONE);
    	kruis4.setVisibility(nFrigatesSunk == 2 ? View.VISIBLE : View.GONE);
    	kruis5.setVisibility(nMinesweepersSunk >= 1 ? View.VISIBLE : View.GONE);
    	kruis6.setVisibility(nMinesweepersSunk >= 2 ? View.VISIBLE : View.GONE);
    	kruis7.setVisibility(nMinesweepersSunk == 3 ? View.VISIBLE : View.GONE);	   
	
    	updateChatIcon();    	
	 }
	 
	 private void updateChatIcon()
	 {
		 switch (styleID)
		 {
			 case 0:
				 ibtnChat.setBackgroundResource(bNewChatMessage ? R.drawable.chatbutton_newmessage : R.drawable.chatbutton);
				 break;
			 case 1:
				 ibtnChat.setBackgroundResource(bNewChatMessage ? R.drawable.chatbutton_newmessage_black : R.drawable.chatbutton_black);
				 break;
			 case 2:
				 ibtnChat.setBackgroundResource(bNewChatMessage ? R.drawable.chatbutton_newmessage_white : R.drawable.chatbutton_white);
				 break;
			 case 3:
				 ibtnChat.setBackgroundResource(bNewChatMessage ? R.drawable.chatbutton_newmessage_brush : R.drawable.chatbutton_brush);
				 break;
			 
		 }	
	 }
	
	 @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) 
	{
		super.onRestoreInstanceState(savedInstanceState);
		
		if (savedInstanceState != null)
		{					
			currentUsr = savedInstanceState.getString("currentUsr");
			currentOpp = savedInstanceState.getString("currentOpp");
			opphits1 = savedInstanceState.getLong("opphits1");
			opphits2 = savedInstanceState.getLong("opphits2");
			myhits1 = savedInstanceState.getLong("myhits1");
			myhits2 = savedInstanceState.getLong("myhits2");
			iCrossHair = savedInstanceState.getInt("crosshair");
			originalInviter = savedInstanceState.getString("originalInviter");
			originalInvitee = savedInstanceState.getString("originalInvitee");
			myTurn = savedInstanceState.getBoolean("myTurn");					
			gamestate = savedInstanceState.getInt("gamestate");
			gameIsOver = savedInstanceState.getBoolean("gameIsOver");
			scoreAgainst = savedInstanceState.getInt("scoreAgainst");
			scoreInFavor = savedInstanceState.getInt("scoreInFavor");
			gameID = savedInstanceState.getLong("gameID");
			sunkship = savedInstanceState.getInt("sunkship");
			readyforresult = savedInstanceState.getBoolean("readyforresult");
			lastFireResultString = savedInstanceState.getString("lastFireResultString");
			myCountry = savedInstanceState.getString("myCountry");
			hisCountry = savedInstanceState.getString("hisCountry");
			bMyFleetVisible = savedInstanceState.getBoolean("bMyFleetVisible");
		}		
	}
	 
	 @Override
	protected void onSaveInstanceState(Bundle outState) 
	{	
		opphits1 = _oppFleet.getHits1();
		opphits2 = _oppFleet.getHits2();
		myhits1 = _myFleet.getHits1();
		myhits2 = _myFleet.getHits2();
		iCrossHair = _oppFleet.getCrossHair();
				
		outState.putString("currentUsr", currentUsr);
		outState.putString("currentOpp", currentOpp);
		outState.putString("originalInviter", originalInviter);	
		outState.putString("originalInvitee", originalInvitee);	
		outState.putLong("opphits1", opphits1);
		outState.putLong("opphits2", opphits2);
		outState.putLong("myhits1", myhits1);
		outState.putLong("myhits2", myhits2);
		outState.putInt("crosshair", iCrossHair);
		outState.putBoolean("myTurn", myTurn);
		outState.putInt("gamestate", gamestate);
		outState.putBoolean("gameIsOver", gameIsOver);
		outState.putInt("scoreAgainst", scoreAgainst);
		outState.putInt("scoreInFavor", scoreInFavor);
		outState.putLong("gameID", gameID);
		outState.putInt("sunkship", sunkship);
		outState.putBoolean("readyforresult", readyforresult);
		outState.putString("lastFireResultString", lastFireResultString);
		outState.putString("myCountry", myCountry);
		outState.putString("hisCountry", hisCountry);	
		outState.putBoolean("bMyFleetVisible", bMyFleetVisible);
				
		super.onSaveInstanceState(outState);
	}	 
	 
	@Override
	protected void onPause() 
	{
			super.onPause(); 
        
	        if (refreshtimer != null)
	        {
	           	refreshtimer.cancel();
	           	refreshtimer = null;
	        }
	        if (waitforfireresulttimer != null)
	        {
	        	waitforfireresulttimer.cancel();
	        	waitforfireresulttimer = null;
	        }
	        if (fallingtimer != null)
	        {
	        	fallingtimer.cancel();
	        	fallingtimer = null;
	        }	        
	                
	        SharedPreferences.Editor editor;
			editor = sharedPrefs.edit();
	    	editor.putBoolean("inAScreenThatDoesNotNeedNotifications", false);
	    	editor.putBoolean("bMyFleetVisible", bMyFleetVisible);
	    	editor.commit(); // receive notifications after 'leaving' this screen	        
	        
	    	Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, defTimeOut); // set normale timeout terug
	        
	        _oppFleet.setMode(OpponentsFleet.PAUSE);
	        _myFleet.setMode(MyFleet.PAUSE);
	       
	    }
	
	 	@Override
		protected void onDestroy() 
		{
	 		_oppFleet.recycleBitmaps();
			_myFleet.recycleBitmaps();
			super.onDestroy();
			if (mHelper != null) 
		   		mHelper.dispose();
			mHelper = null;
			//unbindDrawables(findViewById(R.id.RootView));
			System.gc();
		}
		
		private void unbindDrawables(View view) 
		{
	        if (view.getBackground() != null) 
	        {
	        	view.getBackground().setCallback(null);
	        }
	        if (view instanceof ViewGroup) 
	        {
	            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) 
	            {
	            	unbindDrawables(((ViewGroup) view).getChildAt(i));
	            }
	            ((ViewGroup) view).removeAllViews();
	        }
	    }

    private void setUpTheScreen()
    {
        _oppFleet.setTheme(themeID);
        _myFleet.setTheme(themeID);

        if (bPaidVersion)
        {
            FragmentManager fm = getSupportFragmentManager();
            Fragment adFragment = fm.findFragmentById(R.id.adFragment);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(adFragment);
            ft.commit();

            float ondersteratio = (float) (screenMax - screenMin) / (float) screenMin;
            if (ondersteratio > 0.7) // 0.7
            {
                adOrUnderBannerHeight = (int) (screenMin / 7.2); // sgs3: breedte 720, adheight 100;
                underbanner.setVisibility(View.VISIBLE);
            }
            else
            {
                adOrUnderBannerHeight = 0;
                underbanner.setVisibility(View.GONE);
            }
        }
        else // free version
        {
            adOrUnderBannerHeight = AdSize.SMART_BANNER.getHeightInPixels(this); // sharedPrefs.getInt("adHeightPortrait", 150);
            underbanner.setVisibility(View.GONE);
        }

        switch (styleID)
        {
            case 0: //default
                ibtnResignRematch.setBackgroundResource(R.drawable.new_resign);
                if (gameIsOver)
                    ibtnResignRematch.setBackgroundResource(R.drawable.new_rematch);
                ivTellertjes.setImageResource(R.drawable.tellertjes);
                ivOverboot.setImageResource(R.drawable.overboot);
                ibtnAddafriend.setBackgroundResource(R.drawable.new_add_friend);
                ibtnHistory.setBackgroundResource(R.drawable.history);
                ibtnChat.setBackgroundResource(R.drawable.chatbutton);
                underbanner.setImageResource(R.drawable.underbanner);
                _screenTexts.setBackgroundResource(R.drawable.screens);
                break;
            case 1:
                ibtnResignRematch.setBackgroundResource(R.drawable.new_resign_black);
                if (gameIsOver)
                    ibtnResignRematch.setBackgroundResource(R.drawable.new_rematch_black);
                ivTellertjes.setImageResource(R.drawable.tellertjes_black);
                ivOverboot.setImageResource(R.drawable.overboot_black);
                ibtnAddafriend.setBackgroundResource(R.drawable.new_add_friend_black);
                ibtnHistory.setBackgroundResource(R.drawable.history_black);
                ibtnChat.setBackgroundResource(R.drawable.chatbutton_black);
                underbanner.setImageResource(R.drawable.underbanner_black);
                _screenTexts.setBackgroundResource(R.drawable.screens_black);
                break;
            case 2:
                ibtnResignRematch.setBackgroundResource(R.drawable.new_resign_white);
                if (gameIsOver)
                    ibtnResignRematch.setBackgroundResource(R.drawable.new_rematch_white);
                ivTellertjes.setImageResource(R.drawable.tellertjes_white);
                ivOverboot.setImageResource(R.drawable.overboot_white);
                ibtnAddafriend.setBackgroundResource(R.drawable.new_add_friend_white);
                ibtnHistory.setBackgroundResource(R.drawable.history_white);
                ibtnChat.setBackgroundResource(R.drawable.chatbutton_white);
                underbanner.setImageResource(R.drawable.underbanner_white);
                _screenTexts.setBackgroundResource(R.drawable.screens_white);
                break;
            case 3:
                ibtnResignRematch.setBackgroundResource(R.drawable.new_resign_brush);
                if (gameIsOver)
                    ibtnResignRematch.setBackgroundResource(R.drawable.new_rematch_brush);
                ivTellertjes.setImageResource(R.drawable.tellertjes_brush);
                ivOverboot.setImageResource(R.drawable.overboot_brush);
                ibtnAddafriend.setBackgroundResource(R.drawable.new_add_friend_brush);
                ibtnHistory.setBackgroundResource(R.drawable.history_brush);
                ibtnChat.setBackgroundResource(R.drawable.chatbutton_brush);
                underbanner.setImageResource(R.drawable.underbanner_brush);
                _screenTexts.setBackgroundResource(R.drawable.screens_brush);
                break;
        }

        _oppFleet.setOppboard(oppShipboard, oppShipdirections, opphits1, opphits2);
        _oppFleet.setFinished(gamestate == GameInstance.GS_PLAYER1_WON || gamestate == GameInstance.GS_PLAYER2_WON ||
                gamestate == GameInstance.GS_PLAYER1_RESIGNED || gamestate == GameInstance.GS_PLAYER2_RESIGNED);

        _myFleet.setConfiguredBoard(myShipboard, myShipdirections, myhits1, myhits2);

        _oppFleetDim = screenMin;
        _myFleetDim = screenMax - screenMin - adOrUnderBannerHeight;

        _oppFleet.init(_oppFleetDim,  _oppFleetDim);
        _myFleet.init((_myFleetDim), (_myFleetDim));

        _myFleet.setMode(MyFleet.RUNNING);
        _oppFleet.setMode(OpponentsFleet.RUNNING);

        int newShot = getMostRecentShotPosition(oppLastKnownHits1, oppLastKnownHits2, myhits1, myhits2);
        if (newShot >= 0)
            oppLastShot = newShot;
        oppLastKnownHits1 = myhits1;
        oppLastKnownHits2 = myhits2;

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("last"+gameID, oppLastShot);
        editor.putLong("last1h"+gameID, oppLastKnownHits1);
        editor.putLong("last2h"+gameID, oppLastKnownHits2);
        editor.putBoolean("inAScreenThatDoesNotNeedNotifications", true); // don't receive notifications in the shootactivity screen
        editor.commit();

        _myFleet.setLastShotPosition(oppLastShot);

        jgd = new JGetDataFromWebService();
        jgd.requestCheckGameInstance(this, gameID);
        jgd.requestScoreAndCountryCodes(this, gameID);

        doSounds = sharedPrefs.getBoolean("sounds", true);
        doVibrations = sharedPrefs.getBoolean("vibrations", true);

        updateHisFleetStrength();
        updateMyFleetStrength();
        updateScore();
        updateFlags();

        setFireButtonFireOrHold();
        drawScreen();
    }

	@Override
	protected void onResume()
	{
    	//adjust screen timeout
    	defTimeOut = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, DELAY);
    	Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, Math.max(defTimeOut, DELAY));
    	
    	refreshtimer = new Timer();
    	refreshtimer.schedule(new RefreshTask(this), (JConstants.UPDATE_INTERVAL_IN_GAME * 1000));

    	buttonSlideToTheSide.setOnClickListener(new View.OnClickListener()
    	{
			@Override
			public void onClick(View v) 
			{
				if (!bMyFleetAnimating)
				{
					buttonSlideToTheSide.setEnabled(false);
					ibtnAddafriend.setEnabled(false);
					ibtnHistory.setEnabled(false);
					ibtnResignRematch.setEnabled(false);
					_myFleet.setEnabled(false);
					SlideMyFleetInOrOut();
				}
				
			}	
    	});
    	
    	_myFleet.setOnClickListener(new View.OnClickListener()
    	{
			@Override
			public void onClick(View v) 
			{
				if (!bMyFleetAnimating)
				{
					SlideMyFleetInOrOut();
				}
			}	
    	});

    	ibtnFire.setEnabled(myTurn);
    	ibtnFire.setOnClickListener(new View.OnClickListener() 
    	{		
        	@Override
			public void onClick(View v)
			{        		
        		int alreadyFiredOn = _oppFleet.checkIfFiringIsPossibleHere(); // returns 0 if not fired on before
        		if (alreadyFiredOn == 0) // if it is a spot that has not been fired on before
        		{        			
        			myTurn = false;
        			somethingChanged = true;        			
        			playTheSound(soundKNALFALLING);
        			setFireButtonFireOrHold(); // will set it to HOLD
					ibtnFire.setEnabled(false); // disable the button until server reply
	        		int firePosition = _oppFleet.getCrossHair();
	        		_oppFleet.disableCrossHair();
	        		if (currentUsr.equals(originalInviter))
	        			jgd.sendFireUpdateUserX(ShootActivity.this, gameID, firePosition, 1);
	        		else if (currentOpp.equals(originalInviter))
	        			jgd.sendFireUpdateUserX(ShootActivity.this, gameID, firePosition, 2);
	        		readyforresult = false;
	        		waitforfireresulttimer = new Timer();	        		
	        		waitforfireresulttimer.schedule(new WaitTask(ShootActivity.this), 3100); //3.1 seconds between 'knal_falling' sound and result sound
	        		
        		}
			}
        });    	
	    
    	ibtnAddafriend.setEnabled(!currentOpp.startsWith("computer")); // can not add bots to friend list
    	ibtnAddafriend.setOnClickListener(new View.OnClickListener() 
    	{		
        	@Override
			public void onClick(View v)
			{   
        		if (!bMyFleetAnimating)
				{
	        		AlertDialog.Builder alertfriend = new AlertDialog.Builder(ShootActivity.this);
	           		String message2 = String.format(getString(R.string.fdialog_suretoaddfriend), currentOpp);
	           		alertfriend.setMessage(message2);
	           		alertfriend.setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() 
	                {
	                    public void onClick(DialogInterface arg0, int arg1) 
	                    {
	                    	jgd.addToFriendsList(ShootActivity.this, currentUsr, currentOpp);                    	              	    
	                    }
	                });
	
	           		alertfriend.setNegativeButton(getString(R.string.btn_no), new DialogInterface.OnClickListener() 
	                {
	             	   public void onClick(DialogInterface arg0, int arg1) 
	             	   {
	             		   //nothing
	             	   }
	                });
	           		AlertDialog alertDialog3 = alertfriend.create();
	        		alertDialog3.show();	           		
	        		setCorrectBackGroundDrawableForButtons(alertDialog3, true, true, false);
				}
			}
    	});
    	
    	ibtnResignRematch.setOnClickListener(new View.OnClickListener() 
    	{		
        	@Override
			public void onClick(View v)
			{   
        		if (!gameIsOver) // it is the resign button
        		{
	        		if (!bMyFleetAnimating)
					{
		        		AlertDialog.Builder alertbox = new AlertDialog.Builder(ShootActivity.this);
		           		String message = String.format(getString(R.string.fdialog_suretoresign), currentOpp);
		                alertbox.setMessage(message);
		                alertbox.setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() 
		                {
		                    public void onClick(DialogInterface arg0, int arg1) 
		                    {
		                    	Toast.makeText(ShootActivity.this, getString(R.string.toast_resigning), Toast.LENGTH_SHORT).show();
		                    	if (currentUsr.equals(originalInviter))
		        	            	jgd.sendResignRequest(ShootActivity.this, gameID, 1);
		                		else if (currentOpp.equals(originalInviter))
		                			jgd.sendResignRequest(ShootActivity.this, gameID, 2);
		                    }
		                });
		
		                alertbox.setNegativeButton(getString(R.string.btn_no), new DialogInterface.OnClickListener() 
		                {
		             	   public void onClick(DialogInterface arg0, int arg1) 
		             	   {
		             		   //nothing
		             	   }
		                });
		                AlertDialog alertDialog3 = alertbox.create();
		        		alertDialog3.show();	           		
		           		
		        		setCorrectBackGroundDrawableForButtons(alertDialog3, true, true, false);
		                
					}
        		}
        		else // it is the rematch button
        		{
        			jgd.sendGameInvite(ShootActivity.this, currentUsr, currentOpp);
        		}
			}
    	});
    	
    	ibtnHistory.setOnClickListener(new View.OnClickListener() 
    	{		
        	@Override
			public void onClick(View v)
			{  
        		if (!bMyFleetAnimating)
				{
        			if (!bPaidVersion)
    				{
        				showBuyAppWindow();
    				}
    				else
    				{
    					ibtnHistory.setEnabled(false);
    					showDialog(DIALOG1_KEY);
    					jgd.requestLastXBoards(ShootActivity.this, currentOpp);
    				}
				}
			}
    	});
    	
    	ibtnChat.setOnClickListener(new View.OnClickListener() 
    	{		
        	@Override
			public void onClick(View v)
			{           		
        		if (!bMyFleetAnimating)
				{
        			
        			jgd.requestPastChatMessages(ShootActivity.this, gameID);
        			
        			LayoutInflater li = LayoutInflater.from(context);
        			chatView = li.inflate(R.layout.chatwindow, null);
        			
        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        			alertDialogBuilder.setView(chatView);
        			        			
        			final EditText enterChat = (EditText) chatView.findViewById(R.id.enterChat);
        			chatScroll = (ScrollView) chatView.findViewById(R.id.chatScroll);
        			ImageButton btnSendChat = (ImageButton) chatView.findViewById(R.id.btnSendChat);        			
        			
        			btnSendChat.setOnClickListener(new View.OnClickListener() 
        			{						
						@Override
						public void onClick(View v) 
						{
							String txt = enterChat.getText().toString();
							if (txt.length() > 0 && txt.length() < 512)
							{
								if (txt.contains("'") || txt.contains("&") || txt.contains("#"))
								{
									Toast.makeText(ShootActivity.this, getString(R.string.toast_cannot_enter_funny_stuff), Toast.LENGTH_SHORT).show();								
								}
								else
								{
									enterChat.setText("");
									ibtnChat.setEnabled(false);
									jgd.sendChatMessage(ShootActivity.this, gameID, currentUsr, txt, bPaidVersion);
								}
							}							
						}
					});
        			
        			alertDialog = alertDialogBuilder.create();
        			alertDialog.show();        			
				}        		
			}
    	});
    	
    	if (lastFireResultString != "") // there is an unhandled fire result pending (possibly after screen orientation change)
    	{
    		processFireUpdateResult(lastFireResultString);
    	}

        setUpTheScreen();
        super.onResume();
	 }

	public void receiveLastXBoards(String resultStr)
	{
		if (dialog1 != null)
		{
			dialog1.cancel();
		}
		String [] resultArr = null;
		String[] fleets = new String[5];		
		
		long [] boards = new long[5];
		int [] dirs = new int[5];
		int resultCode, n = 0, i, j;
		
		resultArr = resultStr.split("#");
		resultCode = Integer.parseInt(resultArr[0]);
		
		if (resultCode == JConstants.RESULT_OK)
		{
			n = Integer.parseInt(resultArr[1]);
			for (i = 0; i < n; i++)
			{
				String s[];
				fleets[i] = resultArr[i+2];
				s = fleets[i].split("&");
				boards[i] = Long.parseLong(s[0]);
				dirs[i] = Integer.parseInt(s[1]);
			}			
		}
		
		LayoutInflater li = LayoutInflater.from(context);
		pastBoardsView = li.inflate(R.layout.past_boards, null);	
		LinearLayout layout_root = (LinearLayout) pastBoardsView.findViewById(R.id.layout_root);
		switch (themeID)
		{
			case 0:
				layout_root.setBackgroundResource(R.drawable.chatbackground);	
				break;
			case 1:
				layout_root.setBackgroundResource(R.drawable.chatbackground_green);	
				break;
			case 2:
				layout_root.setBackgroundResource(R.drawable.chatbackground_white);	
				break;
			case 3:
				layout_root.setBackgroundResource(R.drawable.chatbackground_orange);
				break;
			case 4:
				layout_root.setBackgroundResource(R.drawable.chatbackground);	
				break;
		}
		
		int min = Math.min(screenWidth, screenHeight);
	    
	    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (2 * min / 4),  (int) (2 * min / 4)); 
	    
	    _pastField[0] = (SetupPlayField) pastBoardsView.findViewById(R.id.past_field1);
	    _pastField[1] = (SetupPlayField) pastBoardsView.findViewById(R.id.past_field2);
	    _pastField[2] = (SetupPlayField) pastBoardsView.findViewById(R.id.past_field3);
	    _pastField[3] = (SetupPlayField) pastBoardsView.findViewById(R.id.past_field4);
	    _pastField[4] = (SetupPlayField) pastBoardsView.findViewById(R.id.past_field5);
	    
	    TextView one = (TextView) pastBoardsView.findViewById(R.id.one);
	    TextView two = (TextView) pastBoardsView.findViewById(R.id.two);
	    TextView three = (TextView) pastBoardsView.findViewById(R.id.three);
	    TextView four = (TextView) pastBoardsView.findViewById(R.id.four);
	    TextView five = (TextView) pastBoardsView.findViewById(R.id.five);
	    
		for (j = 0; j < 5; j++)
		{				
			_pastField[j].setTheme(bPaidVersion ? themeID : 0);
			_pastField[j].init((int) (2 * min / 4),  (int) (2 * min / 4), false, j<n);
			_pastField[j].setLayoutParams(lp);
			_pastField[j].setInitialBoard(boards[j], dirs[j]);
			_pastField[j].setMode(SetupPlayField.RUNNING);
		}	    
		if (n<5)
		{
			_pastField[4].setVisibility(View.GONE);
			five.setVisibility(View.GONE);
		}
		if (n<4) 
		{
			_pastField[3].setVisibility(View.GONE);
			four.setVisibility(View.GONE);
		}
		if (n<3)
		{
			_pastField[2].setVisibility(View.GONE);
			three.setVisibility(View.GONE);
		}
		if (n<2) 
		{
			_pastField[1].setVisibility(View.GONE);
			two.setVisibility(View.GONE);
		}
		if (n<1) 
		{
			_pastField[0].setVisibility(View.GONE);
			one.setVisibility(View.GONE);
		}
	    
		TextView uppertext = (TextView) pastBoardsView.findViewById(R.id.uppertext);
		String playerFleetHistory = String.format(getString(R.string.fsfleet_history), currentOpp);
		String playerHasNoFleetHistoryYet = String.format(getString(R.string.fhas_no_fleet_history), currentOpp);
				
		String txt = playerFleetHistory;
		if (n == 0) txt = playerHasNoFleetHistoryYet;
		uppertext.setText(txt);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(pastBoardsView);
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		
		alertDialog.show();
		alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
		    @Override
		    public void onCancel(DialogInterface dialog)
		    {
		    	_pastField[0].recycleBitmaps();
				_pastField[1].recycleBitmaps();
				_pastField[2].recycleBitmaps();
				_pastField[3].recycleBitmaps();
				_pastField[4].recycleBitmaps();
				ibtnHistory.setEnabled(true);
		    }
		});
		
	}
	
	public void receiveChatResult(int resultCode)
	{
		if (resultCode == JConstants.RESULT_OK)
			jgd.requestPastChatMessages(ShootActivity.this, gameID);
		else if (resultCode == JConstants.RESULT_CHAT_LIMIT_EXCEEDED)
		{
			Toast.makeText(context, getString(R.string.toast_chat_limit_exceeded), Toast.LENGTH_LONG).show();
			showBuyAppWindow();
		}
	}
	
	public void receivePastChatMessages(String resultStr)
	{		
		
		long globalMaxChatID;
		String resultArr[];
		resultArr = resultStr.split("#");
		int resultCode = Integer.parseInt(resultArr[0]);
		long maxChatID = -1L, chatID;
		if (resultCode == JConstants.RESULT_OK)
		{
			String txt = "";
			TextView allChat;			
			for (int i = 1; i < resultArr.length; i++)
			{
				String txtArr[] = resultArr[i].split("&");
				chatID = Long.parseLong(txtArr[0]);
				String talker = txtArr[1];
				String tekst = txtArr[2];
				txt = (currentUsr.equals(talker) ? 
						"<font color=\"#64b7eb\">" + talker + ": " + tekst + "</font><br>" + txt :
							"<font color=\"#a2c037\">" + talker + ": " + tekst + "</font><br>" + txt);
				if (chatID > maxChatID) maxChatID = chatID;
				
			}
			
			if (chatView != null)
			{
				allChat = (TextView) chatView.findViewById(R.id.allText);	
				LinearLayout chatRoot = (LinearLayout) chatView.findViewById(R.id.layout_root);
				switch (themeID)
				{
					case 0:
						chatRoot.setBackgroundResource(R.drawable.chatbackground);	
						break;
					case 1:
						chatRoot.setBackgroundResource(R.drawable.chatbackground_green);	
						break;
					case 2:
						chatRoot.setBackgroundResource(R.drawable.chatbackground_white);	
						break;
					case 3:
						chatRoot.setBackgroundResource(R.drawable.chatbackground_orange);	
						break;
					case 4:
						chatRoot.setBackgroundResource(R.drawable.chatbackground);	
						break;
				}
				allChat.setText(Html.fromHtml(txt), TextView.BufferType.SPANNABLE);
				
				chatScroll.post(new Runnable() { //scroll'em down

    		        @Override
    		        public void run() 
    		        {
    		            chatScroll.fullScroll(ScrollView.FOCUS_DOWN);
    		        }
    		    });
			
				final EditText enterChat = (EditText) chatView.findViewById(R.id.enterChat);
				enterChat.requestFocus();
				
				sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
				globalMaxChatID = sharedPrefs.getLong(currentUsr+"globalMaxChatID", -1L);
				SharedPreferences.Editor editor = sharedPrefs.edit();
		    	editor.putLong(currentUsr+"MaxChatID"+gameID, maxChatID); //zowel voor deze game
		    	if (maxChatID > globalMaxChatID)
		    		editor.putLong(currentUsr+"globalMaxChatID", maxChatID); //als globaal de max chat id opslaan
		    	editor.commit();
			}
		}
		ibtnChat.setEnabled(true);
	}
	
	 public void receiveResignResult(int resultCode)
	 {
		 switch (resultCode)
		 {
			case JConstants.RESULT_OK:
				ilost = true;
				myTurn = false;
				somethingChanged = true;
				gameIsOver = true;
				scoreAgainst++;
				updateScore();
				ibtnFire.setEnabled(false);
				setFireButtonFireOrHold(); // will set it on HOLD
				setResignButtonToRematch();				
				jgd.sendRatingUpdateRequest(ShootActivity.this, currentUsr, currentOpp);
				break;
				
			case JConstants.RESULT_NOT_OK:
				Toast.makeText(context, getString(R.string.toast_resignnotpossible), Toast.LENGTH_SHORT).show();
				break;
		
			case JConstants.RESULT_GENERAL_ERROR:
				Toast.makeText(context, getString(R.string.toast_someerror), Toast.LENGTH_SHORT).show();
				break;
		}
	 }
	 
	 public void receiveFriendInviteResult(int resultCode)
		{
			switch (resultCode)
			{
			case JConstants.RESULT_OK:
				// friend added
				Toast.makeText(ShootActivity.this, getString(R.string.toast_friendadded), Toast.LENGTH_SHORT).show();	
				break;
			case JConstants.RESULT_NOT_OK:
				// friend not added
				
				break;
			case JConstants.RESULT_INVALID_REQUEST:	
				Toast.makeText(ShootActivity.this, getString(R.string.msg_invalidrequest), Toast.LENGTH_SHORT).show();	
				break;	
			case JConstants.RESULT_NO_RESPONSE:			
				Toast.makeText(ShootActivity.this, getString(R.string.msg_nocommunication), Toast.LENGTH_SHORT).show();
				break;	
			}		
		}
	 
	 public void receiveScoreAndCountryCodesResult(String resultString)
	 {
		 int scoreA = -1, scoreB = -1;
		 String resultArr[];
		 resultArr = resultString.split("&");
		 int resultCode = Integer.parseInt(resultArr[0]);
		 
		 if (resultCode == JConstants.RESULT_OK)
		 {
			 scoreA = Integer.parseInt(resultArr[1]);
			 scoreB = Integer.parseInt(resultArr[2]);
			 myCountry = (originalInviter.equals(currentUsr) ? resultArr[3] : resultArr[4]);
			 hisCountry = (originalInviter.equals(currentUsr) ? resultArr[4] : resultArr[3]);
			 
			 SharedPreferences.Editor editor = sharedPrefs.edit();
	    	 editor.putString("countryCode", myCountry);
	    	 editor.putString("countryCode"+currentOpp, hisCountry);
	    	 editor.putInt("favor"+currentUsr+"&"+currentOpp, (originalInviter.equals(currentUsr) ? scoreA : scoreB));
	    	 editor.putInt("against"+currentUsr+"&"+currentOpp, (originalInviter.equals(currentUsr) ? scoreB : scoreA));
	    	 	    	 
	    	 editor.commit();
	    	 updateFlags();
		 }
		 
		 if (scoreA >= 0 && scoreB >= 0)
		 {
			 scoreInFavor = (originalInviter.equals(currentUsr) ? scoreA : scoreB);
			 scoreAgainst = (originalInviter.equals(currentUsr) ? scoreB : scoreA);
			 updateScore();
		 }
		 //updateFlags(); // stond hier, maar vlaggetjes werden soms wit.
	 }
	 
	 public void receiveFireUpdateResult(String resultString, int timeittook)
	 {	 		 
		 if (readyforresult) // als de communicatie langer dan 3100ms in beslag heeft genomen
		 {
			 if (waitforfireresulttimer != null)
			 {
				 waitforfireresulttimer.cancel(); // stop de timer
				 waitforfireresulttimer = null;
			 }
			 processFireUpdateResult(resultString);		 
		 }
		 else
		 {
			 //System.out.println("Storing resultstring (" + resultString + ") after " + timeittook + "ms");
			 lastFireResultString = resultString;
			 //lastResultCode = resultCode; //sla resultCode op, en laat de timer processen
		 }		 
	 }
	 
	 public void processFireUpdateResult(String resultString)
	 {		
		 if (resultString.length() == 0)
		 {
			 return;
		 }
		 lastFireResultString = "";
		 int fireResult;
		 readyforresult = true;
		 
		 int resultCode = 6, oldA = -1, newA = -1, oldB = -1, newB = -1;
		 String resultArr[];
		 if (resultString.length() > 0)
		 {
			 resultArr = resultString.split("&");
			 resultCode = Integer.parseInt(resultArr[0]);
			 if (resultCode == JConstants.RESULT_GAME_FINISHED)
			 {
				 oldA = Integer.parseInt(resultArr[1]);
				 newA = Integer.parseInt(resultArr[2]);
				 oldB = Integer.parseInt(resultArr[3]);
				 newB = Integer.parseInt(resultArr[4]);
			 }
		 }
		 
	 	switch (resultCode)
		{
		case JConstants.RESULT_OK:
						
			myTurn = false;
			somethingChanged = true;
			gamestate = 13 - gamestate; //switch game state from 6 to 7 or vice versa
			
			fireResult = _oppFleet.fire();				
			if (_oppFleet.getShotPositionGridValue() == 1) // plons
			{					
				playTheSound(soundPLONS);
			}
			else if (_oppFleet.getShotPositionGridValue() == 3) // ship hit
			{
				if (doVibrations) 
					v.vibrate(500);					
				playTheSound(soundBOEM);
			}
			else if (_oppFleet.getShotPositionGridValue() == 4) // sunken
			{
				long[] pattern = {
					    0,  // Start immediately
					    500, 100, 1000, 100, 100, 100, 50, 50, 50, 100, 50};
				if (doVibrations) 
					v.vibrate(pattern, -1);
				playTheSound(soundBOEMBUBBEL);
			}			
			
			if (fireResult >= 0)
			{
				String sankMessage = String.format(getString(R.string.ftoast_yousankaship), shipNameFromID(fireResult));
				Toast.makeText(context, sankMessage, Toast.LENGTH_SHORT).show();
			}
			updateHisFleetStrength();
			drawScreen(); //kruisjes evt hertekenen
			// nobody WON here ..
			
			iNumberOfShotsToday++;
			Calendar nu = Calendar.getInstance();		
			String dayPrefName = currentUsr +"&shots&" + nu.get(Calendar.YEAR) + "&" + (nu.get(Calendar.MONTH) + 1) + "&" + nu.get(Calendar.DAY_OF_MONTH);
			SharedPreferences.Editor editor;
			editor = sharedPrefs.edit();
	    	editor.putInt(dayPrefName, iNumberOfShotsToday);
	    	editor.commit();
	    	if (iNumberOfShotsToday % 50 == 49)
	    	{
	    		if (!didArating)
	    		{
	    			showLinkToMarket();
	    		}
	    	}
			break;
			
		case JConstants.RESULT_GAME_FINISHED: // i won!			
			
			gameIsOver = true;
			fireResult = _oppFleet.fire();
			if (_oppFleet.getShotPositionGridValue() == 1) // plons
			{					
				playTheSound(soundPLONS); // should never happen
			}
			else if (_oppFleet.getShotPositionGridValue() == 3) // ship hit
			{
				if (doVibrations) 
					v.vibrate(500);					
				playTheSound(soundBOEM);  // should never happen
			}
			else if (_oppFleet.getShotPositionGridValue() == 4) // sunken
			{
				long[] pattern = {
					    0,  // Start immediately
					    500, 100, 1000, 100, 100, 100, 50, 50, 50, 100, 50};
				if (doVibrations) 
					v.vibrate(pattern, -1);
				playTheSound(soundBOEMBUBBEL);
			}			
			
			if (fireResult >= 0)
			{
				String sankMessage = String.format(getString(R.string.ftoast_yousankaship), shipNameFromID(fireResult));
				Toast.makeText(context, sankMessage, Toast.LENGTH_SHORT).show();
			}
			updateHisFleetStrength();
			drawScreen(); //kruisjes hertekenen		
			scoreInFavor++;
			updateScore();
			setResignButtonToRematch();
	    				
			LayoutInflater li = LayoutInflater.from(context);
			View promptsView = li.inflate(R.layout.endgame_dialog, null);

			LinearLayout layout_root = (LinearLayout) promptsView.findViewById(R.id.layout_root);
			switch (themeID)
			{
				case 0:
					layout_root.setBackgroundResource(R.drawable.chatbackground_half);	
					break;
				case 1:
					layout_root.setBackgroundResource(R.drawable.chatbackground_half_green);	
					break;
				case 2:
					layout_root.setBackgroundResource(R.drawable.chatbackground_half_white);	
					break;
				case 3:
					layout_root.setBackgroundResource(R.drawable.chatbackground_half_orange);	
					break;
				case 4:
					layout_root.setBackgroundResource(R.drawable.chatbackground_half);	
					break;
			}
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

			alertDialogBuilder.setView(promptsView);				
			
			ImageView imageWinLose = (ImageView) promptsView.findViewById(R.id.imageWinLose);
			imageWinLose.setImageResource(R.drawable.win);
			final TextView tvYouWhat = (TextView) promptsView.findViewById(R.id.textUitslag);
			final TextView tvYourNewRatingVal = (TextView) promptsView.findViewById(R.id.tvYourNewRatingVal);
			final TextView tvHisNewRatingVal = (TextView) promptsView.findViewById(R.id.tvHisNewRatingVal);
			final TextView tvHisNewRating = (TextView) promptsView.findViewById(R.id.tvHisNewRating);
			
			final TextView tvScore = (TextView) promptsView.findViewById(R.id.textScore);
			
			String youWonIn = String.format(getString(R.string.fdialog_youwonin), myNumberOfShots);
			
			tvYouWhat.setText(youWonIn);
			if (currentUsr.equals(originalInviter))
			{
				if ((newA - oldA) >= 0)
					tvYourNewRatingVal.setText(newA + " (+" + (newA - oldA) + ")");
				else
					tvYourNewRatingVal.setText(newA + " (" + (newA - oldA) + ")");
				String onr = String.format(getString(R.string.opponentsnewratingcolon), currentOpp);	
				if (currentOpp.startsWith("computer")) // bot krijgt geen ratingupdatevermelding
					tvHisNewRating.setText("");
				else
					tvHisNewRating.setText(onr);
				
				if (currentOpp.startsWith("computer"))
					tvHisNewRatingVal.setText("");
				else
				{
					if ((newB - oldB) >= 0)
						tvHisNewRatingVal.setText(newB + " (+" + (newB - oldB) + ")");
					else
						tvHisNewRatingVal.setText(newB + " (" + (newB - oldB) + ")");
				}
			}
			else
			{
				if ((newB - oldB) >= 0)
					tvYourNewRatingVal.setText(newB + " (+" + (newB - oldB) + ")");
				else
					tvYourNewRatingVal.setText(newB + " (" + (newB - oldB) + ")");
				String onr = String.format(getString(R.string.opponentsnewratingcolon), currentOpp);
				if (currentOpp.startsWith("computer"))
					tvHisNewRating.setText("");
				else
					tvHisNewRating.setText(onr);
				if (currentOpp.startsWith("computer"))
					tvHisNewRatingVal.setText("");
				else
				{
					if ((newA - oldA) >= 0)
						tvHisNewRatingVal.setText(newA + " (+" + (newA - oldA) + ")");
					else
						tvHisNewRatingVal.setText(newA + " (" + (newA - oldA) + ")");
				}
			}
			tvScore.setText(getString(R.string.scorecolon) + " " + scoreInFavor + "-" + scoreAgainst);		
						
			alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton(getString(R.string.btn_rematch),
				  new DialogInterface.OnClickListener() 
				  {
				    public void onClick(DialogInterface dialog, int id) 
				    {							
				    	jgd.sendGameInvite(ShootActivity.this, currentUsr, currentOpp);
				    	dialog.cancel();					
				    }
				  })
				.setNegativeButton(getString(R.string.btn_meh),
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int id) {
				    	dialog.cancel();
				    }
				  });

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);
			
			break;
			
		case JConstants.RESULT_NOT_YOUR_TURN:	
			Toast.makeText(context, getString(R.string.toast_notyourturn), Toast.LENGTH_SHORT).show();
			break;
		case JConstants.RESULT_ALREADY_SHOT_HERE:
			Toast.makeText(context, getString(R.string.toast_alreadyshothere), Toast.LENGTH_SHORT).show();
			break;
		case JConstants.RESULT_NOT_OK:
			// game update could not be sent to server ... DO SOMETHING
			Toast.makeText(context, "Fire result NOT OK!", Toast.LENGTH_SHORT).show();	
			break;
		case JConstants.RESULT_INVALID_REQUEST:
			Toast.makeText(context, getString(R.string.msg_noconnection), Toast.LENGTH_SHORT).show();
			break;	
		case JConstants.RESULT_NO_RESPONSE:
			Toast.makeText(context, getString(R.string.msg_noconnection), Toast.LENGTH_SHORT).show();
			break;	
		case JConstants.RESULT_GENERAL_ERROR:
			Toast.makeText(context, getString(R.string.msg_generalerror), Toast.LENGTH_SHORT).show();
			break;
		}	
	 	_oppFleet.enableCrossHair();
	 }	
	 
	
	public void receiveGameInviteResult(int resultCode)
	{		
		switch (resultCode)
		{
		case JConstants.RESULT_OK:
			somethingChanged = true;
			Toast.makeText(context, getString(R.string.toast_invitationsuccessful), Toast.LENGTH_SHORT).show();			
			break;
		case JConstants.RESULT_ALREADY_RUNNING_GAME:
			Toast.makeText(context, getString(R.string.toast_alreadygamerunning), Toast.LENGTH_SHORT).show();			
			break;
		case JConstants.RESULT_CANNOT_PLAY_AGAINST_YOURSELF:
			Toast.makeText(context, getString(R.string.toast_cannotinviteyourself), Toast.LENGTH_SHORT).show();
			break;
		case JConstants.RESULT_UNKNOWN_USER:
			Toast.makeText(context, getString(R.string.toast_playernonexistent), Toast.LENGTH_SHORT).show();
			break;
		case JConstants.RESULT_INVALID_REQUEST:	
			Toast.makeText(context, getString(R.string.msg_invalidrequest), Toast.LENGTH_SHORT).show();	
			break;	
		case JConstants.RESULT_NO_RESPONSE:			
			Toast.makeText(context, getString(R.string.msg_nocommunication), Toast.LENGTH_SHORT).show();
			break;		
		}			
	}
		
	public void receiveCheckGameInstanceResult(GameInstance game) //gamesList contains only 1 game (the current) 
	{
		if (readyforresult)
		{			
			if (game != null)
			{	
				if (gamestate != game.getGameState()) //gamestate changed, that is: oppenent has fired (.. and possibly won ..)
				{
					somethingChanged = true;
					gamestate = game.getGameState();					
					gameIsOver = ((gamestate == GameInstance.GS_PLAYER1_WON) || 
							(gamestate == GameInstance.GS_PLAYER2_WON) ||
							(gamestate == GameInstance.GS_PLAYER1_RESIGNED) || 
							(gamestate == GameInstance.GS_PLAYER2_RESIGNED));
					
					if (game.getPlayerA().equals(currentUsr))
			        {					
			        	opphits1 = game.getHits1User1();
			        	opphits2 = game.getHits2User1();
			        	myhits1 = game.getHits1User2();
			        	myhits2 = game.getHits2User2();
			        	myTurn = (gamestate == GameInstance.GS_PLAYER1_TURN);
			        	if (gamestate == GameInstance.GS_PLAYER2_WON)
			        	{
			        		ilost = true;
			        	}
			        	else if (gamestate == GameInstance.GS_PLAYER2_RESIGNED)
			        	{
			        		iwon = true;
			        	}
			        }
					else if (game.getPlayerB().equals(currentUsr))
					{		        	
						opphits1 = game.getHits1User2();
			        	opphits2 = game.getHits2User2();
			        	myhits1 = game.getHits1User1();
			        	myhits2 = game.getHits2User1();
			        	myTurn = (gamestate == GameInstance.GS_PLAYER2_TURN);
			        	if (gamestate == GameInstance.GS_PLAYER1_WON)
			        	{
			        		ilost = true;			        		
			        	}
			        	else if (gamestate == GameInstance.GS_PLAYER1_RESIGNED)
			        	{
			        		iwon = true;
			        	}
			        }
					
					if (myTurn || ilost)					
					{	
						sunkship = _myFleet.updateFireShotsTEST(myhits1, myhits2); // test the waters first
						
						oppLastShot = getMostRecentShotPosition(oppLastKnownHits1, oppLastKnownHits2, myhits1, myhits2);
						oppLastKnownHits1 = myhits1;
						oppLastKnownHits2 = myhits2;
						SharedPreferences.Editor editor = sharedPrefs.edit();   		
					   	editor.putInt("last"+gameID, oppLastShot);
					   	editor.putLong("last1h"+gameID, oppLastKnownHits1);
					   	editor.putLong("last2h"+gameID, oppLastKnownHits2);
				   	 	editor.commit();
				   	 	_myFleet.setLastShotPosition(oppLastShot);
						playTheSound(soundFALLING);
						fallingtimer = new Timer();
						fallingtimer.schedule(new FallingTask(ShootActivity.this), 2600); //2.6 seconds between 'falling' sound and result sound
					}
					
					if (iwon)
					{						
						scoreInFavor++;
						updateScore();
						setResignButtonToRematch();
	        			ibtnFire.setEnabled(false);	        			
	        			jgd.sendRatingUpdateRequest(ShootActivity.this, currentUsr, currentOpp);
					}
					
					setFireButtonFireOrHold();						
        			ibtnFire.setEnabled(myTurn);
				}
								
				//check for new chat messages
				
				long maxChatID = game.getMaxChatID();
				long maxChatIDFromPrefs = sharedPrefs.getLong(currentUsr+"MaxChatID"+game.getGameID(), -1L);
				
				bNewChatMessage = (maxChatID > maxChatIDFromPrefs);
				updateChatIcon();				
				if (bNewChatMessage && chatView != null)
				{
					jgd.requestPastChatMessages(ShootActivity.this, gameID);
				}				
			}		
		}
		else
		{
			//Toast.makeText(getApplicationContext(), "Not ready for checkgameinstanceresult result, skipping a round ...", Toast.LENGTH_SHORT).show();
		}
	}	
	
	private String shipNameFromID(int sunkship)
	{
		String name = "";
		
		switch(sunkship)
		{
		case 0:
			name = getString(R.string.ship_battleship);
			break;
		case 1:
			name = getString(R.string.ship_cruiser);
			break;
		case 2:
		case 3:
			name = getString(R.string.ship_frigate);
			break;
		case 4:
		case 5:
		case 6:
			name = getString(R.string.ship_minesweeper);
			break;		
		default:
			name = "squadoosh";
		}				
		return name;
	}
	 
	protected void playTheSound(int soundID)
	{
		if (doSounds)
		{
			AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
			float actualVolume = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			float maxVolume = audioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float volume = actualVolume / maxVolume;
			if (soundloaded) 
			{
				soundPool.play(soundID, volume, volume, 1, 0, 1f);
			}
		}
	}
	
	protected void playTickSound()
	{
		playTheSound(soundTick);
	}
	
	private void updateMyFleetStrength()
	{
		int numberOfHitsOnMe = _myFleet.getNumberOfHits();
		hisNumberOfShots = _myFleet.getNumberOfShots();
		myName.setText(currentUsr);		
		myFSbar = (ImageView) findViewById(R.id.myFSBar);
		myFSbar.setImageResource(getDrawableForStrength(numberOfHitsOnMe));	
	}
	
	private void updateHisFleetStrength()
	{
		int numberOfHitsOnOpponent = _oppFleet.getNumberOfHits();
		myNumberOfShots = _oppFleet.getNumberOfShots();    	
		hisName.setText(currentOpp);    	
    	oppFSbar = (ImageView) findViewById(R.id.oppFSBar);
    	oppFSbar.setImageResource(getDrawableForStrength(numberOfHitsOnOpponent));    	
	}
	
	private void updateScore()
	{
		if (scoreInFavor > scoreAgainst)
			theScore.setTextColor(Color.rgb(0,200,0)); //green
		else if (scoreAgainst > scoreInFavor)
			theScore.setTextColor(Color.rgb(200,0,0)); //red
		else
			theScore.setTextColor(Color.rgb(229,135,0)); //orange

		if (scoreInFavor >= 0 && scoreAgainst >= 0)
			theScore.setText(scoreInFavor + "-" + scoreAgainst);
					
	}
	
	private void updateFlags()
	{
		ivMyFlag = (ImageView) findViewById(R.id.myflag);
		if (ivMyFlag != null) // crasthe hier??
			ivMyFlag.setImageResource(JConstants.getCountryFlagResourceID(myCountry));		
		ivHisFlag = (ImageView) findViewById(R.id.hisflag);
		if (ivHisFlag != null)
			ivHisFlag.setImageResource(JConstants.getCountryFlagResourceID(hisCountry));		
	}
	
	protected void processOpponentsShotResult()
	{
		if ((sunkship >= -2) && (sunkship <= 7))
		{
			if (sunkship >= 0)
			{
				if (sunkship == 7)
				{
					long[] pattern = {
							0, // don't wait
							500};
					if (doVibrations) 
						v.vibrate(pattern, -1);
					playTheSound(soundBOEM);
				}
				else
				{							
					long[] pattern = {
						    0,  // don't wait 2 sec for the 'falling' sound
						    500, 100, 1000, 50, 50, 50, 50, 50, 50};
					if (doVibrations) 
						v.vibrate(pattern, -1);
					playTheSound(soundBOEMBUBBEL);
					String toastMessage = String.format(getString(R.string.ftoast_youlostaship), shipNameFromID(sunkship));
					Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();					
				}
			}
			else if (sunkship == -1) // plonsj
			{						
				playTheSound(soundPLONS);
			}
			sunkship = -3;
		}
			
		_myFleet.updateFireShots(myhits1, myhits2); //update for real		
		
		if (_myFleet.getNumberOfHits() == MAX_HITS)
		{
			if (refreshtimer != null)
			{
				refreshtimer.cancel(); // to avoid getting the next update, and having 2 'you lost' dialogs
				ilost = true;
			}
		} 
		updateMyFleetStrength();
		
		if (ilost)
		{
			scoreAgainst++;
			updateScore();
			_oppFleet.setFinished(true);
			gameIsOver = true;
			setResignButtonToRematch();
			jgd.sendRatingUpdateRequest(ShootActivity.this, currentUsr, currentOpp);
			
		}
		else
		{
			setFireButtonFireOrHold();
			ibtnFire.setEnabled(myTurn);			
		}
	}	
	
	protected void showLinkToMarket()
	{
		final String appName = "com.kipsap.jshipbattle";		
		
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.link_market, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptsView);	
		
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton(getString(R.string.btn_yeah_sure),
		  new DialogInterface.OnClickListener() 
		  {
		    public void onClick(DialogInterface dialog, int id) 
		    {								
		    	dialog.cancel();
		    	didArating = true;
		    	SharedPreferences.Editor editor;
				editor = sharedPrefs.edit();
		    	editor.putBoolean("didArating", true); // let's say the user rated the app here
		    	editor.commit();
		    	try 
		    	{
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appName)));
				} 
		    	catch (android.content.ActivityNotFoundException anfe) 
		    	{
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+appName)));
				}
		    }
		  })
		.setNegativeButton(getString(R.string.btn_not_now),
		  new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog,int id) 
		    {
		    	dialog.cancel();
		    }
		  });
	
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();	
		setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);
		
	}
	
	protected void receiveRatingUpdateResult(String resultString)
	{		
		int resultCode, oldA = -1, newA = -1, oldB = -1, newB = -1;
		String resultArr[];
		resultArr = resultString.split("&");
		//resultCode = Integer.parseInt(resultArr[0]);
		oldA = Integer.parseInt(resultArr[0]);
		newA = Integer.parseInt(resultArr[1]);
		oldB = Integer.parseInt(resultArr[2]);
		newB = Integer.parseInt(resultArr[3]);		 // todo: add resultcode!
				
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.endgame_dialog, null);
		LinearLayout layout_root = (LinearLayout) promptsView.findViewById(R.id.layout_root);
		switch (themeID)
		{
			case 0:
				layout_root.setBackgroundResource(R.drawable.chatbackground_half);	
				break;
			case 1:
				layout_root.setBackgroundResource(R.drawable.chatbackground_half_green);	
				break;
			case 2:
				layout_root.setBackgroundResource(R.drawable.chatbackground_half_white);	
				break;
			case 3:
				layout_root.setBackgroundResource(R.drawable.chatbackground_half_orange);	
				break;
			case 4:
				layout_root.setBackgroundResource(R.drawable.chatbackground_half);	
				break;
		}

		ImageView imageWinLose = (ImageView) promptsView.findViewById(R.id.imageWinLose);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptsView);	
		
		final TextView tvYouWhat = (TextView) promptsView.findViewById(R.id.textUitslag);		
		final TextView tvScore = (TextView) promptsView.findViewById(R.id.textScore);		
		final TextView tvYourNewRatingVal = (TextView) promptsView.findViewById(R.id.tvYourNewRatingVal);
		final TextView tvHisNewRatingVal = (TextView) promptsView.findViewById(R.id.tvHisNewRatingVal);
		final TextView tvHisNewRating = (TextView) promptsView.findViewById(R.id.tvHisNewRating);
				
		if (iwon)
		{
			String yw = String.format(getString(R.string.fyouwon_opponentresigned), currentOpp);
			imageWinLose.setImageResource(R.drawable.win);
			tvYouWhat.setText(yw);
		}
		else
		{
			tvYouWhat.setText(getString(R.string.dialog_youlost));
			imageWinLose.setImageResource(R.drawable.lose);
		}
		
		if ((newA - oldA) >= 0)
			tvYourNewRatingVal.setText(newA + " (+" + (newA - oldA) + ")");
		else
			tvYourNewRatingVal.setText(newA + " (" + (newA - oldA) + ")");
		String onr = String.format(getString(R.string.opponentsnewratingcolon), currentOpp);				
		
		if (currentOpp.startsWith("computer")) // bot krijgt geen ratingupdatevermelding
			tvHisNewRating.setText("");
		else
			tvHisNewRating.setText(onr);
		
		if (currentOpp.startsWith("computer"))
			tvHisNewRatingVal.setText("");
		else
		{
			if ((newB - oldB) >= 0)
				tvHisNewRatingVal.setText(newB + " (+" + (newB - oldB) + ")");
			else
				tvHisNewRatingVal.setText(newB + " (" + (newB - oldB) + ")");
		}
		
		tvScore.setText(getString(R.string.scorecolon) + " " + scoreInFavor + "-" + scoreAgainst);		
		
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton(getString(R.string.btn_rematch),
			  new DialogInterface.OnClickListener() 
			  {
			    public void onClick(DialogInterface dialog, int id) 
			    {								
			    	jgd.sendGameInvite(ShootActivity.this, currentUsr, currentOpp);
			    	dialog.cancel();							
			    }
			  })
			.setNegativeButton(getString(R.string.btn_meh),
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) 
			    {
			    	dialog.cancel();
			    }
			  });			
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();	
		setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);
	}
		
	int getDrawableForStrength(int numberOfHits)
	{
		switch (numberOfHits)
		{
		case 0:
			return R.drawable.fs21;
		case 1:
			return R.drawable.fs20;
		case 2:
			return R.drawable.fs19;
		case 3:
			return R.drawable.fs18;
		case 4:
			return R.drawable.fs17;
		case 5:
			return R.drawable.fs16;
		case 6:
			return R.drawable.fs15;
		case 7:
			return R.drawable.fs14;
		case 8:
			return R.drawable.fs13;
		case 9:
			return R.drawable.fs12;
		case 10:
			return R.drawable.fs11;
		case 11:
			return R.drawable.fs10;
		case 12:
			return R.drawable.fs09;
		case 13:
			return R.drawable.fs08;
		case 14:
			return R.drawable.fs07;
		case 15:
			return R.drawable.fs06;
		case 16:
			return R.drawable.fs05;
		case 17:
			return R.drawable.fs04;
		case 18:
			return R.drawable.fs03;
		case 19:
			return R.drawable.fs02;
		case 20:
			return R.drawable.fs01;
		case 21:
			return R.drawable.fs00;				
		}
		
		return -1;
	}	
	
	
	int getMostRecentShotPosition(long oldh1, long oldh2, long newh1, long newh2)
	{
		int pos = -1;
		int i = 0;
		long diff1 = newh1 - oldh1;
		long diff2 = newh2 - oldh2;
		if (diff1 > 0)
		{
			while ((diff1 & (1L << i)) == 0)
			{
				i++;
			}
			pos = i;
		}
		else if (diff2 > 0)
		{
			while ((diff2 & (1L << i)) == 0)
			{
				i++;
			}
			pos = i + 60;
		}
		return pos;
	}
	
	
	
	//
	//timer classes:
	//
	
	class RefreshTask extends TimerTask 
    {		
		private ShootActivity a;		
		public RefreshTask(ShootActivity a) {
			this.a = a;
		}
		
		class RunnableLol implements Runnable {
			private ShootActivity a;
			
			public RunnableLol(ShootActivity a) {
				this.a = a;
			}
			
			@Override
			public void run() 
			{				
				this.a.jgd.requestCheckGameInstance(ShootActivity.this, gameID);					
				if (this.a.refreshtimer != null)
					this.a.refreshtimer.schedule(new RefreshTask(this.a), (JConstants.UPDATE_INTERVAL_IN_GAME * 1000));				
			}		
		}
		
		@Override
		public void run() {			
			RunnableLol r = new RunnableLol(this.a);
			this.a.refreshhandler.post(r);			
		}		
    }
	
	class WaitTask extends TimerTask 
    {		
		private ShootActivity a;		
		public WaitTask(ShootActivity a) {
			this.a = a;
		}
		
		class RunnableLol implements Runnable {
			private ShootActivity a;
			
			public RunnableLol(ShootActivity a) {
				this.a = a;
			}
			
			@Override
			public void run() 
			{
				//this.a.readyforresult = true;
				if (!readyforresult)
				{
					//System.out.println("Timer firing, calling processFireUpdateResult(" + this.a.lastFireResultString + ")");
					this.a.processFireUpdateResult(this.a.lastFireResultString);	
				}
				
				readyforresult = true;
			}		
		}
		
		@Override
		public void run() {
			RunnableLol r = new RunnableLol(this.a);
			this.a.waitforfireresulthandler.post(r);
		}
		
    }
	
	
	class FallingTask extends TimerTask {
        private ShootActivity a;

        public FallingTask(ShootActivity a) {
            this.a = a;
        }

        class RunnableLol implements Runnable {
            private ShootActivity a;

            public RunnableLol(ShootActivity a) {
                this.a = a;
            }

            @Override
            public void run() {
                this.a.processOpponentsShotResult();
            }
        }

        @Override
        public void run() {
            RunnableLol r = new RunnableLol(this.a);
            this.a.waitforfallingsoundhandler.post(r);
        }

    }

	@Override
	public void onAnimationEnd(Animation animation) 
	{	
		bMyFleetAnimating = false;		
		bMyFleetVisible = !bMyFleetVisible;
		drawScreen();
		buttonSlideToTheSide.setEnabled(true);
		ibtnAddafriend.setEnabled(true);
		ibtnHistory.setEnabled(true);
		ibtnResignRematch.setEnabled(true);
		_myFleet.setEnabled(true);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {}

	@Override
	public void onAnimationStart(Animation animation) 
	{	
		bMyFleetAnimating = true;
		if (bMyFleetVisible)
		{
			
		}
		else
		{
			LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(_myFleetDim/4, _myFleetDim); 
			//lp2.leftMargin = 0;
			_knopjeszwik.setLayoutParams(lp2); 		    
		}		
	}		
	
	private void SlideMyFleetInOrOut()
	{		
		Animation anim;
		if (bMyFleetVisible)
		{			
			anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, -0.9f, 
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 0);
		    anim.setAnimationListener(this);
		    anim.setDuration(700);		    
		    LL.startAnimation(anim);  		    
		}
		else
		{			
			anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.9f, 
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 0);
		    anim.setAnimationListener(this);		   
		    anim.setDuration(700);		    
		    LL.startAnimation(anim);
		}		
	}   
	
	private void setFireButtonFireOrHold()
	{
		switch (styleID)
		{
			case 0:
				ibtnFire.setImageResource(myTurn? R.drawable.fire_on : R.drawable.hold);
				break;
			case 1:
				ibtnFire.setImageResource(myTurn? R.drawable.fire_on_black : R.drawable.hold_black);
				break;
			case 2:
				ibtnFire.setImageResource(myTurn? R.drawable.fire_on_white : R.drawable.hold_white);
				break;
			case 3:
				ibtnFire.setImageResource(myTurn? R.drawable.fire_on_brush : R.drawable.hold_brush);
				break;
		}		
	}
	
	private void setResignButtonToRematch()
	{		
		switch (styleID)
		{
			case 0:
				ibtnResignRematch.setImageResource(R.drawable.new_rematch);
				break;
			case 1:
				ibtnResignRematch.setImageResource(R.drawable.new_rematch_black);
				break;
			case 2:
				ibtnResignRematch.setImageResource(R.drawable.new_rematch_white);
				break;
			case 3:
				ibtnResignRematch.setImageResource(R.drawable.new_rematch_brush);
				break;
		}
	}
	
	private void setSlideButtonDrawable()
	{
		switch (styleID)
		{
			case 0:
				buttonSlideToTheSide.setBackgroundResource(bMyFleetVisible ? R.drawable.slide_back : R.drawable.slide);
				break;
			case 1:
				buttonSlideToTheSide.setBackgroundResource(bMyFleetVisible ? R.drawable.slide_back_black : R.drawable.slide_black);
				break;
			case 2:
				buttonSlideToTheSide.setBackgroundResource(bMyFleetVisible ? R.drawable.slide_back_white : R.drawable.slide_white);
				// WHITE!
				break;
			case 3:
				buttonSlideToTheSide.setBackgroundResource(bMyFleetVisible ? R.drawable.slide_back : R.drawable.slide);
				// BRUSH?
				break;
		}
	}
	
	@Override
    protected Dialog onCreateDialog(int id) 
	{
        switch (id) 
        {        
	        case DIALOG1_KEY: 
	        {
	        	 dialog1 = new ProgressDialog(this);
	             dialog1.setIndeterminate(true);
	             dialog1.setCancelable(true);
	             return dialog1;
	        }	        
        }
        return null;
    }
	
	public void setCorrectBackGroundDrawableForButtons(AlertDialog aD, boolean hasPositive, boolean hasNegative, boolean hasNeutral)
    {
    	Button bn = aD.getButton(DialogInterface.BUTTON_NEGATIVE);
		Button bp = aD.getButton(DialogInterface.BUTTON_POSITIVE);
		Button b =  aD.getButton(DialogInterface.BUTTON_NEUTRAL);
    	switch (styleID)
		{
			case 0:
				if (hasPositive && bn != null)
				{
				      bn.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button));
				      bn.setTextColor(getResources().getColor(R.color.background_blue));
				}
				if (hasNegative && bp != null)
				{
				      bp.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button));
				      bp.setTextColor(getResources().getColor(R.color.background_blue));
				}
				if (hasNeutral && b != null)
				{
				      b.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button));
				      b.setTextColor(getResources().getColor(R.color.background_blue));
				}
				break;
			case 1:
				if (hasPositive && bn != null)
				{
				      bn.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_black));
				      bn.setTextColor(getResources().getColor(R.color.white));
				}
				if (hasNegative && bp != null)
				{
				      bp.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_black));
				      bp.setTextColor(getResources().getColor(R.color.white));
				}
				if (hasNeutral && b != null)
				{
				      b.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_black));
				      b.setTextColor(getResources().getColor(R.color.white));
				}
				break;
			case 2:
				if (hasPositive && bn != null)
				{
				      bn.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_white));
				      bn.setTextColor(getResources().getColor(R.color.background_blue));
				}
				if (hasNegative && bp != null)
				{
				      bp.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_white));
				      bp.setTextColor(getResources().getColor(R.color.background_blue));
				}
				if (hasNeutral && b != null)
				{
				      b.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_white));
				      b.setTextColor(getResources().getColor(R.color.background_blue));
				}
				break;
			case 3:
				if (hasPositive && bn != null)
				{
				      bn.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_brush));
				      bn.setTextColor(getResources().getColor(R.color.background_blue));
				}
				if (hasNegative && bp != null)
				{
				      bp.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_brush));
				      bp.setTextColor(getResources().getColor(R.color.background_blue));
				}
				if (hasNeutral && b != null)
				{
				      b.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_button_brush));
				      b.setTextColor(getResources().getColor(R.color.background_blue));
				}
				break;
				
		}    	
    }
	
	
	protected void showBuyAppWindow()
	{				
		LayoutInflater li = LayoutInflater.from(context);
		View buyMeView = li.inflate(R.layout.buyme, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(buyMeView);
		
		alertDialogBuilder
		.setCancelable(true)
		.setPositiveButton(getString(R.string.btn_buy_premium_version),
		  new DialogInterface.OnClickListener() 
		  {
		    public void onClick(DialogInterface dialog, int id) 
		    {	
		    	//Toast.makeText(ShootActivity.this, "going to buy it now ...", Toast.LENGTH_SHORT).show();
		    	buyPremiumVersion();
		    	dialog.cancel();
		    	
		    }
		  })
		.setNegativeButton(getString(R.string.btn_not_now),
		  new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog,int id) 
		    {
		    	dialog.cancel();
		    }
		  });
	
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();	
		setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);
		
	}
	public void buyPremiumVersion() 
    {
	     mHelper.launchPurchaseFlow(this, JConstants.SKU_PREMIUM_APP, 10001, mPremiumAppBoughtListener, "");
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
          if (!mHelper.handleActivityResult(requestCode, resultCode, data)) 
          {     
        	  	super.onActivityResult(requestCode, resultCode, data);
          }          
    }



    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment()
        { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.shootfield, container, false);
            return rootView;
        }
    }

    public static class AdFragment extends Fragment {

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        public int getTheHeight()
        {
            return mAdView.getHeight();
        }

        /**
         * Called when leaving the activity
         */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /**
         * Called when returning to the activity
         */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /**
         * Called before the activity is destroyed
         */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

    }

}
