package pl.dur.client.model.handlers;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.ClientManager;
import pl.dur.client.model.Client;
import pl.dur.client.shared.Constants;
import pl.dur.client.shared.MessageTypes;

public class ChannelOffMessageHandler implements MessageHandler {

	private final static Log log = LogFactory.getLog(ChannelOffMessageHandler.class);
	
	@Override
	public String getMessageHeader() {
		return MessageTypes.CHANNEL_OFF.toString();
	}

	@Override
	public void handleMessage(Map<Object, Object> message) {
		String clientId = (String) message.get(Constants.SENDER_ID.toString());
		Client client = ClientManager.getClient(clientId);
		if(client != null){
			String channel = (String) message.get(Constants.CHANNEL.toString());
			log.info("##### Removing channel " + channel);
			client.removeCommunicationChannel(channel);
		}
	}

}
