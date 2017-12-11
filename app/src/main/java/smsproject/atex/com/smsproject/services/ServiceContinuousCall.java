package smsproject.atex.com.smsproject.services;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import smsproject.atex.com.smsproject.MainActivity;
import smsproject.atex.com.smsproject.utilities.ConstantValues;

public class ServiceContinuousCall  extends Service {

	private static final String TAG = "BroadcastService";
	private final Handler handler = new Handler();
	public static boolean isOkPressed = false;
	public static boolean isServiceOn = false;
	
	public static String userId = "";
	public static String telephoneNo = "";
	public static String popupMessage = "";
	public static String amountValue = "";
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 100); // 10 second
   
    }

    private Runnable sendUpdatesToUI = new Runnable() {
    	public void run() {
    		
            if(!ConstantValues.ACTIVITY_VISIBLE && !ServiceContinuousCall.isOkPressed ){
                Intent dialogIntent = new Intent(getApplicationContext(), MainActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(dialogIntent);
            } else {

            }
            handler.postDelayed(this, 100); //1000 = 1 second
    	}
    };    

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {		
        handler.removeCallbacks(sendUpdatesToUI);		
		super.onDestroy();
	}

	
}
