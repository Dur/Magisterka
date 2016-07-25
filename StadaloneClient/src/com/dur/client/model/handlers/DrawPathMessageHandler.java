package com.dur.client.model.handlers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dur.client.controllers.MainViewController;
import com.dur.client.controllers.PrimaryWindowController;
import com.dur.client.model.Cords;
import com.dur.client.view.PrimaryView;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;
import com.dur.shared.MessageTypes;

public class DrawPathMessageHandler implements MessageHandler{

	@Override
	public String getMessageHeader() {
		return MessageTypes.DRAW_PATH.toString();
	}

	@Override
	public void handleMessage(JSONMessage message) {
		@SuppressWarnings("unchecked")
		List<Map<Object, Object>> cords = (List<Map<Object, Object>>) message.get(Constants.PATH);
		List<Cords> path = new LinkedList<>();
		for(Map<Object, Object> single : cords){
			String lat = (String) single.get(Constants.latitude.toString());
			String lon = (String) single.get(Constants.longitude.toString());
			path.add(new Cords( Double.parseDouble(lat), Double.parseDouble(lon)));
		}
		PrimaryWindowController controller = (PrimaryWindowController) MainViewController.getControllerForView(PrimaryView.class);
		if(null != cords && null != controller){
			controller.drawOnMyMap(path);
		}
		
	}

}
