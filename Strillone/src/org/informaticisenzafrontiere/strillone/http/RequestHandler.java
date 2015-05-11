package org.informaticisenzafrontiere.strillone.http;

import java.util.Map;

import org.informaticisenzafrontiere.strillone.xml.Testate;

public abstract class RequestHandler implements IResponseListener {
	
	public void handleRequest() {
		Map<String, String> parameters = getParameters();
		RequestParameters requestParameters = new RequestParameters(getURL(), parameters);
		HttpHandler httpHandler = new HttpHandler(this);
		httpHandler.execute(requestParameters);
	}
	
	
	protected abstract String getURL();
	
	protected abstract Map<String, String> getParameters();
		
}
