package com.dur.client.controllers;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.model.handlers.ChannelOffMessageHandler;
import com.dur.client.model.handlers.ChannelOnMessageHandler;
import com.dur.client.model.handlers.ClientsListMessageHandler;
import com.dur.client.model.handlers.DrawPathMessageHandler;
import com.dur.client.model.handlers.GetLocationMessageHandler;
import com.dur.client.model.handlers.MessageHandler;
import com.dur.client.model.handlers.NewLocationMessageHandler;
import com.dur.client.model.handlers.RegisterMessageHandler;
import com.dur.client.model.handlers.RegisterResponseMessageHandler;
import com.dur.client.model.handlers.StopObservingMessageHandler;
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
public class MessageDispatcher implements Runnable{
	
	private final static Log log = LogFactory.getLog(MessageDispatcher.class);
	
	private PrimaryWindowController viewController;
	private HashMap<String, MessageHandler> handlers;
	private static MessageDispatcher instance;
	private BlockingQueue<String> messageQueue;
	private static Thread thread;
	
	private MessageDispatcher(PrimaryWindowController viewController) {
		super();
		this.viewController = viewController;
		handlers = generateHandlersMap();
		messageQueue = new LinkedBlockingQueue<String>();
	}
	
	public static MessageDispatcher getInstance(PrimaryWindowController viewController){
		if(null == instance){
			instance = new MessageDispatcher(viewController);
			thread = new Thread(instance);
			thread.start();
			
		}
		return instance;
	}
	
	public static void dispatch(String message){
		try {
			if(thread.isAlive()){
				instance.messageQueue.put(message);
			}
			else{
				log.error("##### Dispatcher object not yet active");
			}
		} 
		catch (InterruptedException e) {
			log.error("##### Unable to put message into queue " + e.getMessage());
		}
	}

	private void handleMessage(String message){
		JSONMessage params = new JSONMessage(message);
		String requestType = (String) params.get(Constants.REQUEST_TYPE);
		MessageHandler handler = handlers.get(requestType);
		if(null != handler){
			log.info("##### Processing message: " + requestType);
			handler.handleMessage(params);
		}
		else{
			log.error("##### Unable to parse message: " + requestType);
		}
	}
	
	/**
	 * Creation of handlers map. Each handler should be registered here to allow dispatcher to process particular message.
	 * @return
	 */
	private HashMap<String, MessageHandler> generateHandlersMap(){
		HashMap<String, MessageHandler> newHandlers = new HashMap<>();
		newHandlers.put(MessageTypes.NEW_LOCATION.toString(), new NewLocationMessageHandler(viewController));
		newHandlers.put(MessageTypes.CLIENTS_LIST.toString(), new ClientsListMessageHandler(viewController));
		newHandlers.put(MessageTypes.GET_LOCATION.toString(), new GetLocationMessageHandler(viewController));
		newHandlers.put(MessageTypes.REGISTER.toString(), new RegisterMessageHandler());
		newHandlers.put(MessageTypes.REGISTER_RESPONSE.toString(), new RegisterResponseMessageHandler());
		newHandlers.put(MessageTypes.CHANNEL_OFF.toString(), new ChannelOffMessageHandler());
		newHandlers.put(MessageTypes.CHANNEL_ON.toString(), new ChannelOnMessageHandler());
		newHandlers.put(MessageTypes.STOP_OBSERV.toString(), new StopObservingMessageHandler());
		newHandlers.put(MessageTypes.DRAW_PATH.toString(), new DrawPathMessageHandler());
		return newHandlers;
	}

	@Override
	public void run() {
		log.info("##### Dispatcher thread started");
		String message = null;
		try {
			message = messageQueue.take();
		} catch (InterruptedException e) {
			log.error("Error during message retrieval " + e.getMessage());
		}
		while( ! message.equals(Constants.POISON_PILL.toString())){
			try {
				handleMessage(message);
				message = messageQueue.take();
			} 
			catch (InterruptedException e) {
				log.error("Error during message retrieval " + e.getMessage());
			}
		}
		log.info("##### Dispatcher thread exits");
		return;
	}

}
