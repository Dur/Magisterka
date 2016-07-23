package com.dur.client.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

public class RestConnectionAPI {
	
	private final Log log = LogFactory.getLog(RestConnectionAPI.class);
	private String HOST_URL = "127.0.0.1:8080/ServerSide";
	private String HTTP = "http://";
	
	public void connect() {
		HttpClient httpClient = new DefaultHttpClient();
		BasicHttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(HTTP + HOST_URL + "/http/rest/hello.json");
		httpGet.setHeader(new BasicHeader("Accept", "application/json"));
		try {
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			String text = new String(EntityUtils.toString(entity));
			log.info("##### Rest response: " + text);

		} catch (Exception e) {
			log.error("Unable to connect to " + HTTP + HOST_URL + "/http/rest/hello.json");
		}
	}

}
