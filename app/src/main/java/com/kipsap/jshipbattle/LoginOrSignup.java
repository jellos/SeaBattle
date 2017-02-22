package com.kipsap.jshipbattle;

import com.kipsap.commonsource.JConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class LoginOrSignup extends Activity {

private Button buttonLogIn, buttonSignUp;
SharedPreferences sharedPrefs;
boolean newb, bPaidVersion;
final Context context = this;
int styleID;
private LinearLayout rootView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);	
		
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		Typeface army = Typeface.createFromAsset(this.getAssets(), "Army.ttf");
		
		sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);		
		styleID = Math.max(0, sharedPrefs.getInt("styleID", 0));
        bPaidVersion = sharedPrefs.getBoolean("bPaidVersion", false);

		if (!bPaidVersion)
            styleID = 0;
		
		buttonLogIn = (Button) findViewById(R.id.btnWantToLogIn);
		buttonSignUp = (Button) findViewById(R.id.btnWantToSignUp);
		buttonLogIn.setTypeface(army);
		buttonSignUp.setTypeface(army);
		rootView = (LinearLayout) findViewById(R.id.RootView);
		switch (styleID)
		{
			case 0:
				buttonLogIn.setBackgroundResource(R.drawable.menu_button);
				buttonSignUp.setBackgroundResource(R.drawable.menu_button);
				buttonLogIn.setTextColor(getResources().getColor(R.color.background_blue));
				buttonSignUp.setTextColor(getResources().getColor(R.color.background_blue));
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border);
				break;
			case 1:
				buttonLogIn.setBackgroundResource(R.drawable.menu_button_black);
				buttonSignUp.setBackgroundResource(R.drawable.menu_button_black);
				buttonLogIn.setTextColor(getResources().getColor(R.color.white));
				buttonSignUp.setTextColor(getResources().getColor(R.color.white));
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_black);
				break;	
			case 2:
				buttonLogIn.setBackgroundResource(R.drawable.menu_button_white);
				buttonSignUp.setBackgroundResource(R.drawable.menu_button_white);
				buttonLogIn.setTextColor(getResources().getColor(R.color.background_blue));
				buttonSignUp.setTextColor(getResources().getColor(R.color.background_blue));
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_white);
				break;	
			case 3:
				buttonLogIn.setBackgroundResource(R.drawable.menu_button_brush);
				buttonSignUp.setBackgroundResource(R.drawable.menu_button_brush);
				buttonLogIn.setTextColor(getResources().getColor(R.color.background_blue));
				buttonSignUp.setTextColor(getResources().getColor(R.color.background_blue));
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_brush);
				break;	
		}
		
		
		
		buttonLogIn.setOnClickListener(new OnClickListener()
		{			
			public void onClick(View v) 
			{
				Intent goLogIn = new Intent(LoginOrSignup.this, JLoginActivity.class);
				startActivity(goLogIn);				
			}
		});

		buttonSignUp.setOnClickListener(new OnClickListener() 
		{	
			public void onClick(View v) 
			{
				Intent goSignUp = new Intent(LoginOrSignup.this, JSignupActivity.class);
				startActivity(goSignUp);		
			}
		});
		
		sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
		newb = sharedPrefs.getBoolean("newb", true);
		
		if (newb)
		{
			
			LayoutInflater li = LayoutInflater.from(context);
			View newbView = li.inflate(R.layout.newbie_dialog, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

			alertDialogBuilder.setView(newbView);
			alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					SharedPreferences.Editor editor;
					editor = sharedPrefs.edit();
			    	editor.putBoolean("newb", false); // not a newb anymore
			    	editor.commit();
					
				}
			});
			
			AlertDialog alertDialog = alertDialogBuilder.create();		
			alertDialog.show();			
			setCorrectBackGroundDrawableForButtons(alertDialog, false, false, true);		
    		
		}
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
	
	
}
