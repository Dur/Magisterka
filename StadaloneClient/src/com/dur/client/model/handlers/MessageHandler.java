package com.dur.client.model.handlers;

import com.dur.shared.JSONMessage;

public interface MessageHandler {
	
	public String getMessageHeader();
	
	public void handleMessage(JSONMessage message);

}
