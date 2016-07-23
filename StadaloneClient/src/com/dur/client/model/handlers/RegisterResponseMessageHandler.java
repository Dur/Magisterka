package com.dur.client.model.handlers;

import java.util.List;
import java.util.Map;

import com.dur.client.ClientManager;
import com.dur.client.connection.CommunicationChannel;
import com.dur.client.connection.CommunicationChannelFactory;
import com.dur.client.controllers.MainViewController;
import com.dur.client.controllers.PrimaryWindowController;
import com.dur.client.model.Client;
import com.dur.client.view.PrimaryView;
import com.dur.shared.Constants;
import com.dur.shared.MessageTypes;

public class RegisterResponseMessageHandler  implements MessageHandler{

	@Override
	public void handleMessage(Map<Object, Object> message) {
		String id = (String) message.get(Constants.SENDER_ID.toString());
		String displayName = (String) message.get(Constants.DISPLAY_NAME.toString());
		List<CommunicationChannel> channels = CommunicationChannelFactory.getCommunicationChannels(message);
		ClientManager.registerClient(id, new Client(id, channels, displayName));
		PrimaryWindowController controller = (PrimaryWindowController) MainViewController.getControllerForView(PrimaryView.class);
		if(null != controller){
			controller.setClientsList(ClientManager.getClientsList());
		}
	}

	@Override
	public String getMessageHeader() {
		return MessageTypes.REGISTER_RESPONSE.toString();
	}
}