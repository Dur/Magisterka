package com.dur.client.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.model.ApplicationContext;

public class SocketCommunicationChannel extends CommunicationChannel{
	
	private final Log log = LogFactory.getLog(SocketCommunicationChannel.class);
	
	private String ipAddress;
	private Integer port;
	private Socket socket;
	
	public SocketCommunicationChannel(String ipAddress, String port){
		super();
		this.ipAddress = ipAddress;
		this.port = Integer.parseInt(port);
	}

	@Override
	public boolean sendMessage(String message) {
		if(null == ApplicationContext.getIPAddress()){
			log.info("##### Socket channel is not available now");
			return false;
		}
		PrintWriter out = null;
		try {
			socket = new Socket(ipAddress, this.port);
			out = new PrintWriter(socket.getOutputStream(), true);
			log.info("##### Socket sending message " + message);
			out.println(message);
			out.close();
			socket.close();
			return true;
		} 
		catch (IOException e) {
			log.error("##### Unable to send message by socket " + e.getMessage());
			try {
				if(out!= null){
					out.close();
				}
				if(null != socket){
					socket.close();
				}
			} 
			catch (IOException e1) {
				log.error("##### Unable to close socket " + e1.getMessage());
			}
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
		if(null != socket && ! socket.isClosed()){
			try {
				socket.close();
			} 
			catch (IOException e) {
				log.error("##### Unable to close client socket " + e.getMessage());
			}
		}
		
	}

	@Override
	protected ConnectionType getType() {
		return ConnectionType.SOCKET;
	}

	@Override
	protected int getDefaultPriority() {
		return 0;
	}

}
