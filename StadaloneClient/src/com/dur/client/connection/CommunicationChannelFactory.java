package com.dur.client.connection;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.controllers.AndroidContextController;
import com.dur.client.model.ApplicationContext;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;
import com.dur.shared.MessageTypes;

import android.bluetooth.BluetoothDevice;


public class CommunicationChannelFactory {
	
	private final static Log log = LogFactory.getLog(CommunicationChannelFactory.class);
	
	public static List<CommunicationChannel> getCommunicationChannels(JSONMessage params){
		List<CommunicationChannel> channels = new LinkedList<>();
		if(params.hasParam(Constants.CLIENT_IP_ADDRESS)){
			String ipAddress = (String) params.get(Constants.CLIENT_IP_ADDRESS);
			String port = (String) params.get(Constants.IP_PORT);
			channels.add(constructCommunicationChannel(ConnectionType.SOCKET, new String[] {ipAddress, port}));
		}
		if(AndroidContextController.isMobileDevice() && params.hasParam(Constants.CLIENT_BT_ID)){
			CommunicationChannel channel = constructCommunicationChannel(ConnectionType.BLUETOOTH, new String[] {
					(String) params.get(Constants.CLIENT_BT_ID)
			} );
			if(channel != null){
				channels.add(channel);
			}
			else{
				log.info("##### Bluetooth device not found. Unable to create communication channel");
			}
		}
		if(null != ApplicationContext.getWebSocketCommunicationChannel()){
			channels.add(ApplicationContext.getWebSocketCommunicationChannel());
		}
		if(AndroidContextController.isMobileDevice() &&  params.hasParam(Constants.CLIENT_PHONE)){
			channels.add(constructCommunicationChannel(ConnectionType.PHONE, new String[]{
					(String) params.get(Constants.CLIENT_PHONE)
			}));
		}
		return channels;
	}
	
	public static CommunicationChannel constructCommunicationChannel(ConnectionType connectionType, String[] params){
		switch (connectionType){
			case PHONE :
				return getSmsCommuniactionChannel(params[0]);
			case BLUETOOTH :
				return getBluetoothComunicationChannel(params[0]);
			case SOCKET:
				return getSocketCommunicationChannel(params[0], params[1]);
			case WEBSOCKET:
				return getWebSocketCommunicationChannel(params[0], params[1]);
			default:
				break;
		}
		return null;
	}
	
	public static BluetoothCommunicationChannel getBluetoothComunicationChannel(String btId){
		log.info("##### Creating Bluetooth communication channel");
		BluetoothDevice device = AndroidContextController.getInstance().getBluetooth().getDeviceByName(btId);
		if(device != null){
			log.info("##### Bluetooth device is not null - creating communication channel");
			return new BluetoothCommunicationChannel(device);
		}
		else{
			log.error("##### Bluetooth device is null. Unable to create communication channel");
			return null;
		}
	}
	
	private static WebSocketCommunicationChannel getWebSocketCommunicationChannel(String address, String port){
		if(ApplicationContext.getWebSocketCommunicationChannel() == null){
			List<String> initialMessages = new LinkedList<>();
			JSONMessage message = ApplicationContext.getBusinessCard();
			message.addParam(Constants.REQUEST_TYPE, MessageTypes.REGISTER.toString());
			initialMessages.add(message.toString());
			
			message = new JSONMessage();
			message.addParam(Constants.REQUEST_TYPE, MessageTypes.GET_CLIENTS.toString());
			message.addParam(Constants.SENDER_ID, ApplicationContext.getDeviceID());
			initialMessages.add(message.toString());
			ApplicationContext.connectToExternalServer(address, port, initialMessages);
		}
		return ApplicationContext.getWebSocketCommunicationChannel();
	}
	
	public static SocketCommunicationChannel getSocketCommunicationChannel(String ipAddress, String port){
		log.info("##### Creating Socket communication channel " + ipAddress + ":" + port);
		return new SocketCommunicationChannel(ipAddress, port);
	}
	
	public static SmsCommuniactionChannel getSmsCommuniactionChannel(String phoneNumber){
		log.info("##### Creating SMS communication channel for number " + phoneNumber);
		return new SmsCommuniactionChannel(phoneNumber);
	}

}
