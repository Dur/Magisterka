package pl.dur.client.model.handlers;

import java.util.List;
import java.util.Map;

import pl.dur.client.ClientManager;
import pl.dur.client.connection.CommunicationChannel;
import pl.dur.client.connection.CommunicationChannelFactory;
import pl.dur.client.controllers.MainViewController;
import pl.dur.client.controllers.PrimaryWindowController;
import pl.dur.client.model.Client;
import pl.dur.client.shared.Constants;
import pl.dur.client.shared.MessageTypes;
import pl.dur.client.view.PrimaryView;

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