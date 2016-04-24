package pl.dur.client.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.controllers.MessageDispatcher;

public abstract class Receiver {
	
	private final Log log = LogFactory.getLog(Receiver.class);
	
	protected final void onMessageReceive(String message){
		beforeDispatch(message);
		log.info("Passing message to dispatcher");
		MessageDispatcher.dispatch(message);
		log.info("Message passed to dispatcher");
		afterDispatch(message);
	}
	
	abstract protected void beforeDispatch(String message);
	
	abstract protected void afterDispatch(String message);
	
	abstract public void closeChannel();
}
