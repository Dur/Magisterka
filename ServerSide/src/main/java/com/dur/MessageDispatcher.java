package com.dur;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.WebSocketSession;

import com.dur.controllers.ConnectedClientsController;
import com.dur.model.handlers.DafaultForwardHandler;
import com.dur.model.handlers.GetClientsMessageHandler;
import com.dur.model.handlers.GetLocationMessageHandler;
import com.dur.model.handlers.MessageHandler;
import com.dur.model.handlers.NewLocationMessageHandler;
import com.dur.model.handlers.RegisterMessageHandler;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;
import com.dur.shared.MessageTypes;

/**
 * Message dispatcher for all possible messages. 
 * It has access to view controller which allows it to modify UI base on incoming messages.
 * Each message should be processed by dedicated MessageHandler stored in handlers map.
 * @author ddr
 *
 */
public class MessageDispatcher {
	
	private final Log log = LogFactory.getLog(MessageDispatcher.class);
	
	private HashMap<String, MessageHandler> handlers;
	private ConnectedClientsController clientsController;
	
	public MessageDispatcher(ConnectedClientsController clientsController) {
		super();
		this.clientsController = clientsController;
		handlers = generateHandlersMap();
	}

	public void dispatch(String message, WebSocketSession session) throws Exception{
		JSONMessage params = new JSONMessage(message);
		log.info("##### Received message: " + message);
		String requestType = (String) params.get(Constants.REQUEST_TYPE);
		MessageHandler handler = handlers.get(requestType);
		if(null != handler){
			log.info("##### Processing message: " + requestType);
			handler.handleMessage(params, session);
		}
		else{
			log.error("##### Unable to parse message: " + requestType + " Forwarding...");
			handler = handlers.get(MessageTypes.FORWARD.toString());
			handler.handleMessage(params, session);
		}
	}
	
	/**
	 * Creation of handlers map. Each handler should be registered here to allow dispatcher to process particular message.
	 * @return
	 */
	private HashMap<String, MessageHandler> generateHandlersMap(){
		HashMap<String, MessageHandler> newHandlers = new HashMap<String, MessageHandler>();
		newHandlers.put(MessageTypes.NEW_LOCATION.toString(), new NewLocationMessageHandler(clientsController));
		newHandlers.put(MessageTypes.GET_CLIENTS.toString(), new GetClientsMessageHandler(clientsController));
		newHandlers.put(MessageTypes.GET_LOCATION.toString(), new GetLocationMessageHandler(clientsController));
		newHandlers.put(MessageTypes.REGISTER.toString(), new RegisterMessageHandler());
		newHandlers.put(MessageTypes.FORWARD.toString(), new DafaultForwardHandler(clientsController));
		return newHandlers;
	}

}
