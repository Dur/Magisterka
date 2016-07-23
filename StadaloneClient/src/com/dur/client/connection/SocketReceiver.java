package com.dur.client.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketReceiver extends Receiver implements Runnable{
	
	private ServerSocket server;
	private final Log log = LogFactory.getLog(SocketReceiver.class);
	private BufferedReader in; 
	
	public SocketReceiver(int port){
		try {
			server = new ServerSocket(port);
			log.info("##### Socket is listening on port " + port);
		} catch (IOException e) {
			log.error("##### Socket initialisation error " + e.getMessage());
			closeChannel();
		}
	}
	
	@Override
	public void run() {
		while (true){
			Socket socket = null;
			try {
				socket = server.accept();
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String message = in.readLine().trim();
				log.info("##### Socket received message " + message);
				onMessageReceive(message);
			} 
			catch (IOException e) {
				log.error("##### Socket error " + e.getMessage() );
				try {
					if(null != socket){
						socket.close();
					}
					return;
				} 
				catch (IOException e1) {
					log.error("##### Unable to close client socket " + e.getMessage() );
					return;
				}
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
		log.info("##### Closing socket");
		try {
			server.close();
		} catch (IOException e) {
			log.error("##### Unable to close socket " + e.getMessage() );
		}
		
	}

}
