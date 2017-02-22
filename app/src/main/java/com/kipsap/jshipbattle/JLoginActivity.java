package com.kipsap.jshipbattle;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.kipsap.commonsource.JConstants;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JLoginActivity extends Activity {	
	
	EditText usr, pw;
	private TextView output;
	private Button btnLogIn;
	private Boolean bLoggedIn = false;
	private String currentUsr, md5password, username_from_prefs, password_from_prefs, theUser, thePassword;

    boolean bPaidVersion;
	private TextView t1, t2;
	private static final int DIALOG1_KEY = 0;
	SharedPreferences sharedPrefs;
	int styleID;
	private LinearLayout rootView;
	JGetDataFromWebService jgd;
	ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		Typeface army = Typeface.createFromAsset(this.getAssets(), "Army.ttf");
		
		t1 = (TextView) findViewById(R.id.textView1);
		t2 = (TextView) findViewById(R.id.textView2);
		
		t1.setTypeface(army);
		t2.setTypeface(army);
		
		int verNumber = -1;
		PackageInfo pinfo;
		try 
		{
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			verNumber = pinfo.versionCode;			
		} 
		catch (NameNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		final int versionNumber = verNumber;
		
		jgd = new JGetDataFromWebService();
		
		sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
		username_from_prefs = sharedPrefs.getString("username", "");
		password_from_prefs = sharedPrefs.getString("password", "");
		styleID = Math.max(0, sharedPrefs.getInt("styleID", 0));
        bPaidVersion = sharedPrefs.getBoolean("bPaidVersion", false);

		if (!bPaidVersion)
            styleID = 0;
		
		usr = (EditText) findViewById(R.id.EditText01);
		pw = (EditText) findViewById(R.id.EditText02);		
		
		output = (TextView) findViewById(R.id.TextView01);
		btnLogIn = (Button) findViewById(R.id.btnLogIn);
		btnLogIn.setTypeface(army);
		btnLogIn.setEnabled(true);		
		
		rootView = (LinearLayout) findViewById(R.id.RootView);
		switch (styleID)
		{
			case 0:
				btnLogIn.setBackgroundResource(R.drawable.menu_button);
				btnLogIn.setTextColor(getResources().getColor(R.color.background_blue));				
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border);
				break;
			case 1:
				btnLogIn.setBackgroundResource(R.drawable.menu_button_black);				
				btnLogIn.setTextColor(getResources().getColor(R.color.white));				
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_black);
				break;		
			case 2:
				btnLogIn.setBackgroundResource(R.drawable.menu_button_white);				
				btnLogIn.setTextColor(getResources().getColor(R.color.background_blue));				
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_white);
				break;	
				
			case 3:
				btnLogIn.setBackgroundResource(R.drawable.menu_button_brush);				
				btnLogIn.setTextColor(getResources().getColor(R.color.background_blue));				
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_brush);
				break;	
		}
		
		btnLogIn.setOnClickListener(new View.OnClickListener() {
			
		public void onClick(View v) 
		{			
			String txt;
			currentUsr = usr.getText().toString();
			md5password = md5(pw.getText().toString());
			if (currentUsr.equals(""))
			{
				txt = getString(R.string.msg_usernamecannotbeempty);
				display(txt);
			}
			else if (pw.getText().toString().equals(""))
			{
				txt = getString(R.string.msg_passwordcannotbeempty);
				display(txt);
			}
			else
			{
				showDialog(DIALOG1_KEY);
				jgd.sendLogInRequest(JLoginActivity.this,
														  currentUsr,
														  md5password, // encoded password
														  versionNumber);
				theUser = currentUsr;
				thePassword = md5password;
				btnLogIn.setEnabled(false); // disable the button until server reply
			}	
			
		}
		});
		
		if ((!username_from_prefs.equals("")) && (!password_from_prefs.equals(""))) // if there is a stored username&password, do autologin
		{	
			showDialog(DIALOG1_KEY);
			
			jgd.sendLogInRequest(JLoginActivity.this,
								  username_from_prefs,
								  password_from_prefs,
								  versionNumber);
			theUser = username_from_prefs;
			thePassword = password_from_prefs;
			btnLogIn.setEnabled(false); // disable the button until server reply
		}
	}
		
	
	
	@Override
    protected Dialog onCreateDialog(int id) 
	{
        switch (id) 
        {
            case DIALOG1_KEY: 
            {
            	 dialog = new ProgressDialog(this);
                 dialog.setIndeterminate(true);
                 dialog.setCancelable(true);
                 return dialog;                
            }            
        }
        return null;
    }

	public void receiveLogInResult(int returnCode)
	{
		if (dialog != null)
		{
			dialog.cancel();
		}
		bLoggedIn = false;
		btnLogIn.setEnabled(true);
		switch (returnCode)
		{
		case JConstants.RESULT_OK:
			bLoggedIn = true;
			display(getString(R.string.msg_loggedin));
			break;
		case JConstants.RESULT_UNKNOWN_USER:
			display(getString(R.string.msg_unknownuser));
			break;
		case JConstants.RESULT_WRONG_PASSWORD:
			display(getString(R.string.msg_incorrectpassword));
			break;
		case JConstants.RESULT_INVALID_REQUEST:
			display(getString(R.string.msg_invalidrequest));
			break;
		case JConstants.RESULT_INCORRECT_VERSION:
			display(getString(R.string.msg_incorrectversion));
			break;
		case JConstants.RESULT_NO_RESPONSE:
			display(getString(R.string.msg_nocommunication));
			break;
		}
		if (bLoggedIn)
		{				
			sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPrefs.edit();
	    	editor.putString("username", theUser);
	    	editor.putString("password", thePassword);
	    	editor.commit();
	    	goToGamePickerScreen();
			usr.setText("");
			pw.setText("");
			finish();
		}
	}
	
	private void display(String text) 
	{
		output.setText(text);
	}
	
	private void goToGamePickerScreen()
	{
		Bundle bundle = new Bundle();
		bundle.putString("currentUser", theUser);
		bundle.putBoolean("somethingChanged", true); //to force a screen refresh
		Intent goLogIn = new Intent(JLoginActivity.this, JGamePicker.class);
		goLogIn.putExtras(bundle);
		startActivityForResult(goLogIn, 0);
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