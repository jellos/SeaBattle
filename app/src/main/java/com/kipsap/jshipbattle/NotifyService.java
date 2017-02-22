package com.kipsap.jshipbattle;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import com.kipsap.commonsource.*;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class NotifyService extends Service 
{
	final Context context = this;	
	private NotificationManager nm;
	private static Timer timer = new Timer();
	private long maxChatID;
	SharedPreferences sharedPrefs;
	String username;
	private JGetDataFromWebService jgd;
	String _notifyTxt;
	boolean bNotifications, waitforanswer;
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		jgd = new JGetDataFromWebService();
		nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
        username = sharedPrefs.getString("username", "");
        bNotifications = sharedPrefs.getBoolean("notifications", false);
        waitforanswer = false;
        timer.scheduleAtFixedRate(new requestTask(), 0, (JConstants.UPDATE_INTERVAL_BACKGROUND_SERVICE * 1000));
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
        // Cancel the persistent notification.
		shutdownCounter();
        nm.cancel(R.string.service_label);
	}
	
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() 
    {
        // In this sample, we'll use the same text for the ticker and the expanded notification
    	CharSequence text = _notifyTxt;
    	   	    	 
        Notification notification = new Notification(R.drawable.notif_ch, text, System.currentTimeMillis());
                
        
        //notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        //notification.defaults = Notification.DEFAULT_VIBRATE; // goes bezerk!@
        
        //notification.defaults = Notification.DEFAULT_ALL;
        
        //notification.defaults |= Notification.DEFAULT_VIBRATE;
        //notification.sound = Uri.parse("android.resource://com.kipsap.jshipbattle/raw/shoot");
        
        notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sonar);

        Bundle bundle = new Bundle();
		bundle.putString("currentUser", username);
		bundle.putBoolean("somethingChanged", true); //to force a screen refresh
        Intent i = new Intent(this, JGamePicker.class);
        i.putExtras(bundle);
        PendingIntent contentIntent;
        if (username.length() > 0)
        {
        	contentIntent = PendingIntent.getActivity(this, 0, i, 0); // if a username is stored, go directly to his gamepicker screen
        }
        else
        {
        	contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, JLoginActivity.class), 0); // otherwise, goto login screen
        }

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.service_label), text, contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        nm.notify(R.string.service_label, notification);
    }   
    
    private void shutdownCounter() 
    {
    	if (timer != null) 
    	{
    		timer.cancel();
    	}
    }   
    
    public void receiveLatestChatInfo(long serverMaxChatID)
    {
    	waitforanswer = false;
    	
    	sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
     	maxChatID = sharedPrefs.getLong(username+"globalMaxChatID", -1L);
    	
    	if (serverMaxChatID > 0 && username.length() > 0)
    	{
    		if (serverMaxChatID > maxChatID)
    		{
    			// new chats
    			SharedPreferences.Editor editor;    					
				editor = sharedPrefs.edit();
		    	editor.putLong(username+"globalMaxChatID", serverMaxChatID);
		    	editor.commit();
		    	if (!sharedPrefs.getBoolean("inAScreenThatDoesNotNeedNotifications", true))	
				{
		    		_notifyTxt = getString(R.string.new_chat_message);
		    		//_notifyTxt = "New chat message";
		    		showNotification();	
				}
    		}
    	}
        
    	//Toast.makeText(getApplicationContext(), "Max chat ID: " + maxChatID, Toast.LENGTH_SHORT).show();
    }
    
    public void receiveLatestGameInfo_LEAN(String resultStr)
    {
    	waitforanswer = false;
    	SharedPreferences.Editor editor;  
    	String resultArr[] = resultStr.split("&");
    	sharedPrefs = this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
    	boolean anyDifference = false;
    	
    	int resultCode = Integer.parseInt(resultArr[0]);
		long gameID = -1L;
		int gameState = -1, whichPlayerAmI = -1;
		String opponent = "";
		if (resultCode == JConstants.RESULT_OK)
		{
			gameID = Long.parseLong(resultArr[1]);
			whichPlayerAmI = Integer.parseInt(resultArr[2]);
			opponent = resultArr[3];
			gameState = Integer.parseInt(resultArr[4]);
			
			String id_and_state_stamp_prefs = sharedPrefs.getString("id_and_state_stamp", "");
			String id_and_state_stamp_now = gameID + "&" + gameState;
			
			if (!id_and_state_stamp_prefs.equals(id_and_state_stamp_now)) //check if the NEWEST gameID + gamestate combination has changed
			{				
				if (whichPlayerAmI == 1)
		    	{	// i'm player A
					if (gameState == 12)  //hurry up text!
					{
						_notifyTxt = String.format(getString(R.string.fnoti_24hleft), opponent);
		    			anyDifference = true;
					}
					else if (gameState == GameInstance.GS_PLAYER1_TURN)
		    		{
		    			_notifyTxt = String.format(getString(R.string.fpick_yourturn), opponent);
		    			anyDifference = true;
		    		}
		    		else if (gameState == GameInstance.GS_BOARD_BOTH_NOTREADY)
		    		{
		    			_notifyTxt = String.format(getString(R.string.fpick_setupyourfleet), opponent);
		    			anyDifference = true;
		    		}
		    		else if (gameState == GameInstance.GS_PLAYER2_BOARD_READY)
		    		{
		    			_notifyTxt = String.format(getString(R.string.fnoti_opponentwaiting), opponent);
		    			anyDifference = true;
		    		}
		    		else if (gameState == GameInstance.GS_PLAYER2_WON)
		    		{
		    			_notifyTxt = String.format(getString(R.string.fnoti_youlostvs), opponent);
		    			anyDifference = true;
		    		}		
		    		else if (gameState == GameInstance.GS_PLAYER2_RESIGNED)
		    		{
		    			_notifyTxt = String.format(getString(R.string.fpick_opponentresigned), opponent);
		    			anyDifference = true;
		    		}	
		    	}
		    	else if (whichPlayerAmI == 2)
		    	{	// i'm player B
		    		if (gameState == 12)  //hurry up text!
					{
		    			_notifyTxt = String.format(getString(R.string.fnoti_24hleft), opponent);
		    			anyDifference = true;
					}
					else if (gameState == GameInstance.GS_PLAYER2_TURN)
		    		{
		    			_notifyTxt = String.format(getString(R.string.fpick_yourturn), opponent);
		    			anyDifference = true;
		    		}
		    		else if (gameState == GameInstance.GS_BOARD_BOTH_NOTREADY)
		    		{
		    			_notifyTxt = String.format(getString(R.string.fpick_setupyourfleet), opponent);
		    			anyDifference = true;
		    		}
		    		else if (gameState == GameInstance.GS_PLAYER1_BOARD_READY)
		    		{
		    			_notifyTxt = String.format(getString(R.string.fnoti_opponentwaiting), opponent);
		    			anyDifference = true;
		    		}
		    		else if (gameState == GameInstance.GS_PLAYER1_WON)
		    		{
		    			_notifyTxt = String.format(getString(R.string.fnoti_youlostvs), opponent);
		    			anyDifference = true;
		    		}
		    		else if (gameState == GameInstance.GS_UNACCEPTED)
		    		{
		    			_notifyTxt = String.format(getString(R.string.ffnoti_openinvitation), opponent);
		    			anyDifference = true;
		    		}
		    		else if (gameState == GameInstance.GS_PLAYER1_RESIGNED)
		    		{
		    			_notifyTxt = String.format(getString(R.string.fpick_opponentresigned), opponent);
		    			anyDifference = true;
		    		}	
		    	}				
				
			}
			if (!sharedPrefs.getBoolean("inAScreenThatDoesNotNeedNotifications", true))	
			{
				if (anyDifference)
				{
					showNotification();				
					editor = sharedPrefs.edit();
			    	editor.putString("id_and_state_stamp", id_and_state_stamp_now); // adjust the sharedprefs to the updated situation in order not to get the same notification multiple times
			    	editor.commit();
				}
			}			
		}    	      
    	
    }
    
    private class requestTask extends TimerTask
    { 
        public void run() 
        {           	
        	sharedPrefs = NotifyService.this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);       	
        	bNotifications = sharedPrefs.getBoolean("notifications", true);
        	if (bNotifications)
        	{       		
    			
    			requestHandler.sendEmptyMessage(0);    					
        	}
        }
    }
    
    private final Handler requestHandler = new Handler()
    {    	
        @SuppressWarnings("deprecation")
		@Override
        public void handleMessage(Message msg)
        {        	
        	sharedPrefs = NotifyService.this.getSharedPreferences("com.kipsap.jshipbattle", Context.MODE_PRIVATE);
            username = sharedPrefs.getString("username", "");
            if (username.length() > 0)
            {
            	if (!waitforanswer)
            	{
            		waitforanswer = true;
            		Date d = new Date();
            		if (d.getMinutes() % 2 == 1)
            			jgd.requestMostRecentGame(NotifyService.this, username);
            		 else
            			jgd.requestCheckNewChatMessages(NotifyService.this, username);
            		
            	}
            	//else
            	//{
            	//	Toast.makeText(getApplicationContext(), "Service superfluous non-request", Toast.LENGTH_SHORT).show();
            	//}
            }
        }
    };
}
