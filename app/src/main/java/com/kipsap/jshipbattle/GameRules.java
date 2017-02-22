package com.kipsap.jshipbattle;

import com.kipsap.commonsource.JConstants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;

public class GameRules extends Activity {

	SharedPreferences sharedPrefs;
	int styleID;
    boolean bPaidVersion;
	private LinearLayout rootView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gamerules);	
		
		sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
		styleID = Math.max(0, sharedPrefs.getInt("styleID", 0));
        bPaidVersion = sharedPrefs.getBoolean("bPaidVersion", false);

		if (bPaidVersion)
            styleID = 0;
		
		rootView = (LinearLayout) findViewById(R.id.RootView);
		switch (styleID)
		{
			case 0:					
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border);
				break;
			case 1:
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_black);
				break;	
			case 2:
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_white);
				break;
			case 3:
				rootView.setBackgroundResource(R.drawable.new_backgr_low_border_brush);
				break;
		}
	}
	
}
