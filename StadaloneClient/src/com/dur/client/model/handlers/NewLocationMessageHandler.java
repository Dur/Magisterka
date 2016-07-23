package com.dur.client.model.handlers;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.controllers.PrimaryWindowController;
import com.dur.client.model.Cords;
import com.dur.client.model.SimpleLocation;
import com.dur.shared.Constants;
import com.dur.shared.MessageTypes;

public class NewLocationMessageHandler implements MessageHandler {

	private final PrimaryWindowController controller;

	private final Log log = LogFactory.getLog(NewLocationMessageHandler.class);

	public NewLocationMessageHandler(PrimaryWindowController controller) {
		super();
		this.controller = controller;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handleMessage(Map<Object, Object> message) {
		String clientId = (String) message.get(Constants.SENDER_ID.toString());
		if(controller.getCurrentObservedClient() != null && clientId.equals(controller.getCurrentObservedClient().getId())){
			log.info("##### I'm observing client " + clientId);
			String latString = (String) ((Map) message.get(MessageTypes.NEW_LOCATION.toString())).get(Constants.latitude.toString());
			String lanString = (String) ((Map) message.get(MessageTypes.NEW_LOCATION.toString())).get(Constants.longitude.toString());
			double lat = Double.parseDouble(latString); 
			double lan = Double.parseDouble(lanString);
			Cords center = new Cords(lat, lan);
			controller.reloadUserMap(new SimpleLocation(center, "new_location"));
		}
		else{
			log.info("##### I'm not observing client " + clientId);
		}
	}

	@Override
	public String getMessageHeader() {
		return MessageTypes.NEW_LOCATION.toString();
	}
}
