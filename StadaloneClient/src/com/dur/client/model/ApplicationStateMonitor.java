package com.dur.client.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.ClientManager;
import com.dur.client.connection.ConnectionType;
import com.dur.client.controllers.AndroidContextController;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;
import com.dur.shared.MessageTypes;

public class ApplicationStateMonitor implements Runnable{
	
	private final static Log log = LogFactory.getLog(ApplicationStateMonitor.class);
	private Set<ConnectionType> currentState = new HashSet<>();
	private boolean operate = true;
	
	public ApplicationStateMonitor (Set<ConnectionType> types){
		this.currentState = types;
	}
	
	@Override
	public void run() {
		log.info("##### Monitor thread starts");
		while(operate){
			try {
				Thread.sleep(5000);
			} 
			catch (InterruptedException e) {
				log.error("##### Unable to go sleep");
			}
			Set<ConnectionType> now = ApplicationContext.getAvailableChannels();
			List<ConnectionType> toRemove = new LinkedList<>();
			List<ConnectionType> toAdd = new LinkedList<>();
			for(ConnectionType type : now){
				if( ! currentState.contains(type)){
					toAdd.add(type);
					currentState.add(type);
				}
			}
			for(ConnectionType type : currentState){
				if(! now.contains(type)){
					toRemove.add(type);
				}
			}
			currentState.removeAll(toRemove);
			if(toRemove.size() > 0){
				log.info("##### will remove channels");
				removeChannels(toRemove);
			}
			if(toAdd.size() > 0){
				log.info("##### will add channels");
				addChannels(toAdd);
			}
		}
		log.info("##### State monitor is going down");
	}
	
	private void removeChannels(List<ConnectionType> toRemove){
		for(ConnectionType type : toRemove){
			log.info("##### Removeing " + type.toString());
			JSONMessage message = new JSONMessage();
			message.addParam(Constants.SENDER_ID, ApplicationContext.getDeviceID());
			message.addParam(Constants.REQUEST_TYPE, MessageTypes.CHANNEL_OFF.toString());
			message.addParam(Constants.CHANNEL, type.toString());
			for(Client client : ClientManager.getClientsList()){
				client.sendMessage(message);
			}
		}
	}
	
	private void addChannels(List<ConnectionType> toAdd){
		JSONMessage message = new JSONMessage();
		message.addParam(Constants.SENDER_ID, ApplicationContext.getDeviceID());
		message.addParam(Constants.REQUEST_TYPE, MessageTypes.CHANNEL_ON.toString());
		for(ConnectionType type : toAdd){
			if(type == ConnectionType.PHONE){
				log.info("##### Adding PHONE connection type");
				message.addParam(Constants.CLIENT_PHONE, AndroidContextController.getCurrentDevicePhoneNumber());
			}
			if(type == ConnectionType.SOCKET){
				log.info("##### Adding SOCKET connection type");
				String ipAddress = ApplicationContext.getIPAddress();
				message.addParam(Constants.CLIENT_IP_ADDRESS, ipAddress);
				message.addParam(Constants.IP_PORT, ApplicationContext.getPort());
			}
			if(type == ConnectionType.BLUETOOTH){
				log.info("##### Adding BLUETOOTH connection type");
				String btNameString = AndroidContextController.getInstance().getBluetooth().getDeviceBluetoothName();
				message.addParam(Constants.CLIENT_BT_ID, btNameString);
			}
		}
		for(Client client : ClientManager.getClientsList()){
			client.sendMessage(message);
		}
	}
	
	public void exit(){
		operate = false;
	}
	

}
