package com.dur.client.connection;


public enum ConnectionType {
	PHONE, SOCKET, WEBSOCKET, BLUETOOTH;
	
	public static String getMapping(ConnectionType type){
		switch (type){
			case PHONE:
				return "phone number";
			case SOCKET:
				return "IP address";
			case WEBSOCKET:
				return "server IP address";
			case BLUETOOTH:
				return "bluetooth ID";
		}
		return "None";
	}
}
