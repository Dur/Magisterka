package com.dur.model.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dur.controllers.ConnectedClientsController;
import com.dur.model.ClientSession;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;
import com.dur.shared.MessageTypes;

public class GetClientsMessageHandler implements MessageHandler {

	private final Log log = LogFactory.getLog(GetClientsMessageHandler.class);
	private ConnectedClientsController clientsController;

	public GetClientsMessageHandler(ConnectedClientsController clientsController) {
		this.clientsController = clientsController;
	}

	@Override
	public void handleMessage(JSONMessage message, WebSocketSession session) throws IOException {
		String receiver = (String) message.get(Constants.SENDER_ID);
		log.info("Recived session ID " + receiver);
		List<ClientSession> clients = clientsController.getAllClientExcept(receiver);
		List<HashMap<Object, Object>> clientsList = new LinkedList<>();
		for(ClientSession singleClient : clients){
			clientsList.add(singleClient.getState());
		}
		JSONMessage toSend = new JSONMessage();
		toSend.addParam(Constants.REQUEST_TYPE, MessageTypes.CLIENTS_LIST.toString());
		if( ! clients.isEmpty()){
			toSend.addParam(MessageTypes.CLIENTS_LIST, clientsList);
		}
		log.info("sending " + toSend.toString());
		TextMessage returnMessage = new TextMessage(toSend.toString());
		WebSocketSession sessionToSend = clientsController.getSessionFor(receiver);
		if(null != receiver && null != session){
			log.error("##### Sending message GetLocation to: " + receiver);
			sessionToSend.sendMessage(returnMessage);
		}
		else{
			log.error("##### No session for: " + receiver);
		}
	}
}
