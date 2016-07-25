package com.dur.client.model.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.ClientManager;
import com.dur.client.model.Client;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;
import com.dur.shared.MessageTypes;

public class ChannelOffMessageHandler implements MessageHandler {

	private final static Log log = LogFactory.getLog(ChannelOffMessageHandler.class);
	
	@Override
	public String getMessageHeader() {
		return MessageTypes.CHANNEL_OFF.toString();
	}

	@Override
	public void handleMessage(JSONMessage message) {
		String clientId = (String) message.get(Constants.SENDER_ID);
		Client client = ClientManager.getClient(clientId);
		if(client != null){
			String channel = (String) message.get(Constants.CHANNEL);
			log.info("##### Removing channel " + channel);
			client.removeCommunicationChannel(channel);
		}
	}

}
