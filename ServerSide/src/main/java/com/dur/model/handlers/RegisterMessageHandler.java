package com.dur.model.handlers;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.WebSocketSession;

import com.dur.controllers.ServerController;
import com.dur.model.ClientSession;
import com.dur.server.shared.Constants;

public class RegisterMessageHandler implements MessageHandler {

	private final Log log = LogFactory.getLog(RegisterMessageHandler.class);
	
	@Override
	public void handleMessage(Map<Object, Object> message, WebSocketSession session) throws Exception {
		String clientId = (String) message.get(Constants.SENDER_ID.toString());
		String displayName = (String) message.get(Constants.DISPLAY_NAME.toString());
		String phoneNumber = (String) message.get(Constants.CLIENT_PHONE.toString());
		String bluetoothId = (String) message.get(Constants.CLIENT_BT_ID.toString());
		String ipAddress = (String) message.get(Constants.CLIENT_IP_ADDRESS.toString());
		String port = (String) message.get(Constants.IP_PORT.toString());
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
