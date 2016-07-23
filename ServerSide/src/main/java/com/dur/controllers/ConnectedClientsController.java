package com.dur.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dur.model.ClientSession;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;
import com.dur.shared.MessageTypes;

public class ConnectedClientsController {
	
	private HashMap<String, ClientSession> sessions;
	private final Log log = LogFactory.getLog(ConnectedClientsController.class);
	
	public ConnectedClientsController(){
		sessions = new HashMap<String, ClientSession>();
	}
	
	public boolean isClientRegistered(String clientId){
		return sessions.containsKey(clientId);
	}
	
	public void addSession(String clientID, ClientSession session ){
		sessions.put(clientID, session);
		notifyAboutNewClient(clientID);
	}
	
	public ClientSession getClientBySessionId(String sessionId){
		for(ClientSession session : sessions.values()){
			if(session.getCurrentSession().getId().equals(sessionId)){
				return session;
			}
		}
		return null;
	}
	
	public WebSocketSession getSessionFor(String clientId){
		if(null == sessions.get(clientId)){
			return null;
		}
		return sessions.get(clientId).getCurrentSession();
	}
	
	public ClientSession getClient(String clientId){
		return sessions.get(clientId);
	}
	
	public List<ClientSession> getAllClientExcept(String clientId){
		List<ClientSession> clients = new LinkedList<ClientSession>();
		for(ClientSession client : sessions.values()){
			if( ! client.getId().equals(clientId)){
				clients.add(client);
			}
		}
		return clients;
	}
	
	/**
	 * Removes session from the server.
	 * @param sessionID
	 */
	public void removeClient(String clientId){
		if(sessions.containsKey(clientId)){
			log.info("Session " + clientId + " found on server. Removing");
			sessions.remove(clientId);
			log.info("Current registered sessions: " + sessions.keySet());
		}
		else{
			log.info("No such session");
		}
		notifyAboutNewClient(clientId);
		
	}
	
	/**
	 * Removes session from the server.
	 * @param sessionID
	 */
	public void removeClientBySessionId(String sessionId){
		ClientSession client = getClientBySessionId(sessionId);
		if(null != client ){
			removeClient(client.getId());
		}
	}
	
	private void notifyAboutNewClient(String clientId){
		for(ClientSession client : getAllClientExcept(clientId)) {
			List<ClientSession> clients = getAllClientExcept(client.getId());
			List<HashMap<Object, Object>> clientsList = new LinkedList<>();
			for(ClientSession singleClient  : clients){
				clientsList.add(singleClient.getState());
			}
			JSONMessage toSend = new JSONMessage();
			toSend.addParam(Constants.REQUEST_TYPE, MessageTypes.CLIENTS_LIST.toString());
			if( ! clients.isEmpty()){
				toSend.addParam(MessageTypes.CLIENTS_LIST, clientsList);
			}
			TextMessage returnMessage = new TextMessage(toSend.toString());
			log.info("##### Sending notification: " + toSend.toString() + " to " + client.getId());
			try {
				ServerController.getClientsController().getSessionFor(client.getId()).sendMessage(returnMessage);
			} 
			catch (IOException e) {
				log.error("##### Sending notification failed: " + e.getMessage());
			} 
			catch(IllegalArgumentException ex){
				log.error("##### Sending notification failed: " + ex.getMessage());
			}
		}
	}
			
}
