package com.dur.client.controllers;

import java.util.HashMap;
import java.util.Set;

import javafxports.android.FXActivity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.connection.BluetoothSocketReceiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothController {
	private final Log log = LogFactory.getLog(BluetoothController.class);
	private BluetoothAdapter adapter;
	private HashMap<String, BluetoothDevice> foundDevices = new HashMap<>();
	private FXActivity context;
	private BroadcastReceiver discoveryReceiver;
	private BroadcastReceiver stateReceiver;
	private BluetoothSocketReceiver bluetoothServer;
	private boolean isSearching = false;

	public BluetoothController(FXActivity context) {
		adapter = BluetoothAdapter.getDefaultAdapter();
		if(adapter == null){
			return;
		}
		for(BluetoothDevice device : adapter.getBondedDevices()){
			log.info("##### Have bounded device " + device.getName());
			foundDevices.put(device.getName(), device);
		}
		this.context = context;
		discoveryReceiver = getDiscoveryReceiver(context);
		stateReceiver = getBluetoothStateReceiver(context);
	}
	
	public void turnOnBluetooth(){
		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		context.getApplicationContext().registerReceiver(stateReceiver, filter);
		if( ! adapter.isEnabled()){
			adapter.enable();
		}
		else{
			startListening();
		}
	}

	public boolean hasBluetooth() {
		if (adapter != null) {
			log.info("##### Bluetooth is enabled. Device name is " + adapter.getName());
		}
		else {
			log.info("##### Bluetooth disabled");
		}
		return adapter != null;
	}
	
	public boolean isBluetoothEnabled(){
		return hasBluetooth() && adapter.isEnabled();
	}
	
	public BluetoothDevice getDeviceByName(String name){
		BluetoothDevice device = foundDevices.get(name);
		if(device == null){
			log.info("##### Bluetooth device not found");
		}
		else{
			log.info("##### Bluetooth device found");
		}
		return device;
	}
	
	public void startDiscovery(){
		log.info("##### Bluetooth starting discovery");
		isSearching = true;
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.getApplicationContext().registerReceiver(discoveryReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  
		context.getApplicationContext().registerReceiver(discoveryReceiver, filter);  
		Intent discoverableIntent = new	Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		adapter.startDiscovery();
		context.runOnUiThread(new Runnable() {
			public void run() {
				context.startActivity(discoverableIntent);
			}
		});
	}
	
	public void stopDiscovery(){
		adapter.cancelDiscovery();
		isSearching = false;
		unregisterReceiver(discoveryReceiver);
	}
	
	public final boolean isSearching() {
		return isSearching;
	}
	
	public void cleanUp(){
		unregisterReceiver(discoveryReceiver);
		unregisterReceiver(stateReceiver);
		adapter.cancelDiscovery();
		adapter.disable();
		isSearching = false;
		log.info("##### Bluetooth cleaned up");
		stopListening();
	}
	
	public Set<String> getDevicesNames(){
		return foundDevices.keySet();
	}
	
	public String getDeviceBluetoothName(){
		if(adapter != null){
			return adapter.getName();
		}
		return null;
	}
	
	private void unregisterReceiver(BroadcastReceiver receiver){
		try{
			context.getApplicationContext().unregisterReceiver(receiver);
		}
		catch(IllegalArgumentException ex){
			log.error("##### Receiver is not registered");
		}
	}

	private BroadcastReceiver getDiscoveryReceiver(Context context) {
		return new BroadcastReceiver() {  
		    @Override  
		    public void onReceive(Context context, Intent intent) {  
		        String action = intent.getAction();  
		        if(BluetoothDevice.ACTION_FOUND.equals(action)){  
		             BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
		             if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
		            	 log.info("##### Found unbonded device " + device.getName() + "\n" + device.getAddress());
		             }
		             else{
		            	 log.info("##### Found bonded device " + device.getName() + "\n" + device.getAddress());
		             }
		             foundDevices.put(device.getName(), device);
		        }
		        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
		            log.info("##### Bluetooth devices lookup finished");
		            isSearching = false;
		        }  

		    }
		};
	}

	private void stopListening(){
		if(bluetoothServer != null){
			bluetoothServer.closeChannel();
		}
	}
	
	private void startListening(){
		bluetoothServer = new BluetoothSocketReceiver();
		Thread thread = new Thread(bluetoothServer);
		thread.start();
		log.info("##### Bluetooth server thread started");
	}
	
	private final BroadcastReceiver getBluetoothStateReceiver(FXActivity context) {
		return new BroadcastReceiver(){
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        final String action = intent.getAction();
		        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
		            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
		            log.info("##### Bluetooth state changed " + state);
		            switch (state) {
			            case BluetoothAdapter.STATE_OFF:
			            	cleanUp();
			            case BluetoothAdapter.STATE_TURNING_OFF:
			                break;
			            case BluetoothAdapter.STATE_ON:
			            	startListening();
			                break;
			            case BluetoothAdapter.STATE_TURNING_ON:
			                break;
		            }
		        }
		    }
		};
	}

}
