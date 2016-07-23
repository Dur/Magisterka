package com.dur.client.connection;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java_websocket.drafts.Draft_17;

public class WebSocketCommunicationChannel extends CommunicationChannel implements Runnable{
	
	private final Log log = LogFactory.getLog(WebSocketCommunicationChannel.class);
	private final int GOING_DOWN_REASON = 1001;
	private final String WS = "ws://";
	private final String HOST_URL;
	private final String APP_URL = "/ServerSide/websocket/websocket";
	private WebSocket client;
	private boolean stop = false;
	private List<String> initialMessages;
	
	public WebSocketCommunicationChannel(String URL, List<String> initialMessages) {
		super();
		this.initialMessages = initialMessages;
		this.HOST_URL = URL;
	}

	private void connect() {
		try {
			client = new WebSocket(new URI(WS + HOST_URL + APP_URL), new Draft_17(), this, initialMessages);
			client.connect();
		} 
		catch (URISyntaxException e1) {
			log.error(e1);
		}
	}
	
	public boolean sendMessage(String message){
		if(client.getConnection().isOpen()){
			log.info("##### Websocket connection is open - sending message");
			client.send(message);
			return true;
		}
		log.info("##### Websocket connection is closed. Unable to send message");
		return false;
		
	}
	
	public boolean isOpen(){
		return client.getConnection().isOpen();
	}
	
	public void exit(){
		closeChannel();
	}
	
	@Override
	public void run() {
		this.connect();
		try {
			while(! stop){
				log.info("##### Waiting");
				synchronized(client){
					client.wait();
				}
				log.info("#####After waiting");
				if(! stop){
					Thread.sleep(5000);
					if(! stop){
						log.error("##### Reconnecting to server");
						this.connect();
					}
				}
			}
		} 
		catch (InterruptedException e) {
			log.error("##### Unable to wait");
		}
		log.info("##### Websocket thread is going down");
		return;
	}

	@Override
	protected void beforeDispatch(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void afterDispatch(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void closeChannel() {
		this.stop = true;
		log.info("##### Closing websocket");
		client.getConnection().close(GOING_DOWN_REASON);
		
	}

	@Override
	protected ConnectionType getType() {
		return ConnectionType.WEBSOCKET;
	}

	@Override
	protected int getDefaultPriority() {
		return 2;
	}
}
