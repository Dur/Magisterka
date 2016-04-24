package pl.dur.client.model.handlers;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.ClientManager;
import pl.dur.client.connection.CommunicationChannel;
import pl.dur.client.connection.CommunicationChannelFactory;
import pl.dur.client.controllers.PrimaryWindowController;
import pl.dur.client.model.Client;
import pl.dur.client.shared.Constants;
import pl.dur.client.shared.MessageTypes;

public class ClientsListMessageHandler implements MessageHandler {

	private final Log log = LogFactory.getLog(ClientsListMessageHandler.class);

	private final PrimaryWindowController controller;

	public ClientsListMessageHandler(PrimaryWindowController controller) {
		super();
		this.controller = controller;
	}

	@Override
	public void handleMessage(Map<Object, Object> message) {
		@SuppressWarnings("unchecked")
		List<Map<Object, Object>> clients = (List<Map<Object, Object>>) message.get(MessageTypes.CLIENTS_LIST.toString());
		if(null != clients){
			for(Map<Object, Object> singleClient : clients){
				String id = (String) singleClient.get(Constants.SENDER_ID.toString());
				String displayName = (String) singleClient.get(Constants.DISPLAY_NAME.toString());
				List<CommunicationChannel> channels = CommunicationChannelFactory.getCommunicationChannels(singleClient);
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
