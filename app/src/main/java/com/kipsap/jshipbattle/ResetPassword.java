package com.kipsap.jshipbattle;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.kipsap.commonsource.JConstants;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResetPassword extends Activity {
	
	TextView changePW;
	EditText oldpw, newpw, newpw2;
	Button btnResetPW;
	private TextView output;
	String usr, theOldPW, theNewPW, theNewPW2;
	private Boolean successfulChange;
	SharedPreferences sharedPrefs;
	int styleID;
	private LinearLayout rootView;
    boolean bPaidVersion;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resetpw);		
		
		Typeface army = Typeface.createFromAsset(this.getAssets(), "Army.ttf");
				
		changePW = (TextView) findViewById(R.id.changepw);
		oldpw = (EditText) findViewById(R.id.CurrentPassword);
		newpw = (EditText) findViewById(R.id.editChoosePW);
		newpw2 = (EditText) findViewById(R.id.editRepeatPW);
		
		output = (TextView) findViewById(R.id.TextView01);
		btnResetPW = (Button) findViewById(R.id.btnResetPW);
				
		sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
		usr = sharedPrefs.getString("username", "");
		styleID = Math.max(0, sharedPrefs.getInt("styleID", 0));
        bPaidVersion = sharedPrefs.getBoolean("bPaidVersion", false);

		if (!bPaidVersion)
            styleID = 0;
		
		rootView = (LinearLayout) findViewById(R.id.RootView);
		switch (styleID)
		{
			case 0:
				btnResetPW.setBackgroundResource(R.drawable.menu_button);
				btnResetPW.setTextColor(getResources().getColor(R.color.background_blue));				
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border);
				break;
			case 1:
				btnResetPW.setBackgroundResource(R.drawable.menu_button_black);				
				btnResetPW.setTextColor(getResources().getColor(R.color.white));				
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_black);
				break;	
			case 2:
				btnResetPW.setBackgroundResource(R.drawable.menu_button_white);				
				btnResetPW.setTextColor(getResources().getColor(R.color.background_blue));				
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_white);
				break;	
			case 3:
				btnResetPW.setBackgroundResource(R.drawable.menu_button_brush);				
				btnResetPW.setTextColor(getResources().getColor(R.color.background_blue));				
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_brush);
				break;	
		}
		
		changePW.setTypeface(army);
		btnResetPW.setTypeface(army);
		
		btnResetPW.setEnabled(true);
		btnResetPW.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				theOldPW = oldpw.getText().toString();
				theNewPW = newpw.getText().toString();
				theNewPW2 = newpw2.getText().toString();				
				
				if ((theOldPW.equals("")) || (theNewPW.equals("")) || (theNewPW2.equals("")))
				{					
					display(getString(R.string.msg_passwordcannotbeempty));
				}				
				else if (!theNewPW.equals(theNewPW2))
				{
					display(getString(R.string.msg_samepasswordtwice));
				}				
				else
				{					
					new JGetDataFromWebService().sendChangePasswordRequest(ResetPassword.this,
											usr,
											md5(theOldPW),
											md5(theNewPW));
					btnResetPW.setEnabled(false); // disable the button until server reply					
				}
				
			}
		});
	}
	
	public void receiveChangePasswordResult(int returnCode)
	{
		successfulChange = false;
		btnResetPW.setEnabled(true);
		switch (returnCode)
		{
			case JConstants.RESULT_OK:
				successfulChange = true;
				display(getString(R.string.msg_passwordsuccessfullychanged));
				break;			
			case JConstants.RESULT_INVALID_REQUEST:
				display(getString(R.string.msg_invalidrequest));
				break;	
			case JConstants.RESULT_NO_RESPONSE:
				display(getString(R.string.msg_nocommunication));
				break;			
			case JConstants.RESULT_GENERAL_ERROR:
				display(getString(R.string.msg_generalerror));
				break;
			case JConstants.RESULT_WRONG_PASSWORD:
				display(getString(R.string.msg_incorrectpassword));
				break;
		}
		
		if (successfulChange)
		{
			sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPrefs.edit();	    	
	    	editor.putString("password", md5(theNewPW));
	    	editor.commit();	
	    	
	    	
	    	oldpw.setText("");
	    	newpw.setText("");
	    	newpw2.setText("");
		}
	}
	
	
	
	private void display(String text) 
	{
		output.setText(text); // + "\n" + output.getText().toString());
	}
	
	private static String md5(String s) 
	{
	    MessageDigest digest;
	    try 
	    {
	        digest = MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes(),0,s.length());
	        String hash = new BigInteger(1, digest.digest()).toString(16);
	        return hash;
	    } 
	    catch (NoSuchAlgorithmException e) 
	    {
	        e.printStackTrace();
	    }
	    return "";
	}	
	
	
}