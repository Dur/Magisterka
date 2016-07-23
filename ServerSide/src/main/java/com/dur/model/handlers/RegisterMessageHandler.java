package com.dur.model.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.WebSocketSession;

import com.dur.controllers.ServerController;
import com.dur.model.ClientSession;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;

public class RegisterMessageHandler implements MessageHandler {

	private final Log log = LogFactory.getLog(RegisterMessageHandler.class);
	
	@Override
	public void handleMessage(JSONMessage message, WebSocketSession session) throws Exception {
		String clientId = (String) message.get(Constants.SENDER_ID);
		String displayName = (String) message.get(Constants.DISPLAY_NAME);
		String phoneNumber = (String) message.get(Constants.CLIENT_PHONE);
		String bluetoothId = (String) message.get(Constants.CLIENT_BT_ID);
		String ipAddress = (String) message.get(Constants.CLIENT_IP_ADDRESS);
		String port = (String) message.get(Constants.IP_PORT);
		log.info("##### Registered client: " + clientId + " phoneNumber: " + phoneNumber + " BT_ID: " + bluetoothId + " IP: " + ipAddress + ":" + port);
		if(ServerController.getClientsController().isClientRegistered(clientId)){
			log.info("##### Client already registered, overwritting properties");
			ServerController.getClientsController().getClient(clientId).rewriteProperties(phoneNumber, ipAddress, bluetoothId, session, port);
		}
		else{
			log.info("##### New client. Registering");
			ClientSession clientSession = new ClientSession(clientId, phoneNumber, ipAddress, bluetoothId, session, displayName, port);
			ServerController.getClientsController().addSession(clientId, clientSession);
			log.info("##### Client registerd");
		}
		log.info("##### Registered client: " + clientId + " phoneNumber: " + phoneNumber + " BT_ID: " + bluetoothId + " IP: " + ipAddress + ":" + port + " displayName: " + displayName);
	}
}
