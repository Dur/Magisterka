package pl.dur.client.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.ClientManager;
import pl.dur.client.controllers.AndroidContextController;
import pl.dur.client.shared.ConnectionType;
import pl.dur.client.shared.Constants;
import pl.dur.client.shared.MessageTypes;

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
			HashMap<Object, Object> data = new HashMap<>();
			data.put(Constants.SENDER_ID.toString(), ApplicationContext.getDeviceID());
			data.put(Constants.REQUEST_TYPE.toString(), MessageTypes.CHANNEL_OFF.toString());
			data.put(Constants.CHANNEL.toString(), type.toString());
			for(Client client : ClientManager.getClientsList()){
				client.sendMessage(JSONMessage.toJson(data));
			}
		}
	}
	
	private void addChannels(List<ConnectionType> toAdd){
		HashMap<Object, Object> data = new HashMap<>();
		data.put(Constants.SENDER_ID.toString(), ApplicationContext.getDeviceID());
		data.put(Constants.REQUEST_TYPE.toString(), MessageTypes.CHANNEL_ON.toString());
		for(ConnectionType type : toAdd){
			if(type == ConnectionType.PHONE){
				log.info("##### Adding PHONE connection type");
				data.put(Constants.CLIENT_PHONE.toString(), AndroidContextController.getCurrentDevicePhoneNumber());
			}
			if(type == ConnectionType.SOCKET){
				log.info("##### Adding SOCKET connection type");
				String ipAddress = ApplicationContext.getIPAddress();
				data.put(Constants.CLIENT_IP_ADDRESS.toString(), ipAddress);
				data.put(Constants.IP_PORT.toString(), ApplicationContext.getPort());
			}
			if(type == ConnectionType.BLUETOOTH){
				log.info("##### Adding BLUETOOTH connection type");
				String btNameString = AndroidContextController.getInstance().getBluetooth().getDeviceBluetoothName();
				data.put(Constants.CLIENT_BT_ID.toString(), btNameString);
			}
		}
		for(Client client : ClientManager.getClientsList()){
			client.sendMessage(JSONMessage.toJson(data));
		}
	}
	
	public void exit(){
		operate = false;
	}
	

}
