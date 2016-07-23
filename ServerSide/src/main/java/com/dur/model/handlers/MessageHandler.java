package com.dur.model.handlers;

import org.springframework.web.socket.WebSocketSession;

import com.dur.shared.JSONMessage;

public interface MessageHandler {
	
	public void handleMessage(JSONMessage message, WebSocketSession sesion) throws Exception;

}