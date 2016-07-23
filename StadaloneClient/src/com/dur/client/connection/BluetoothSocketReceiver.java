package com.dur.client.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.model.ApplicationContext;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class BluetoothSocketReceiver extends Receiver implements Runnable {

	private final BluetoothServerSocket serverSocket;
	private BluetoothAdapter adapter;
	private final Log log = LogFactory.getLog(BluetoothSocketReceiver.class);
	private static int SOCKET_WAIT_TIME = 4;

	public BluetoothSocketReceiver() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothServerSocket tmp = null;
		try {
			tmp = adapter.listenUsingInsecureRfcommWithServiceRecord("Mój_telefon", ApplicationContext.APP_ID);
		} 
		catch (IOException e) {
			log.error("##### Unable to create server socket instance" );
		}
		serverSocket = tmp;
	}

	public void run() {
		BluetoothSocket socket = null;
		log.info("##### Bluetooth server started");
		while (true) {
			try {
				log.info("##### Bluetooth server is waiting for connection");
				socket = serverSocket.accept();
				log.info("##### Bluetooth socket connection accepted");
			} 
			catch (IOException e) {
				log.info("##### Closing bluetooth socket after exception");
				return;
			}
			if (socket != null) {
				BufferedReader in = null;
				try {
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					log.info("##### Bluetooth waiting for input");
					int counter = SOCKET_WAIT_TIME;
					while(! in.ready() && counter > 0 ){
						log.info("##### Waiting for socket to be ready");
						Thread.sleep(1000);
						counter --;
					}
					if(in.ready()){
						String message = in.readLine();
						log.info("##### Bluetooth socket received message " + message);
						onMessageReceive(message);
					}
					else{
						log.error("##### Bluetooth socket is not connected");
					}
					socket.close();
					in.close();
				}
				catch (IOException | InterruptedException e) {
					log.error("##### server error " + e.getMessage());
					try {
						socket.close();
						in.close();
					} 
					catch (IOException e1) {
						log.error("##### Unable to close bluetooth client socket");
					}
					return;
				}
			}
			else{
				log.error("##### Bluetooth socket is null");
			}
		}
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
	public void closeChannel() {
		if(serverSocket == null){
			return;
		}
		log.info("##### Closing bluetooth socket");
		try {
			serverSocket.close();
		} 
		catch (IOException e) {
			log.error("##### Unable to close socket " + e.getMessage());
		}

	}
}
