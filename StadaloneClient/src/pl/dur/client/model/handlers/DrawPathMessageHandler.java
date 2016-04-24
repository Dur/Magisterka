package pl.dur.client.model.handlers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.dur.client.controllers.MainViewController;
import pl.dur.client.controllers.PrimaryWindowController;
import pl.dur.client.model.Cords;
import pl.dur.client.shared.Constants;
import pl.dur.client.shared.MessageTypes;
import pl.dur.client.view.PrimaryView;

public class DrawPathMessageHandler implements MessageHandler{

	@Override
	public String getMessageHeader() {
		return MessageTypes.DRAW_PATH.toString();
	}

	@Override
	public void handleMessage(Map<Object, Object> message) {
		@SuppressWarnings("unchecked")
		List<Map<Object, Object>> cords = (List<Map<Object, Object>>) message.get(Constants.PATH.toString());
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
