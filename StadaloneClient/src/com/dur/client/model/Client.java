package com.dur.client.model;

import java.util.List;

import com.dur.client.connection.CommunicationChannel;
import com.dur.client.connection.CommunicationChannelManager;
import com.dur.shared.Constants;

public class Client {
	
	private final String id;
	private final String displayName;
	
	private CommunicationChannelManager channels;
	
	public Client(String id, List<CommunicationChannel> channels, String displayName){
		this.channels = new CommunicationChannelManager(channels);
		this.displayName = displayName;
		this.id = id;
		Thread thread = new Thread(this.channels);
		thread.start();
	}
	
	public void destroy(){
		channels.sendMessage(Constants.POISON_PILL.toString());
		channels.destroy();
	}
	
	public boolean sendMessage(String message){
		return channels.sendMessage(message);
	}

	public final String getId() {
		return id;
	}

	public final String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
	
	public void removeCommunicationChannel(String channelType){
		channels.removeChannel(channelType);
	}
	
	public void addCommunicationChannels(List<CommunicationChannel> conChannels){
		channels.addChannels(conChannels);
	}
	
	
}
