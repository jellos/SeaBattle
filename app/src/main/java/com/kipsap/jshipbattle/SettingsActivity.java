package com.kipsap.jshipbattle;

import com.kipsap.commonsource.JConstants;
import com.kipsap.jshipbattle.util.IabHelper;
import com.kipsap.jshipbattle.util.IabResult;
import com.kipsap.jshipbattle.util.Inventory;
import com.kipsap.jshipbattle.util.Purchase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    
	final Context context = this;
	Intent intent;
	String currentUsr; 
    TextView settingstext, backgroundtext, themetext;
    RadioButton rb1, rb2, rb3, rb4, rb5, th1, th2, th3, th4;
    ImageView i1, i2, i3, i4, i5;
    private CheckBox sounds, vibra, notif, random;
	SharedPreferences sharedPrefs;
	boolean bSounds, bVibra, bNotif, bRandomInvites, bInAppBilling, bPaidVersion;
	int themeID, styleID;
	private JGetDataFromWebService jgd;
	private RadioGroup themeGroup, styleGroup;
	private LinearLayout rootView;
	private static final String TAG = "com.kipsap.jshipbattle";
	IabHelper mHelper;
	IabHelper.OnIabPurchaseFinishedListener mPremiumAppBoughtListener;
	IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		intent = getIntent();
        Bundle bundle = intent.getExtras();
        
		currentUsr = bundle.getString("currentUsr");
		
		setContentView(R.layout.settings);		
		
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
	 	    	  if (inventory.hasPurchase(JConstants.SKU_PREMIUM_APP))
	 	    	  {
	 	    		  bPaidVersion = true;
	 	    		  Log.d(TAG, "Premium version!");
	 	    	  }
	 	    	  else
	 	    	  {
                      bPaidVersion = true; //FALSE !
	 	    		  Log.d(TAG, "Free version");
	 	    	  }

                  SharedPreferences.Editor editor;
                  editor = sharedPrefs.edit();
                  editor.putBoolean("bPaidVersion", bPaidVersion);
                  editor.commit();
	 	      }
	 	   }
	    };
	   		
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		Typeface army = Typeface.createFromAsset(this.getAssets(), "Army.ttf");
		settingstext = (TextView) findViewById(R.id.settingstext);
		settingstext.setTypeface(army);

		sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);		
		bSounds = sharedPrefs.getBoolean("sounds", true);
		bVibra = sharedPrefs.getBoolean("vibrations", true);
		bNotif = sharedPrefs.getBoolean("notifications", true);
		bRandomInvites = sharedPrefs.getBoolean("bRandomInvites", true);
		themeID = sharedPrefs.getInt("themeID", 0);
		styleID = sharedPrefs.getInt("styleID", 0);
        bPaidVersion = sharedPrefs.getBoolean("bPaidVersion", false);

		if (!bPaidVersion)
            styleID = 0;
		
		sounds = (CheckBox) findViewById(R.id.cbSoundEffects);
		vibra = (CheckBox) findViewById(R.id.cbVibrations);
		notif = (CheckBox) findViewById(R.id.cbNotifications);
		random = (CheckBox) findViewById(R.id.cbRandom);
		
		themeGroup = (RadioGroup) findViewById(R.id.themeGroup);
		styleGroup = (RadioGroup) findViewById(R.id.styleGroup);
		
		rb1 = (RadioButton) findViewById(R.id.rb1);
		rb2 = (RadioButton) findViewById(R.id.rb2);
		rb3 = (RadioButton) findViewById(R.id.rb3);
		rb4 = (RadioButton) findViewById(R.id.rb4);
		rb5 = (RadioButton) findViewById(R.id.rb5);
		th1 = (RadioButton) findViewById(R.id.rbStyle1);
		th2 = (RadioButton) findViewById(R.id.rbStyle2);
		th3 = (RadioButton) findViewById(R.id.rbStyle3);
		th4 = (RadioButton) findViewById(R.id.rbStyle4);
		
		i1 = (ImageView) findViewById(R.id.smallmap1);
		i2 = (ImageView) findViewById(R.id.smallmap2);
		i3 = (ImageView) findViewById(R.id.smallmap3);
		i4 = (ImageView) findViewById(R.id.smallmap4);
		i5 = (ImageView) findViewById(R.id.smallmap5);
		
		rootView = (LinearLayout) findViewById(R.id.RootView);
			
		switch (styleID)
		{
			case 0:
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border);
				break;
			case 1:
				if (bPaidVersion)
					rootView.setBackgroundResource(R.drawable.new_backgr_low_border_black);
				else
					showBuyAppWindow();	
				break;	
			case 2:
				if (bPaidVersion)
					rootView.setBackgroundResource(R.drawable.new_backgr_low_border_white);
				else
					showBuyAppWindow();	
				break;
			case 3:
				if (bPaidVersion)
					rootView.setBackgroundResource(R.drawable.new_backgr_low_border_brush);
				else
					showBuyAppWindow();	
				break;
		}
		
		RadioButton therb, thestylerb;
		switch (themeID)
		{
			case 0:				
				therb = rb1;
				break;
			case 1:
				if (bPaidVersion)
					therb = rb2;
				else
					therb = rb1;
				break;
			case 2:
				if (bPaidVersion)
					therb = rb3;
				else
					therb = rb1;
				break;
			case 3:
				if (bPaidVersion)
					therb = rb4;
				else
					therb = rb1;
				break;
			case 4:
				if (bPaidVersion)
					therb = rb5;
				else
					therb = rb1;
				break;
			default:
				therb = rb1;
		}				
		
		therb.setChecked(true);
		
		switch (styleID)
		{
		case 0:
			thestylerb = th1;
			break;
		case 1:
			if (bPaidVersion)
				thestylerb = th2;
			else
				thestylerb = th1;
			break;
		case 2:
			if (bPaidVersion)
				thestylerb = th3;
			else
				thestylerb = th1;
			break;
		case 3:
			if (bPaidVersion)
				thestylerb = th4;
			else
				thestylerb = th1;
			break;
		default:
			thestylerb = th1;
				
		}
		thestylerb.setChecked(true);		
	
		sounds.setChecked(bSounds);
		vibra.setChecked(bVibra);
		notif.setChecked(bNotif);
		random.setChecked(bRandomInvites);
		
		themeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
	    {
	        public void onCheckedChanged(RadioGroup group, int checkedId) 
	        {
	            // checkedId is the RadioButton selected
	        	switch (checkedId)
				{
					case R.id.rb1:
						themeID = 0;						
						break;
					case R.id.rb2:						
						if (bPaidVersion)
							themeID = 1;
						else
						{
							showBuyAppWindow();		
							rb1.setChecked(true);
						}
						break;	
						
					case R.id.rb3:
						if (bPaidVersion)
							themeID = 2;
						else
						{
							showBuyAppWindow();
							rb1.setChecked(true);
						}
						break;
						
					case R.id.rb4:
						if (bPaidVersion)
							themeID = 3;
						else
						{
							showBuyAppWindow();
							rb1.setChecked(true);
						}
						break;	
						
					case R.id.rb5:
						if (bPaidVersion)
							themeID = 4;
						else
						{
							showBuyAppWindow();
							rb1.setChecked(true);
						}
						break;	
						
				}
	        }
	    });
		
		styleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
	    {
	        public void onCheckedChanged(RadioGroup group, int checkedId) 
	        {
	            // checkedId is the RadioButton selected
	        	switch (checkedId)
				{
					case R.id.rbStyle1:						
						rootView.setBackgroundResource(R.drawable.new_backgr_low_border);
						break;
					case R.id.rbStyle2:
						if (bPaidVersion)
							rootView.setBackgroundResource(R.drawable.new_backgr_low_border_black);
						else
						{
							showBuyAppWindow();
							th1.setChecked(true);
						}
						break;	
					case R.id.rbStyle3:
						if (bPaidVersion)
							rootView.setBackgroundResource(R.drawable.new_backgr_low_border_white);
						else
						{
							showBuyAppWindow();
							th1.setChecked(true);
						}
						break;	
					case R.id.rbStyle4:
						if (bPaidVersion)
							rootView.setBackgroundResource(R.drawable.new_backgr_low_border_brush);
						else
						{
							showBuyAppWindow();
							th1.setChecked(true);
						}
						break;
				}
	        }
	    });
		
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		unbindDrawables(findViewById(R.id.RootView));
		if (mHelper != null) 
	   		mHelper.dispose();
		mHelper = null;
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
	
	protected void onResume() 
	{    	
		super.onResume(); 
    	jgd = new JGetDataFromWebService();	
    	jgd.askSettingRandomInvitations(SettingsActivity.this, currentUsr);
	}

	@Override
	protected void onPause() 
	{	
		int styleRBID = styleGroup.getCheckedRadioButtonId();
		View styleButton = styleGroup.findViewById(styleRBID);
		styleID = styleGroup.indexOfChild(styleButton);
		
		jgd.giveSettingRandomInvitations(SettingsActivity.this, currentUsr, (random.isChecked()? 1 : 0));
		sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putBoolean("sounds", sounds.isChecked());
    	editor.putBoolean("vibrations", vibra.isChecked());
    	editor.putBoolean("notifications", notif.isChecked());
    	editor.putBoolean("bRandomInvites", random.isChecked());
    	editor.putInt("themeID", themeID);
    	editor.putInt("styleID", styleID);
    	editor.putBoolean("debugmessages", false);
    	editor.commit();		
		super.onPause();
		unbindDrawables(findViewById(R.id.RootView));
        System.gc();
	}
	
	public void receiveAskSettingRandomInvitationsResult(String resultString)
	 {
		 int currentSetting;
		 String resultArr[];
		 resultArr = resultString.split("&");
		 int resultCode = Integer.parseInt(resultArr[0]);
		 
		 if (resultCode == JConstants.RESULT_OK)
		 {
			 currentSetting = Integer.parseInt(resultArr[1]);
			 bRandomInvites = (currentSetting == 1);
			 random.setChecked(bRandomInvites);
		 }		 
	 }
	
	public void receiveGiveSettingRandomInvitationsResult(String resultString)
	 {		 
		// dit is eigenlijk onzinnnig, we komen hier nooit
		
		 /*
		 int resultCode = Integer.parseInt(resultString);		 
		 if (resultCode == JConstants.RESULT_OK)
		 {
			 Toast.makeText(getApplicationContext(), "Setting successfully updated", Toast.LENGTH_SHORT).show();
		 }		
		 else
		 {
			 Toast.makeText(getApplicationContext(), "Setting didn't update", Toast.LENGTH_SHORT).show();
		 }
		 */
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
		View promptsView = li.inflate(R.layout.buyme, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptsView);	
		
		alertDialogBuilder
		.setCancelable(true)
		.setPositiveButton(getString(R.string.btn_buy_premium_version),
		  new DialogInterface.OnClickListener() 
		  {
		    public void onClick(DialogInterface dialog, int id) 
		    {	
		    	buyPremiumVersion();
		    	//Toast.makeText(SettingsActivity.this, "going to buy it now ...", Toast.LENGTH_SHORT).show();
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
}
