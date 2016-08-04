package com.dur.client.model.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.ClientManager;
import com.dur.client.controllers.PrimaryWindowController;
import com.dur.client.model.ApplicationContext;
import com.dur.client.model.Client;
import com.dur.client.model.SimpleLocation;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;
import com.dur.shared.MessageTypes;

public class GetLocationMessageHandler implements MessageHandler {

	private final PrimaryWindowController controller;

	private final Log log = LogFactory.getLog(GetLocationMessageHandler.class);

	public GetLocationMessageHandler(PrimaryWindowController controller) {
		super();
		this.controller = controller;
	}

	@Override
	public void handleMessage(JSONMessage message) {
		SimpleLocation location = controller.getCurrentLocation();
		if(location != null){
			JSONMessage toSend = new JSONMessage();
			String senderID = (String) message.get(Constants.SENDER_ID);
			toSend.addParam(Constants.REQUEST_TYPE, MessageTypes.NEW_LOCATION.toString());
			toSend.addParam(MessageTypes.NEW_LOCATION, location.getCords());
			toSend.addParam(Constants.RECIPIENT_ID, senderID);
			toSend.addParam(Constants.SENDER_ID, ApplicationContext.getDeviceID());
			log.error("##### Sending current location: " + toSend.toString());
			ClientManager.addToObservators(senderID);
			Client client = ClientManager.getClient(senderID);
			if(null != client){
				client.sendMessage(toSend);
			}
			else{
				log.error("##### No such client " + senderID);
			}
		}
		else{
			log.info("Current location is null");
		}
	}

	@Override
	public String getMessageHeader() {
		return MessageTypes.GET_LOCATION.toString();
	}
}
