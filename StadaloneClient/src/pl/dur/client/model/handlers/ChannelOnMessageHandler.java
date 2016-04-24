package pl.dur.client.model.handlers;

import java.util.List;
import java.util.Map;

import pl.dur.client.ClientManager;
import pl.dur.client.connection.CommunicationChannel;
import pl.dur.client.connection.CommunicationChannelFactory;
import pl.dur.client.model.Client;
import pl.dur.client.shared.Constants;
import pl.dur.client.shared.MessageTypes;

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
