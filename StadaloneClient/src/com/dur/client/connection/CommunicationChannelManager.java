package com.dur.client.connection;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.shared.Constants;

public class CommunicationChannelManager implements Runnable{
	
	private final Log log = LogFactory.getLog(CommunicationChannelManager.class);
	private TreeSet<CommunicationChannel> channels = new TreeSet<CommunicationChannel>();
	private BlockingQueue<String> messageQueue;
	
	public CommunicationChannelManager (List<CommunicationChannel> channels){
		this.channels.addAll(channels);
		log.info("##### Channels order:");
		for(CommunicationChannel channel : this.channels){
			log.info("##### " + channel.getClass());
		}
		messageQueue = new LinkedBlockingQueue<String>();
	}
	
	public boolean sendMessage(String message){
		try {
			messageQueue.put(message);
		} 
		catch (InterruptedException e) {
			log.error("##### Unable to put message into queue " + e.getMessage());
		}
		return true;
	}
		
	public void destroy(){
		for(CommunicationChannel single : channels){
			single.closeChannel();
		}
	}
	
	public void removeChannel(String channel){
		synchronized (channels) {
			CommunicationChannel chan = null;
			Iterator<CommunicationChannel> iter = channels.iterator();
			while(iter.hasNext()){
				chan = iter.next();
				if(chan.getType().toString().equals(channel)){
					chan.closeChannel();
					channels.remove(chan);
					log.info("##### Removing communication channel " + channel);
					break;
				}
			}
		}
	}
	
	public void addChannels(List<CommunicationChannel> newChannels){
		synchronized (channels) {
			CommunicationChannel chan = null;
			Iterator<CommunicationChannel> iter = null;
			for(CommunicationChannel channel : newChannels){
				iter = channels.iterator();
				while(iter.hasNext()){
					chan = iter.next();
					if(chan.getType().toString().equals(channel)){
						log.info("##### Removing communication channel before add" + channel.getType().toString());
						chan.closeChannel();
						channels.remove(chan);
						break;
					}
				}
				log.info("##### Adding new communication channel " + channel.getType().toString());
				channels.add(channel);
			}
		}
	}

	@Override
	public void run() {
		String message = null;
		try {
			message = messageQueue.take();
			while( ! message.equals(Constants.POISON_PILL.toString())){
				synchronized (channels) {
					log.info("##### Have " + channels.size() + " communication channels");
					for(CommunicationChannel channel : channels){
						if(channel != null){
							log.info("##### Trying to send by " + channel.getClass());
							if(channel.sendMessage(message)){
								log.info("##### Sent by: " + channel.getClass());
								break;
							}
							else{
								log.error("##### unable to send by " + channel.getClass());
							}
						}
					}
				}
				message = messageQueue.take();
			}
		}
		catch (InterruptedException e) {
			log.error("Error during message retrieval " + e.getMessage());
		}
		log.info("##### Communication channel thread exits");
		return;
	}
}
