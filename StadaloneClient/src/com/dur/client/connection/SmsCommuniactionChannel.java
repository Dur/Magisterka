package com.dur.client.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.controllers.AndroidContextController;
import com.dur.shared.JSONMessage;


public class SmsCommuniactionChannel extends CommunicationChannel{
	
	private final Log log = LogFactory.getLog(SmsCommuniactionChannel.class);
	private final String phoneNumber;
	
	public SmsCommuniactionChannel(String phoneNumber) {
		super();
		this.phoneNumber = phoneNumber;
	}
	

	@Override
	public boolean sendMessage(JSONMessage message) {
		if(AndroidContextController.isMobileDevice()){
			AndroidContextController.getInstance().sendSms(message.toString(), phoneNumber);
			log.info("##### Sending text message to " + phoneNumber);
			return true;
		}
		return false;
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
		// TODO Auto-generated method stub
	}


	@Override
	protected ConnectionType getType() {
		return ConnectionType.PHONE;
	}


	@Override
	protected int getDefaultPriority() {
		return 10;
	}

}
