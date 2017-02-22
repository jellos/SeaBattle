package com.kipsap.jshipbattle;

import java.util.ArrayList;

import com.kipsap.commonsource.JConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StatsActivity extends Activity {
    
	Intent intent;
	String currentUsr, selectedFoundUser = null;
	private JGetDataFromWebService jgd;
	private TextView tvPlayerStats;
	private TextView tvRatingVal, tvWonVal, tvLostVal, tvWorldRankVal, tvNationalRankVal;
	String country;
    private ImageButton viewNationalTop, viewWorldTop;
    ImageView buttonFlag;
    final Context context = this;
    SharedPreferences sharedPrefs;
    private static final int DIALOG1_KEY = 1; //waiting while getting top X data
    ProgressDialog dialog1;
    int styleID, themeID;
    LinearLayout rootView;
    boolean bPaidVersion;

    ArrayList<String> allGames, allFriends, updateTimes, allCountrys;
	ArrayList<Integer> colorStates, allRatings, allRealRanks;
    
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		country = "UNKNOWN";
		super.onCreate(savedInstanceState);
		intent = getIntent();
        Bundle bundle = intent.getExtras();
		currentUsr = bundle.getString("currentUsr");
		jgd = new JGetDataFromWebService();		
		
		setContentView(R.layout.stats);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		Typeface army = Typeface.createFromAsset(this.getAssets(), "Army.ttf");
		
		tvPlayerStats = (TextView) findViewById(R.id.textPlayerStats);
		tvRatingVal = (TextView) findViewById(R.id.tvRatingValue);
		tvWonVal = (TextView) findViewById(R.id.tvWonValue);
		tvLostVal = (TextView) findViewById(R.id.tvLostValue);
		tvWorldRankVal = (TextView) findViewById(R.id.tvWorldRankValue);
		tvNationalRankVal = (TextView) findViewById(R.id.tvNationalRankValue);
		buttonFlag = (ImageView) findViewById(R.id.landflag);
		jgd.requestPlayerStatistics(StatsActivity.this, currentUsr);
		
		String formattedText = String.format(getString(R.string.fplayerstats), currentUsr);
		tvPlayerStats.setTypeface(army);
		tvPlayerStats.setText(formattedText);
		
		viewNationalTop = (ImageButton) findViewById(R.id.btnViewNationalTop);
		viewNationalTop.setEnabled(false);
		viewNationalTop.setOnClickListener(new View.OnClickListener()
		{			
			public void onClick(View v) 
			{
				viewNationalTop.setEnabled(false);
				showDialog(DIALOG1_KEY);
				jgd.requestNationalTopX(StatsActivity.this, 25, country);
			}		
			
		});
		
		viewWorldTop = (ImageButton) findViewById(R.id.btnViewWorldTop);
		viewWorldTop.setOnClickListener(new View.OnClickListener()
		{			
			public void onClick(View v) 
			{
				viewWorldTop.setEnabled(false);
				showDialog(DIALOG1_KEY);
				jgd.requestWorldTopX(StatsActivity.this, 25);
			}		
			
		});
		
		sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
		
		styleID = Math.max(0, sharedPrefs.getInt("styleID", 0));
		themeID = Math.max(0,  sharedPrefs.getInt("themeID", 0));
        bPaidVersion = sharedPrefs.getBoolean("bPaidVersion", false);

		if (!bPaidVersion)
		{
			styleID = 0;
			themeID = 0;
		}
		
		rootView = (LinearLayout) findViewById(R.id.RootView);
		
		switch (styleID)
		{
			case 0:
				viewNationalTop.setBackgroundResource(R.drawable.top25);
				viewWorldTop.setBackgroundResource(R.drawable.top25);				
				rootView.setBackgroundResource(R.drawable.new_backgr_high_border_no_logo);
				break;
			case 1:
				viewNationalTop.setBackgroundResource(R.drawable.top25_black);
				viewWorldTop.setBackgroundResource(R.drawable.top25_black);				
				rootView.setBackgroundResource(R.drawable.new_backgr_high_border_no_logo_black);
				break;	
			case 2:
				viewNationalTop.setBackgroundResource(R.drawable.top25_white);
				viewWorldTop.setBackgroundResource(R.drawable.top25_white);				
				rootView.setBackgroundResource(R.drawable.new_backgr_high_border_no_logo_white);
				break;
			case 3:
				viewNationalTop.setBackgroundResource(R.drawable.top25_brush);
				viewWorldTop.setBackgroundResource(R.drawable.top25_brush);				
				rootView.setBackgroundResource(R.drawable.new_backgr_high_border_no_logo_brush);
				break;
		}
		
		country = sharedPrefs.getString("countryCode", "UNKNOWN");	
		viewNationalTop.setEnabled(!country.equals("UNKNOWN"));		
		buttonFlag.setImageResource(JConstants.getCountryFlagResourceID(country));
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

	@Override
	protected void onPause() 
	{		
		super.onPause();
	}
	/*
	@Override
	public void onBackPressed() 
	{	
		Bundle bundle = new Bundle();
		bundle.putString("currentUser", currentUsr);
		Intent backToGamePicker = new Intent(StatsActivity.this, JGamePicker.class);
		backToGamePicker.putExtras(bundle);
		startActivityForResult(backToGamePicker, 0);
	}	
	 */
	public void receivePlayerStatistics(String returnedString)
	{		
		if (returnedString.length() > 0)
		{
			int theRating = -1;
			
			int nWon = -1;
			int nLost = -1;
			int worldRank = -1;
			int nationalRank = -1;
			
			String resArr[] = returnedString.split("&");
			
			theRating = Integer.parseInt(resArr[0]);
			nWon = Integer.parseInt(resArr[1]);
			nLost = Integer.parseInt(resArr[2]);
			country = resArr[3];
			worldRank = Integer.parseInt(resArr[4]);
			nationalRank = Integer.parseInt(resArr[5]);
			
			tvRatingVal.setText(theRating+"");
			tvWonVal.setText(nWon+"");
			tvLostVal.setText(nLost+"");
			tvWorldRankVal.setText(worldRank+"");
			tvNationalRankVal.setText(nationalRank+"");
	
			//imFlag.setImageResource(getCountryFlagResourceID(country));
			//viewNationalTop.setCompoundDrawablesWithIntrinsicBounds(0, 0, getCountryFlagResourceID(country), 0);
			buttonFlag.setImageResource(JConstants.getCountryFlagResourceID(country));
			
			if (!country.equals("UNKNOWN"))
			{
				viewNationalTop.setEnabled(true);
				sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPrefs.edit();		    	
		    	editor.putString("countryCode", country);
		    	editor.commit();
			}
		}
		else
		{
			Toast.makeText(StatsActivity.this, getString(R.string.msg_nocommunication), Toast.LENGTH_SHORT).show();	
		}
	}
	
	
	public void populateTopXDialog(ArrayList<String> toppers)
	{
		if (dialog1 != null)
		{
			dialog1.cancel();
		}
		viewWorldTop.setEnabled(true);
		viewNationalTop.setEnabled(true);
		if (toppers.size() > 0)
		{
			allFriends = new ArrayList<String>();
			allRatings = new ArrayList<Integer>();
			allCountrys = new ArrayList<String>();	
			allRealRanks = new ArrayList<Integer>();
			
			int i;
			for (i = 0; i < toppers.size(); i++)
			{
				String frDetailsArr[] = toppers.get(i).split("&");
				allFriends.add(frDetailsArr[0]);
				allRatings.add(Integer.parseInt(frDetailsArr[1]));
				allCountrys.add(frDetailsArr[2]);
				allRealRanks.add(Integer.parseInt(frDetailsArr[3]));
			}
						
			LayoutInflater li = LayoutInflater.from(context);
			final View promptsView = li.inflate(R.layout.found_friend_dialog, null);
			LinearLayout layout_root = (LinearLayout) promptsView.findViewById(R.id.layout_root);
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
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setView(promptsView);
			
			ListView lvFoundUsers = (ListView) promptsView.findViewById(R.id.foundUsersView);
			final TopXAdapter selectedAdapter = new TopXAdapter(this, 0, allRealRanks, allFriends, allRatings, allCountrys);
			selectedAdapter.setNotifyOnChange(true);
			
			lvFoundUsers.setAdapter(selectedAdapter);
			
			lvFoundUsers.setOnItemClickListener(new OnItemClickListener() 
			{
		        @Override
		        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) 
		        {
		        	selectedAdapter.setSelectedPosition(position);   
		        	selectedFoundUser = allFriends.get(position).toString();
		        	
		        	AlertDialog.Builder alertBuilder = new AlertDialog.Builder(StatsActivity.this);
		        	alertBuilder.setMessage(R.string.whatdoyouwanttodo);
	           		String txt1 = String.format(getString(R.string.fbtn_send_player_game_invitation), selectedFoundUser);
	           		String txt2 = String.format(getString(R.string.fbtn_add_player_to_friends_list), selectedFoundUser);
	           		
	           		alertBuilder.setPositiveButton(txt1, new DialogInterface.OnClickListener() 
	                {
	                    public void onClick(DialogInterface arg0, int arg1) 
	                    {
	                    	if (selectedFoundUser != null)
							{
					    		jgd.sendGameInvite(StatsActivity.this, currentUsr, selectedFoundUser, false);
							}	              	    
	                    }
	                });

	           		alertBuilder.setNegativeButton(txt2, new DialogInterface.OnClickListener() 
	                {
	                	public void onClick(DialogInterface dialog, int id) 
					    {
	                		if (selectedFoundUser != null)
							{
	                			jgd.addToFriendsList(StatsActivity.this, currentUsr, selectedFoundUser);
							}
					    }
	                });
	           		
	           		AlertDialog alertDialog = alertBuilder.create();
	        		alertDialog.show();	           		
	        		setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);		        	
		        }
			});		
			
			AlertDialog alertDialog = alertDialogBuilder.create();	
			alertDialog.show();
		}
		else
		{
			//Toast.makeText(JGamePicker.this, getString(R.string.toast_nosuchusersfound), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void receiveGameInviteResult(String resultString)
	{		
		String [] resultArr = null;
		int resultCode = 5;
		String friend = null;
		try
		{
			resultArr = resultString.split("&");
			resultCode = Integer.parseInt(resultArr[0]);
			if (resultCode == JConstants.RESULT_OK || 
				resultCode == JConstants.RESULT_ALREADY_RUNNING_GAME)
				friend = resultArr[1];
		}
		catch (Exception exc)
		{
			System.out.println(exc.getMessage());
		}
		
		selectedFoundUser = null;
		switch (resultCode)
		{
		case JConstants.RESULT_OK:
			//Toast.makeText(JGamePicker.this, getString(R.string.toast_loggingoff), Toast.LENGTH_SHORT).show();
			// after a successful invitation, request the updated games list:			
			Toast.makeText(StatsActivity.this, getString(R.string.toast_invitationsuccessful), Toast.LENGTH_SHORT).show();
			
			break;
		case JConstants.RESULT_NOT_OK:
			Toast.makeText(StatsActivity.this, "No more users left to invite ...", Toast.LENGTH_SHORT).show();
			break;
		case JConstants.RESULT_ALREADY_RUNNING_GAME:
			Toast.makeText(StatsActivity.this, getString(R.string.toast_alreadygamerunning), Toast.LENGTH_SHORT).show();
			//if (bAddFriend)
			//{
				//Toast.makeText(JGamePicker.this, "Adding " + friend + " to friends list", Toast.LENGTH_SHORT).show();
    		//	jgd.addToFriendsList(JGamePicker.this, currentUsr, friend);
			//}
			break;
		case JConstants.RESULT_CANNOT_PLAY_AGAINST_YOURSELF:
			Toast.makeText(StatsActivity.this, getString(R.string.toast_cannotinviteyourself), Toast.LENGTH_SHORT).show();
			break;
		case JConstants.RESULT_UNKNOWN_USER:
			Toast.makeText(StatsActivity.this, getString(R.string.toast_playernonexistent), Toast.LENGTH_SHORT).show();
			break;
		case JConstants.RESULT_INVALID_REQUEST:			
			Toast.makeText(StatsActivity.this, getString(R.string.msg_invalidrequest), Toast.LENGTH_SHORT).show();	
			break;	
		case JConstants.RESULT_NO_RESPONSE:
			Toast.makeText(StatsActivity.this, getString(R.string.msg_nocommunication), Toast.LENGTH_SHORT).show();
			break;		
		}
	}
	
	public void receiveFriendInviteResult(int resultCode)
	{
		switch (resultCode)
		{
		case JConstants.RESULT_OK:
			// friend added
			Toast.makeText(StatsActivity.this, getString(R.string.toast_friendadded), Toast.LENGTH_SHORT).show();	
			break;
		case JConstants.RESULT_NOT_OK:
			// friend not added
			
			break;
		case JConstants.RESULT_INVALID_REQUEST:	
			Toast.makeText(StatsActivity.this, getString(R.string.msg_invalidrequest), Toast.LENGTH_SHORT).show();	
			break;	
		case JConstants.RESULT_NO_RESPONSE:			
			Toast.makeText(StatsActivity.this, getString(R.string.msg_nocommunication), Toast.LENGTH_SHORT).show();
			break;	
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