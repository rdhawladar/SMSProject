package smsproject.atex.com.smsproject.asynctaskes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import smsproject.atex.com.smsproject.interfaces.OnRequestComplete;
import smsproject.atex.com.smsproject.jsonparser.CommunicationLayer;

public class SubmitCodeAsynTask extends AsyncTask<String, Void, String> {

	private Activity activity;
	private ProgressDialog progressDialog;
	private OnRequestComplete callback;

	public SubmitCodeAsynTask (Activity activity, OnRequestComplete callback2) {
		this.activity = activity;
		this.callback = (OnRequestComplete) callback2;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = ProgressDialog.show(activity, "", "Loading...", true, false);
	}

	@Override
	protected String doInBackground(String... params) {
		
		try {
			return CommunicationLayer.getEnterCode(params[0], params[1], params[2]);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return "";
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		try {
			if ((progressDialog != null) && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (final IllegalArgumentException e) {
		} catch (final Exception e) {
		} finally {
			progressDialog = null;
		}
		
		
		Log.e("Response is.....", "---"+result);
		callback.onRequestComplete(result);
	}
	
}


