package com.dur.client.model.handlers;

import java.util.List;
import java.util.Map;

import com.dur.client.ClientManager;
import com.dur.client.connection.CommunicationChannel;
import com.dur.client.connection.CommunicationChannelFactory;
import com.dur.client.model.Client;
import com.dur.shared.Constants;
import com.dur.shared.MessageTypes;

public class ChannelOnMessageHandler implements MessageHandler {

	@Override
	public String getMessageHeader() {
		return MessageTypes.CHANNEL_ON.toString();
	}

	@Override
	public void handleMessage(Map<Object, Object> message) {
		String clientId = (String) message.get(Constants.SENDER_ID.toString());
		Client client = ClientManager.getClient(clientId);
		if(client != null){
			List<CommunicationChannel> channels = CommunicationChannelFactory.getCommunicationChannels(message);
			client.addCommunicationChannels(channels);
		}
	}

}