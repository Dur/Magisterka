package pl.dur.client.model.handlers;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.ClientManager;
import pl.dur.client.connection.CommunicationChannel;
import pl.dur.client.connection.CommunicationChannelFactory;
import pl.dur.client.controllers.MainViewController;
import pl.dur.client.controllers.PrimaryWindowController;
import pl.dur.client.model.ApplicationContext;
import pl.dur.client.model.Client;
import pl.dur.client.model.JSONMessage;
import pl.dur.client.shared.Constants;
import pl.dur.client.shared.MessageTypes;
import pl.dur.client.view.PrimaryView;

public class RegisterMessageHandler implements MessageHandler{
	private final Log log = LogFactory.getLog(RegisterMessageHandler.class);
	
	@Override
	public void handleMessage(Map<Object, Object> message) {
		String id = (String) message.get(Constants.SENDER_ID.toString());
		String displayName = (String) message.get(Constants.DISPLAY_NAME.toString());
		List<CommunicationChannel> channels = CommunicationChannelFactory.getCommunicationChannels(message);
		ClientManager.registerClient(id, new Client(id, channels, displayName));
		
		Map<Object, Object> businessCardMap = ApplicationContext.getBusinessCard();
		businessCardMap.put(Constants.REQUEST_TYPE.toString(), MessageTypes.REGISTER_RESPONSE.toString());
		ClientManager.getClient(id).sendMessage(JSONMessage.toJson(businessCardMap));
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
