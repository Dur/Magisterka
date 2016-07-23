package com.dur.model;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import com.dur.shared.Constants;


public class ClientSession {
	
	private final Log log = LogFactory.getLog(ClientSession.class);
	
	private final String id;
	private final String displayName;
	private String phoneNumber;
	private String ipAddress;
	private String bluetoothId;
	private WebSocketSession currentSession;
	private String port;
	
	public ClientSession(String id, String phoneNumber, String ipAddress, String bluetoothId, WebSocketSession currentSession, String displayName, String port) {
		super();
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.ipAddress = ipAddress;
		this.bluetoothId = bluetoothId;
		this.currentSession = currentSession;
		this.displayName = displayName;
		this.port = port;
	}
	
	public HashMap<Object , Object> getState(){
		HashMap<Object, Object> state = new HashMap<Object, Object>();
		state.put(Constants.SENDER_ID.toString(), id);
		state.put(Constants.DISPLAY_NAME.toString(), displayName);
		if(null != phoneNumber){
			state.put(Constants.CLIENT_PHONE.toString(), phoneNumber);
		}
		if(null != ipAddress){
			state.put(Constants.CLIENT_IP_ADDRESS.toString(), ipAddress);
			state.put(Constants.IP_PORT.toString(), port);
		}
		if(null != bluetoothId){
			state.put(Constants.CLIENT_BT_ID.toString(), bluetoothId);
		}
		return state;
	}
	
	public void rewriteProperties(String phoneNumber, String ipAddress, String bluetoothId, WebSocketSession currentSession, String port){
		this.phoneNumber = phoneNumber;
		this.ipAddress = ipAddress;
		this.bluetoothId = bluetoothId;
		this.port = port;
		if( ! currentSession.getId().equals(this.currentSession.getId())){
			try {
				this.currentSession.close(CloseStatus.SERVICE_RESTARTED);
			} catch (IOException e) {
				log.error("##### Problem with closing session for " + id + " " + e.getMessage());
			}
			this.currentSession = currentSession;
		}
	}

	public final void setPort(String port) {
		this.port = port;
	}

	public final String getPort() {
		return port;
	}

	public final String getPhoneNumber() {
		return phoneNumber;
	}
	
	public final void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public final String getIpAddress() {
		return ipAddress;
	}
	
	public final void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public final String getBluetoothId() {
		return bluetoothId;
	}
	
	public final void setBluetoothId(String bluetoothId) {
		this.bluetoothId = bluetoothId;
	}
	
	public final String getId() {
		return id;
	}

	public final WebSocketSession getCurrentSession() {
		return currentSession;
	}

	public final void setCurrentSession(WebSocketSession currentSession) {
		this.currentSession = currentSession;
	}

	public final String getDisplayName() {
		return displayName;
	}
	
}

