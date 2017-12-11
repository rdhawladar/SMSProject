package smsproject.atex.com.smsproject.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import smsproject.atex.com.smsproject.services.ServiceContinuousCall;


public class BootBroadcastReceiver extends BroadcastReceiver {   

    @Override
    public void onReceive(Context context, Intent intent) {

    	 Intent serviceIntent = new Intent(context, ServiceContinuousCall.class);
    	 context.startService(serviceIntent);
    	 ServiceContinuousCall.isServiceOn = true;

    }
}