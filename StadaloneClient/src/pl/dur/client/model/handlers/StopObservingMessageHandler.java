package pl.dur.client.model.handlers;

import java.util.Map;

import pl.dur.client.ClientManager;
import pl.dur.client.shared.Constants;
import pl.dur.client.shared.MessageTypes;

public class StopObservingMessageHandler implements MessageHandler {

	@Override
	public String getMessageHeader() {
		return MessageTypes.STOP_OBSERV.toString();
	}

	@Override
	public void handleMessage(Map<Object, Object> message) {
		String clientId = (String) message.get(Constants.SENDER_ID.toString());
		ClientManager.removeFromObservators(clientId);
	}

}
