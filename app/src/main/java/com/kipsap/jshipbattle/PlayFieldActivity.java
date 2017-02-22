package com.kipsap.jshipbattle;

import com.kipsap.commonsource.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PlayFieldActivity extends Activity {
	
	private SetupPlayField _myField;	
	String currentUsr, currentOpp, originalInviter;
	long shipboardUser1, shipboardUser2, myShipboard, gameID;
	int shipdirectionsUser1, shipdirectionsUser2, myShipdirections;
	ImageButton ibtnGo, ibtnShuffle, ibtnHistory;

	SetupPlayField _pastField[] = new SetupPlayField[5];
	View pastBoardsView;
	final Context context = this;
	
	private JGetDataFromWebService jgd;
	Intent intent;
	int gamestate, styleID;	
	WindowManager wm;
    Display display;
    int screenWidth, screenHeight, min, max, themeID;
    boolean bPaidVersion;
    private static final int DIALOG1_KEY = 1; //waiting while submitting game setup
    private static final int DIALOG2_KEY = 2; //waiting for fleet history
    ProgressDialog dialog1, dialog2;
    private LinearLayout rootView;
    TextView tvDoubleTap, tvConfigureYourFleet;
    SharedPreferences sharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.playfield);       
        
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        screenWidth = display.getWidth();
	    screenHeight = display.getHeight();
	    min = Math.min(screenWidth, screenHeight);
	    max = Math.max(screenWidth, screenHeight);
	    
	    sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);		
		themeID = sharedPrefs.getInt("themeID", 0);
		styleID = Math.max(0, sharedPrefs.getInt("styleID", 0));
        bPaidVersion = sharedPrefs.getBoolean("bPaidVersion", false);

		if (!bPaidVersion)
            styleID = 0;
	     
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
		
		tvConfigureYourFleet = (TextView) findViewById(R.id.tvConfigureYourFleet);
		tvDoubleTap = (TextView) findViewById(R.id.tvDoubleTap);
		ibtnGo = (ImageButton) findViewById(R.id.ibtnGo);
		ibtnShuffle = (ImageButton) findViewById(R.id.ibtnNewRandomConfig);
		ibtnHistory = (ImageButton) findViewById(R.id.ibtnHistorie);
		_myField = (SetupPlayField) findViewById(R.id.grid_view);
	    rootView = (LinearLayout) findViewById(R.id.RootView);
	    
		String tvMessage = String.format(getString(R.string.fpick_setupyourfleet), currentOpp);
		tvConfigureYourFleet.setText(tvMessage);
		
		switch (styleID)
		{
			case 0:
				rootView.setBackgroundResource(R.drawable.new_backgr_extra_high_border);
				ibtnGo.setBackgroundResource(R.drawable.go);
				ibtnShuffle.setBackgroundResource(R.drawable.shuffle);
				ibtnHistory.setBackgroundResource(R.drawable.history);
				tvConfigureYourFleet.setTextColor(getResources().getColor(R.color.background_blue));
				tvDoubleTap.setTextColor(getResources().getColor(R.color.background_blue));
				break;
			case 1:
				rootView.setBackgroundResource(R.drawable.new_backgr_extra_high_border_black);
				ibtnGo.setBackgroundResource(R.drawable.go_black);
				ibtnShuffle.setBackgroundResource(R.drawable.shuffle_black);
				ibtnHistory.setBackgroundResource(R.drawable.history_round_black);
				tvConfigureYourFleet.setTextColor(getResources().getColor(R.color.white));
				tvDoubleTap.setTextColor(getResources().getColor(R.color.white));
				break;
			case 2:
				rootView.setBackgroundResource(R.drawable.new_backgr_extra_high_border_white);
				ibtnGo.setBackgroundResource(R.drawable.go_white);
				ibtnShuffle.setBackgroundResource(R.drawable.shuffle_white);
				ibtnHistory.setBackgroundResource(R.drawable.history_round_white);
				tvConfigureYourFleet.setTextColor(getResources().getColor(R.color.background_blue));
				tvDoubleTap.setTextColor(getResources().getColor(R.color.background_blue));
				break;
			case 3:
				rootView.setBackgroundResource(R.drawable.new_backgr_extra_high_border_brush);				
				ibtnGo.setBackgroundResource(R.drawable.go_brush);
				ibtnShuffle.setBackgroundResource(R.drawable.shuffle_brush);
				ibtnHistory.setBackgroundResource(R.drawable.history_round_brush);
				tvConfigureYourFleet.setTextColor(getResources().getColor(R.color.background_blue));
				tvDoubleTap.setTextColor(getResources().getColor(R.color.background_blue));
				break;
		}
		
		if (originalInviter.equals(currentUsr))
        {
        	myShipboard = shipboardUser1;
        	myShipdirections = shipdirectionsUser1;        	
        }
        else if (originalInviter.equals(currentOpp))
        {
        	myShipboard = shipboardUser2;
        	myShipdirections = shipdirectionsUser2;         	
        }
		
	    if (bPaidVersion)
		{
	    	_myField.setTheme(themeID);
	    	//override the shipboard+directions with the saved favorite		
			myShipboard = sharedPrefs.getLong(currentUsr + "favBoard", myShipboard);
			myShipdirections = sharedPrefs.getInt(currentUsr + "favDirections", myShipdirections);
		}
		else // free version: set themeID 0 no matter what
		{
			_myField.setTheme(0);
		}	  
	    _myField.init(min - 10,  min - 10, true, true);
	     	
	    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(min - 10, min - 10);
	    _myField.setLayoutParams(lp);		
    }

	public void receiveGameUpdateResult(int resultCode)
	{
		if (dialog1 != null)
		{
			dialog1.cancel();
		}
		ibtnGo.setEnabled(true);
		switch (resultCode)
		{
		case JConstants.RESULT_OK:
			// aok
			//_myField.gameUpdateResultSuccessful(true);	//doet niks		
			//Intent resultIntent = new Intent();		
			//resultIntent.putExtra("somethingChanged", true);
			//setResult(Activity.RESULT_OK, resultIntent);
			finish();
			
			break;
		case JConstants.RESULT_NOT_OK:
			// game update could not be sent to server ... DO SOMETHING
			_myField.gameUpdateResultSuccessful(false); // meh
			break;		
		case JConstants.RESULT_INVALID_REQUEST:			
			Toast.makeText(PlayFieldActivity.this, getString(R.string.msg_invalidrequest), Toast.LENGTH_SHORT).show();	
			break;	
		case JConstants.RESULT_NO_RESPONSE:
			Toast.makeText(PlayFieldActivity.this, getString(R.string.msg_nocommunication), Toast.LENGTH_SHORT).show();
			break;	
		}		
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) 
	{		
		super.onRestoreInstanceState(savedInstanceState);
				
		gameID = savedInstanceState.getLong("gameID");
		shipboardUser1 = savedInstanceState.getLong("shipboardUser1");
		shipboardUser2 = savedInstanceState.getLong("shipboardUser2");
		shipdirectionsUser1 = savedInstanceState.getInt("shipdirectionsUser1");
		shipdirectionsUser2 = savedInstanceState.getInt("shipdirectionsUser2");
		currentUsr = savedInstanceState.getString("currentUsr");
		currentOpp = savedInstanceState.getString("currentOpp");
		originalInviter = savedInstanceState.getString("originalInviter");			
		
		if (originalInviter.equals(currentUsr))
        {
        	myShipboard = shipboardUser1;
        	myShipdirections = shipdirectionsUser1;        	
        }
        else if (originalInviter.equals(currentOpp))
        {
        	myShipboard = shipboardUser2;
        	myShipdirections = shipdirectionsUser2;  
        }
        else
        {
        	System.out.println("No way");
        }
	}    
	
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{				
		myShipboard = _myField.getBoard();
		myShipdirections = _myField.getDirections();
		
		if (originalInviter.equals(currentUsr))
		{
			shipboardUser1 = myShipboard;
			shipdirectionsUser1 = myShipdirections;   
		}
		else if (originalInviter.equals(currentOpp))
		{
			shipboardUser2 = myShipboard;
			shipdirectionsUser2 = myShipdirections;
		}
		else
		{
			System.out.println("NEIN");
		}
		
		outState.putLong("shipboardUser1", shipboardUser1);
		outState.putLong("shipboardUser2", shipboardUser2);
		outState.putInt("shipdirectionsUser1", shipdirectionsUser1);
		outState.putInt("shipdirectionsUser2", shipdirectionsUser2);
		outState.putString("currentUsr", currentUsr);
		outState.putString("currentOpp", currentOpp);
		outState.putString("originalInviter", originalInviter);		
		outState.putLong("gameID", gameID);
		System.out.println("onSaveInstanceState");	
		
		super.onSaveInstanceState(outState);
	}    
    
	@Override
	protected void onDestroy() 
	{
		_myField.recycleBitmaps();
		super.onDestroy();
		unbindDrawables(findViewById(R.id.RootView));
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
	
    @Override
    protected void onPause() 
    {
        _myField.setMode(SetupPlayField.PAUSE);        
		super.onPause(); 
    }
    
    protected void onResume() 
    {    	
    	jgd = new JGetDataFromWebService();	
        
    	_myField = (SetupPlayField) findViewById(R.id.grid_view);     	       
    	_myField.setInitialBoard(myShipboard, myShipdirections);        
    	
    	ibtnGo.setEnabled(true);
    	ibtnGo.setOnClickListener(new View.OnClickListener() {
		
        	@Override
			public void onClick(View v) 
			{        		
        		myShipboard = _myField.getBoard();
        		myShipdirections = _myField.getDirections();        		
        		
    			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayFieldActivity.this);
    			alertDialogBuilder.setMessage(getString(R.string.dialog_areyousure_to_start));           		
           		
    			alertDialogBuilder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() 
                {
                    public void onClick(DialogInterface arg0, int arg1) 
                    {
                    	if (currentUsr.equals(originalInviter))
        					jgd.sendBoardUpdateNEW(PlayFieldActivity.this, gameID, myShipboard, myShipdirections, 1);
        				else if (currentOpp.equals(originalInviter))
        					jgd.sendBoardUpdateNEW(PlayFieldActivity.this, gameID, myShipboard, myShipdirections, 2);
                    	
                    	ibtnGo.setEnabled(false); // disable the button until server reply
        				showDialog(DIALOG1_KEY); // show a spinning waiting thingy   
                    }
                });

    			alertDialogBuilder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() 
                {
                	public void onClick(DialogInterface dialog, int id) 
				    {
                		return;
				    }
                });
                
                AlertDialog alertDialog = alertDialogBuilder.create();	
    			alertDialog.show();
    			setCorrectBackGroundDrawableForButtons(alertDialog, true, true, false);
               
			}
        });     
              
    	ibtnShuffle.setEnabled(true);
        
        if (!bPaidVersion)
		{	
        	ibtnShuffle.setVisibility(View.GONE);
		}
        else
        {
        	ibtnShuffle.setOnClickListener(new View.OnClickListener() {
			
	        	@Override
				public void onClick(View v) 
				{          		
	        		int nTries = _myField.setRandomBoard();
	        		if (nTries >= 100) // het is niet gelukt, doe dan maar de standaard opstelling
	        		{
	        			_myField.setInitialBoard(myShipboard, myShipdirections);
	        		}
	        		enableGoAndFavoriteButton(true);
	        		//Toast.makeText(getApplicationContext(), "nTries: " + nTries, Toast.LENGTH_SHORT).show();	
				}
	        });
        }
        
        ibtnHistory.setEnabled(true);
        if (!bPaidVersion)
		{	
        	ibtnHistory.setVisibility(View.GONE);
		}
        else
        {        	
        	ibtnHistory.setOnClickListener(new View.OnClickListener()
        	{        		
	        	@Override
				public void onClick(View v) 
				{          
	        		ibtnHistory.setEnabled(false);
        			jgd.requestLastXBoards(PlayFieldActivity.this, currentOpp);
					showDialog(DIALOG2_KEY);
				}
	        });
        }
        _myField.setMode(SetupPlayField.RUNNING); 
        super.onResume();        
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
	        case DIALOG2_KEY: 
	        {
	        	 dialog2 = new ProgressDialog(this);
	             dialog2.setIndeterminate(true);
	             dialog2.setCancelable(true);
	             return dialog2;
	        }  
       
        }
        return null;
    }
    
    @Override
	public void onBackPressed() 
	{	
		//Bundle bundle = new Bundle();
		//bundle.putString("currentUser", currentUsr);
		//Intent backToGamePicker = new Intent(PlayFieldActivity.this, JGamePicker.class);
		//backToGamePicker.putExtras(bundle);
		//startActivityForResult(backToGamePicker, 0);	
		
		//Intent resultIntent = new Intent();		
		//resultIntent.putExtra("somethingChanged", false);
		//setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
    
    protected void enableGoAndFavoriteButton(boolean bvalidBoard)
    {
    	ibtnHistory.setEnabled(true);
    	ibtnGo.setEnabled(bvalidBoard);
    	switch (styleID)
    	{
	    	case 0:
	    		ibtnGo.setImageResource(bvalidBoard ? R.drawable.go : R.drawable.go_disabled);
	    		break;
	    	case 1:
	    		ibtnGo.setImageResource(bvalidBoard ? R.drawable.go_black : R.drawable.go_disabled_black);
	    		break;
	    	case 2:
	    		ibtnGo.setImageResource(bvalidBoard ? R.drawable.go_white : R.drawable.go_disabled_white);
	    		break;
	    	case 3:
	    		ibtnGo.setImageResource(bvalidBoard ? R.drawable.go_brush : R.drawable.go_disabled_brush);
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
    
    
    public void receiveLastXBoards(String resultStr)
	{    	
		if (dialog2 != null)
		{
			dialog2.cancel();
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
		
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenWidth = display.getWidth();
	    screenHeight = display.getHeight();
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
    
}
