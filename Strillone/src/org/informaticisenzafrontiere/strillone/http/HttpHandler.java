package org.informaticisenzafrontiere.strillone.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import org.informaticisenzafrontiere.strillone.util.Configuration;

import android.os.AsyncTask;
import android.util.Log;

public class HttpHandler extends AsyncTask<RequestParameters, Void, String> {
	
	private final static String TAG = HttpHandler.class.getSimpleName();
	
	private IResponseListener responseListener;
	
	public HttpHandler(IResponseListener responseListener) {
		this.responseListener = responseListener;
	}
	
	@Override
	protected String doInBackground(RequestParameters... aParameters) {
		RequestParameters requestParameters = aParameters[0];
		String response = "";
		
		if (Configuration.DEBUGGABLE) Log.d(TAG, "Parametri richiesta:");
		if (Configuration.DEBUGGABLE) Log.d(TAG, requestParameters.toString());
		
		// Validates the request parameters.
		Map<String, String> parameters = requestParameters.getParameters();
		Set<String> parameterKeys = parameters.keySet();
		Iterator<String> iParameterKeys = parameterKeys.iterator();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		while (iParameterKeys.hasNext()) {
			String key = iParameterKeys.next();
			nameValuePairs.add(new BasicNameValuePair(key, parameters.get(key)));
		}
		
		try {
			String url = requestParameters.getUrl();
			Pattern pattern = Pattern.compile("(https?):\\/\\/([A-Za-z0-9\\.]+)(:([0-9]+))?\\/.*");
			Matcher matcher = pattern.matcher(url);
			if (matcher.matches()) {
				String port = matcher.group(4);
				
				HttpPost httpPost = new HttpPost(url);
			HttpGet httpGet =new HttpGet(url);
			
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				// Crea la stringa dello user agent.
//				StringBuilder sbUserAgent = new StringBuilder();
//				sbUserAgent.append(Configuration.APP_NAME_PUBLIC);
//				sbUserAgent.append(" ");
//				sbUserAgent.append(Configuration.APP_VERSION);
//				sbUserAgent.append(" / ");
//				sbUserAgent.append(System.getProperty("os.name"));
//				sbUserAgent.append(" ");
//				sbUserAgent.append(System.getProperty("os.version"));
				
				//Set the parameters of the request.
				HttpParams params = new BasicHttpParams();
				HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(params, "UTF-8");
				// HttpProtocolParams.setUserAgent(params, sbUserAgent.toString());
				HttpConnectionParams.setConnectionTimeout(params, 60000);
				HttpConnectionParams.setSoTimeout(params, 120000);
				params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
				
				// Registry patterns for http and https.
				SchemeRegistry registry = new SchemeRegistry();
				
				if (port != null) {
					registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), Integer.parseInt(port)));
				} else {
					registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
				}
					
				

				ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
				
				// Run the HTTP request.
				HttpClient httpClient = new DefaultHttpClient(manager, params);
				//HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				// Read the answer.
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					response = EntityUtils.toString(httpResponse.getEntity());
					if (Configuration.DEBUGGABLE) Log.d("HttpHandler", "response: " + response);
				} else {
					if (Configuration.DEBUGGABLE) Log.d("HttpHandler", "status code: " + httpResponse.getStatusLine().getStatusCode());
				}
			}
		} catch (UnsupportedEncodingException e) {
			if (Configuration.DEBUGGABLE) Log.d("HttpHandler", "UnsupportedEncodingException", e);
		} catch (ClientProtocolException e) {
			if (Configuration.DEBUGGABLE) Log.d("HttpHandler", "ClientProtocolException", e);
		} catch (IOException e) {
			if (Configuration.DEBUGGABLE) Log.d("HttpHandler", "IOException", e);
		}
		
		return response;
	}
	
	protected void onPostExecute(String response) {
		responseListener.onResponseReceived(response);
	}

}