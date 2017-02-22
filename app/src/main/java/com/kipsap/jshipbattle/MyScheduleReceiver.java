package com.kipsap.jshipbattle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyScheduleReceiver extends BroadcastReceiver 
{
	private final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if(intent.getAction().equals(BOOT_COMPLETED_ACTION))
		{
			Intent serviceIntent = new Intent(context, NotifyService.class);
			context.startService(serviceIntent);
		}
		else
		{
			//System.out.println("received an intent, other than BOOT_COMPLETED_ACTION: " + intent.getAction());
		}
	}
} 