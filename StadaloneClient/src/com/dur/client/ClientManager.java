package com.dur.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.model.Client;

public class ClientManager {
	
	private final static Log log = LogFactory.getLog(ClientManager.class);
	
	private static HashMap<String, Client> clients = new HashMap<>();
	
	private static Set<String> observators = new HashSet<>(); 
	
	public static Client getClient(String id){
		return clients.get(id);
	}
	
	public static void registerClient(String id, Client client){
		clients.put(id, client);
		log.info("##### Client with id " + id + " registered");
	}
	
	public static void removeClient(String id){
		if(clients.containsKey(id)){
			clients.get(id).destroy();
			clients.remove(id);
			removeFromObservators(id);
		}
	}
	
	public static void removeFromObservators(String clientId){
		observators.remove(clientId);
	}
	
	public static Collection<Client> getClientsList(){
		return clients.values();
	}
	
	public static List<Client> getObservators(){
		List<Client> allObservators = new LinkedList<>();
		Iterator<String> iter = observators.iterator();
		while (iter.hasNext()) {
		    String cl = iter.next();
			Client temp = clients.get(cl);
			if(temp != null){
				allObservators.add(temp);
			}
		}
		return allObservators;
	}
	
	public static void addToObservators(String clientId){
		if( ! observators.contains(clientId) && clients.containsKey(clientId)){
			log.info("##### Adding to observators " + clientId);
			observators.add(clientId);
		}
		else{
			log.error("##### will not add to observators");
		}
	}

}
