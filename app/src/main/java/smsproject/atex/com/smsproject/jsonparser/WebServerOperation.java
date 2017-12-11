package smsproject.atex.com.smsproject.jsonparser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class WebServerOperation {

	private static CookieStore cookieStore;

	public static String sendHTTPPostRequestToServer(String urlPath, List<NameValuePair> postData, boolean readFromServer) throws Exception {
		String data = "";

		HttpClient httpclient = new DefaultHttpClient();
//		HttpPost httppost = new HttpPost("http://114.134.91.91/andriodsmm/result.php");

//		HttpPost httppost = new HttpPost("http://saltlakexx.com/result.php");

		//HttpPost httppost = new HttpPost("http://xnightroom.com/result.php");
//		HttpPost httppost = new HttpPost("http://moonsnowxx.com/result.php");
//		HttpPost httppost = new HttpPost("http:///fallsnownight.com/result.php");
//		HttpPost httppost = new HttpPost("http://pure-romancexx.com/result.php");
//		HttpPost httppost = new HttpPost("http://roseroomlove.com/result.php");
		//HttpPost httppost = new HttpPost("http://rainynightz.com/result.php");
		HttpPost httppost = new HttpPost("http://roseroomlove.com/result.php");
//		HttpPost httppost = new HttpPost("http://lovexxsite.com/result.php");


		HttpResponse response = null;
		HttpEntity entity = null;
		InputStream is = null;
		BufferedReader reader = null;

		if (postData != null) {
			httppost.setEntity(new UrlEncodedFormEntity(postData, "UTF-8"));
			//httppost.setEntity(new UrlEncodedFormEntity(postData));
		}

		if (cookieStore == null) {
			cookieStore = ((DefaultHttpClient) httpclient).getCookieStore();
		} else {
			((DefaultHttpClient) httpclient).setCookieStore(cookieStore);
		}

		response = httpclient.execute(httppost);

		if (readFromServer) {
			entity = response.getEntity();
			is = entity.getContent();
			reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			is.close();
			data = sb.toString();
		}

		return data.trim();
	}

}
