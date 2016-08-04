package com.dur.client.model.handlers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.ClientManager;
import com.dur.client.connection.CommunicationChannel;
import com.dur.client.connection.CommunicationChannelFactory;
import com.dur.client.controllers.MainViewController;
import com.dur.client.controllers.PrimaryWindowController;
import com.dur.client.model.ApplicationContext;
import com.dur.client.model.Client;
import com.dur.client.view.PrimaryView;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;
import com.dur.shared.MessageTypes;

public class RegisterMessageHandler implements MessageHandler{
	private final Log log = LogFactory.getLog(RegisterMessageHandler.class);
	
	@Override
	public void handleMessage(JSONMessage message) {
		String id = (String) message.get(Constants.SENDER_ID);
		String displayName = (String) message.get(Constants.DISPLAY_NAME);
		List<CommunicationChannel> channels = CommunicationChannelFactory.getCommunicationChannels(message);
		ClientManager.registerClient(id, new Client(id, channels, displayName));
		
		JSONMessage businessCardMap = ApplicationContext.getBusinessCard();
		businessCardMap.addParam(Constants.REQUEST_TYPE, MessageTypes.REGISTER_RESPONSE.toString());
		ClientManager.getClient(id).sendMessage(businessCardMap);
		PrimaryWindowController controller = (PrimaryWindowController) MainViewController.getControllerForView(PrimaryView.class);
		if(null != controller){
			log.info("##### Reloading clients list");
			controller.setClientsList(ClientManager.getClientsList());
		}
	}

	@Override
	public String getMessageHeader() {
		return MessageTypes.REGISTER.toString();
	}

}
