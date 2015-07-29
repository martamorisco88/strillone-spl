package org.informaticisenzafrontiere.strillone;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.util.Log;

import org.informaticisenzafrontiere.strillone.http.GiornaleRequestHandler;
import org.informaticisenzafrontiere.strillone.http.RequestHandler;
import org.informaticisenzafrontiere.strillone.http.TestateRequestHandler;
import org.informaticisenzafrontiere.strillone.util.App;
import org.informaticisenzafrontiere.strillone.util.Configuration;
import org.informaticisenzafrontiere.strillone.xml.Giornale;
import org.informaticisenzafrontiere.strillone.xml.Testate;

public class MainPresenter {
	
	private final static String TAG = MainPresenter.class.getSimpleName();
	
	private IMainActivity mainActivity;
	private Map<String, Giornale> giornali;
	
	public MainPresenter(IMainActivity mainActivity) {
		this.mainActivity = mainActivity;
		this.giornali = new HashMap<String, Giornale>();
	}
	
	public void downloadHeaders() {
		RequestHandler requestHandler = new TestateRequestHandler(this);
		requestHandler.handleRequest();
	}
	
	public void downloadGiornale() {
		//String urlTestata = this.mainActivity.getURLTestata();
		
		String urlTestata = this.mainActivity.getResourceTestata();//Morisco
		
		// Trova il nome del file.
		Pattern pattern = Pattern.compile("([^\\/]+)$");
		Matcher matcher = pattern.matcher(urlTestata);
		
		if (!matcher.find()) {
			this.mainActivity.notifyCommunicationError(App.getInstance().getString(R.string.connecting_error_reading_newspaper));
		} else {
			String filename = matcher.group(1);
			if (Configuration.DEBUGGABLE) Log.d(TAG, "filename" + filename);
			
			Giornale giornale;
			if ((giornale = this.giornali.get(filename)) == null) {
				RequestHandler requesthandler = new GiornaleRequestHandler(this, urlTestata, filename);
				requesthandler.handleRequest();
			} else {
				this.mainActivity.notifyGiornaleReceived(giornale);
			}
		}
		
	}
	
	public void notifyErrorDowloadingHeaders(String message) {
		this.mainActivity.notifyErrorDowloadingHeaders(message);
	}
	
	public void notifyCommunicationError(String message) {
		this.mainActivity.notifyCommunicationError(message);
	}
	
	public void notifyHeadersReceived(Testate testate) {
		this.mainActivity.notifyHeadersReceived(testate);
	}
	
	public void notifyGiornaleReceived(String filename, Giornale giornale) {
		this.giornali.put(filename, giornale);
		this.mainActivity.notifyGiornaleReceived(giornale);
	}
	
	public void switchBetaState() {
		Configuration.BETA = !Configuration.BETA;
		downloadHeaders();
	}
	
	@SuppressLint("DefaultLocale")
	public LinkedList<String> splitString(String s, int interval) {
		LinkedList<String> matchList = new LinkedList<String>();
		Pattern regex = Pattern.compile(String.format(".{1,%d}(?:\\s|$)", interval), Pattern.DOTALL);
		Matcher regexMatcher = regex.matcher(s);
		while (regexMatcher.find()) {
		    matchList.add(regexMatcher.group());
		}
		
		return matchList;
	}

}
