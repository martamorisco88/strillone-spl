package org.informaticisenzafrontiere.strillone.http;

import java.util.Map;
import java.util.Set;

public class RequestParameters {

	private String url;
	
	private Map<String, String> parameters;
	
	@SuppressWarnings("unused")
	private RequestParameters() { }
	
	public RequestParameters(String url, Map<String, String> parameters) {
		this.url = url;
		this.parameters = parameters;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("URL: ");
		sb.append(this.url);
		sb.append("\n");
		
		Set<String> keys = parameters.keySet();
		for (String key : keys) {
			sb.append(key);
			sb.append(": ");
			sb.append(parameters.get(key));
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
}
