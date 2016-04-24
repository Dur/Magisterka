package com.dur.model.handlers;

import java.util.Map;

import org.springframework.web.socket.WebSocketSession;

public interface MessageHandler {
	
	public void handleMessage(Map<Object, Object> message, WebSocketSession sesion) throws Exception;

}