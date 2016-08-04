package com.dur.client.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.controllers.MessageDispatcher;
import com.dur.shared.JSONMessage;

public abstract class CommunicationChannel implements Comparable<CommunicationChannel> {
	
	private final Log log = LogFactory.getLog(CommunicationChannel.class);
	public abstract boolean sendMessage(JSONMessage message);
	private int priority;
	
	public CommunicationChannel(){
		this.priority = getDefaultPriority();
	}
	
	protected final void onMessageReceive(String message){
		beforeDispatch(message);
		log.info("Passing message to dispatcher");
		MessageDispatcher.dispatch(message);
		log.info("Message passed to dispatcher");
		afterDispatch(message);
	}
	
	abstract protected void beforeDispatch(String message);
	
	abstract protected void afterDispatch(String message);
	
	abstract protected void closeChannel();
	
	abstract protected ConnectionType getType();
	
	abstract protected int getDefaultPriority();
	
	@Override
	public int compareTo(CommunicationChannel second) {
	        return this.priority - second.priority;
	}
	
	public void setPriority(int priority){
		this.priority = priority;
	}

}
