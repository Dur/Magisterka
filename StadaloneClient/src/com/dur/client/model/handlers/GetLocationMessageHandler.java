package com.dur.client.model.handlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.ClientManager;
import com.dur.client.controllers.PrimaryWindowController;
import com.dur.client.model.ApplicationContext;
import com.dur.client.model.Client;
import com.dur.client.model.JSONMessage;
import com.dur.client.model.SimpleLocation;
import com.dur.shared.Constants;
import com.dur.shared.MessageTypes;

public class GetLocationMessageHandler implements MessageHandler {

	private final PrimaryWindowController controller;

	private final Log log = LogFactory.getLog(GetLocationMessageHandler.class);

	public GetLocationMessageHandler(PrimaryWindowController controller) {
		super();
		this.controller = controller;
	}

	@Override
	public void handleMessage(Map<Object, Object> message) {
		SimpleLocation location = controller.getCurrentLocation();
		if(location != null){
			HashMap<Object, Object> toSend = new HashMap<>();
			String senderID = (String) message.get(Constants.SENDER_ID.toString());
			toSend.put(Constants.REQUEST_TYPE.toString(), MessageTypes.NEW_LOCATION.toString());
			toSend.put(MessageTypes.NEW_LOCATION.toString(), location.getCords());
			toSend.put(Constants.RECIPIENT_ID.toString(), senderID);
			toSend.put(Constants.SENDER_ID.toString(), ApplicationContext.getDeviceID());
			log.error("##### Sending current location: " + JSONMessage.toJson(toSend));
			ClientManager.addToObservators(senderID);
			Client client = ClientManager.getClient(senderID);
			if(null != client){
				client.sendMessage(JSONMessage.toJson(toSend));
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
