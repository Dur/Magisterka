package com.dur.client.model.handlers;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.ClientManager;
import com.dur.client.connection.CommunicationChannel;
import com.dur.client.connection.CommunicationChannelFactory;
import com.dur.client.controllers.PrimaryWindowController;
import com.dur.client.model.Client;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;
import com.dur.shared.MessageTypes;

public class ClientsListMessageHandler implements MessageHandler {

	private final Log log = LogFactory.getLog(ClientsListMessageHandler.class);

	private final PrimaryWindowController controller;

	public ClientsListMessageHandler(PrimaryWindowController controller) {
		super();
		this.controller = controller;
	}

	@Override
	public void handleMessage(JSONMessage message) {
		@SuppressWarnings("unchecked")
		List<Map<Object, Object>> clients = (List<Map<Object, Object>>) message.get(MessageTypes.CLIENTS_LIST);
		if(null != clients){
			for(Map<Object, Object> singleClient : clients){
				String id = (String) singleClient.get(Constants.SENDER_ID.toString());
				String displayName = (String) singleClient.get(Constants.DISPLAY_NAME.toString());
				List<CommunicationChannel> channels = CommunicationChannelFactory.getCommunicationChannels(new JSONMessage(singleClient));
				log.info("##### Registering " + channels.size() + " communication channels");
				ClientManager.registerClient(id, new Client(id, channels, displayName));
			}
		}
		log.info("##### received clients: " + clients);
		controller.setClientsList(ClientManager.getClientsList());
	}

	@Override
	public String getMessageHeader() {
		return MessageTypes.CLIENTS_LIST.toString();
	}

}
