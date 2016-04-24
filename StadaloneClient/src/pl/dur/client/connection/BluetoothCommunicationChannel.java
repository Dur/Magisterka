package pl.dur.client.connection;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.model.ApplicationContext;
import pl.dur.client.shared.ConnectionType;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;


public class BluetoothCommunicationChannel extends CommunicationChannel{
	
	private static final Log log = LogFactory.getLog(BluetoothCommunicationChannel.class);
	private final BluetoothDevice device;
	private BluetoothSocket socket;
	private static int SOCKET_WAIT_TIME = 4;
	
	public BluetoothCommunicationChannel(BluetoothDevice device){
		this.device = device;
	}

	@Override
	public boolean sendMessage(String message) {
		PrintWriter out = null;
		try {
			log.info("##### sending message by bluetooth " + message);
			this.socket = device.createInsecureRfcommSocketToServiceRecord(ApplicationContext.APP_ID);
			socket.connect();
        } 
		catch (IOException e) {
        	log.info("##### Exception during connect by bluetooth " + e.getMessage() + "/n" + e.getStackTrace());
        }
		try {
			int counter = SOCKET_WAIT_TIME;
			while(! socket.isConnected() && counter > 0){
				log.info("##### Waiting for connection to be established");
				Thread.sleep(1000);
				counter --;
			}
			if(socket.isConnected()){
				log.info("##### Bluetooth socket is up and ready");
				out = new PrintWriter(socket.getOutputStream(), true);
				log.info("##### Bluetooth socket sending message " + message);
				out.println(message);
				out.close();
				socket.close();
				return true;
			}
			socket.close();
			log.error("##### Bluetooth socket is not connected, unable to send message");
		} 
		catch (IOException | InterruptedException e) {
			log.error("##### Unable to send message by bluetooth socket " + e.getMessage());
			try {
				socket.close();
			} 
			catch (IOException e1) {
				log.error("##### Unable to close Bluetooth socket " + e1.getMessage());
			}
			
		}
		return false;
	}

	@Override
	protected void beforeDispatch(String message) {
	}

	@Override
	protected void afterDispatch(String message) {
	}

	@Override
	protected void closeChannel() {
		if(null != socket && socket.isConnected()){
			try {
				socket.close();
			} 
			catch (IOException e) {
				log.error("##### Unable to close socket bluetooth client" + e.getMessage());
			}
		}
	}

	@Override
	protected ConnectionType getType() {
		return ConnectionType.BLUETOOTH;
	}

	@Override
	protected int getDefaultPriority() {
		return 5;
	}
}
