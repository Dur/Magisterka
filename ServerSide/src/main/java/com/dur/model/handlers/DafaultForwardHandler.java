package com.dur.model.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dur.controllers.ConnectedClientsController;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;

public class DafaultForwardHandler implements MessageHandler{

	private final Log log = LogFactory.getLog(DafaultForwardHandler.class);
	private ConnectedClientsController clientsController;
	
	public DafaultForwardHandler(ConnectedClientsController clientsController) {
		super();
		this.clientsController = clientsController;
	}
	
	@Override
	public void handleMessage(JSONMessage message, WebSocketSession sesion) throws Exception {
		String receiver = (String) message.get(Constants.RECIPIENT_ID);
		TextMessage toSend = new TextMessage(message.toString());
		log.error("##### Getting session for: " + receiver);
		WebSocketSession session = clientsController.getSessionFor(receiver);
		if(null != receiver && null != session){
			log.error("##### Forwarding message to: " + receiver);
			session.sendMessage(toSend);
		}
		else{
			log.error("##### No session for: " + receiver);
		}
		
	}

}
