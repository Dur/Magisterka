package com.dur;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.dur.controllers.ConnectedClientsController;
import com.dur.controllers.ServerController;
import com.dur.model.ClientSession;


public class WebsocketEndPoint extends TextWebSocketHandler {

	// ws://192.168.1.100:8080/ServerSide/websocket/websocket

	private final Log log = LogFactory.getLog(WebsocketEndPoint.class);
	private ConnectedClientsController clientsControler = ServerController.getClientsController();
	private MessageDispatcher dispatcher = new MessageDispatcher(clientsControler);

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		super.handleTextMessage(session, message);
		log.info("##### New message from " + session.getId());
		dispatcher.dispatch(message.getPayload(), session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		if(CloseStatus.GOING_AWAY.toString().equals(status.toString())){
			log.info("##### Closing with reason " + CloseStatus.GOING_AWAY.toString());
			log.info("##### Removing session " + session.getId());
			ClientSession closingSession = clientsControler.getClientBySessionId(session.getId());
			if(null != closingSession){
				clientsControler.removeClientBySessionId(session.getId());
			}
		}
	}
	
}