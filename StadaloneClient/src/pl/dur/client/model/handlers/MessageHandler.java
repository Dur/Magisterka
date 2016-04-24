package pl.dur.client.model.handlers;

import java.util.Map;

public interface MessageHandler {
	
	public String getMessageHeader();
	
	public void handleMessage(Map<Object, Object> message);

}
