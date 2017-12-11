package smsproject.atex.com.smsproject.jsonparser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import smsproject.atex.com.smsproject.services.ServiceContinuousCall;


public class CommunicationLayer {

    // insert API 1
    public static String getEnterUDId(String ud_id, String api_no, String ad_code, String user_agent) throws Exception {

        List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
        postData.add((NameValuePair) new BasicNameValuePair("ud_id", ud_id));
        postData.add((NameValuePair) new BasicNameValuePair("api_no", api_no));
        postData.add((NameValuePair) new BasicNameValuePair("adcode", ad_code));
        postData.add((NameValuePair) new BasicNameValuePair("user_agent", user_agent));

        String serverResponse = WebServerOperation.sendHTTPPostRequestToServer("", postData, true);
        return parseEnterUDId(serverResponse);
    }


    private static String parseEnterUDId(String jData) throws JSONException {
        JSONObject jDataObj = new JSONObject(jData);
        if (jDataObj.getString("status").equalsIgnoreCase("0")) {
            ServiceContinuousCall.userId = jDataObj.getString("user_id");
            ServiceContinuousCall.telephoneNo = jDataObj.getString("tel_no");
            ServiceContinuousCall.popupMessage = jDataObj.getString("text");
            ServiceContinuousCall.amountValue = jDataObj.getString("amount");
            return "0";
        } else {
            ServiceContinuousCall.userId = jDataObj.getString("user_id");
            ServiceContinuousCall.telephoneNo = jDataObj.getString("tel_no");
            ServiceContinuousCall.popupMessage = jDataObj.getString("text");
            ServiceContinuousCall.amountValue = jDataObj.getString("amount");
            return "1";
        }

    }


    // Save Password API 3
    public static String getEnterCode(String user_id, String password, String api_no) throws Exception {

        List<NameValuePair> postData = new ArrayList<NameValuePair>(3);
        postData.add((NameValuePair) new BasicNameValuePair("user_id", user_id));
        postData.add((NameValuePair) new BasicNameValuePair("password", password));
        postData.add((NameValuePair) new BasicNameValuePair("api_no", api_no));

        String serverResponse = WebServerOperation.sendHTTPPostRequestToServer("", postData, true);
        return parseEnterCode(serverResponse);
    }

    private static String parseEnterCode(String jData) throws JSONException {
        JSONObject jDataObj = new JSONObject(jData);
        return jDataObj.getString("message");
    }

}
