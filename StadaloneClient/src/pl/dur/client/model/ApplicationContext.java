package pl.dur.client.model;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.connection.SocketReceiver;
import pl.dur.client.connection.WebSocketCommunicationChannel;
import pl.dur.client.controllers.AndroidContextController;
import pl.dur.client.shared.ConnectionType;
import pl.dur.client.shared.Constants;

public class ApplicationContext {
	
	private final static Log log = LogFactory.getLog(ApplicationContext.class);
	private static String DEVICE_ID;
	private static String displayName;
	private static WebSocketCommunicationChannel connectionController;
	private static SocketReceiver socketReceiver;
	private static int PORT = 9999;
	private static double screenWidth;
	private static double screenHeight;
	public static final UUID APP_ID = UUID.fromString ("2cadac90-fcbf-11e4-b939-0800200c9a66");
	private static ApplicationStateMonitor monitor;
	
	public static String getDeviceID(){
		return DEVICE_ID;
	}
	
	public static final String getDisplayName() {
		return displayName;
	}

	public static void initialise(){
		log.info("##### Initialising Application context");
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		setScreenWidth(primaryScreenBounds.getWidth());
        setScreenHeight(primaryScreenBounds.getHeight());
		displayName = generateDisplayName();
		if(AndroidContextController.isMobileDevice() && AndroidContextController.hasSimCard()){
			DEVICE_ID = AndroidContextController.getDeviceID();
			log.info("##### ID is: " + DEVICE_ID);
		}
		else if(AndroidContextController.isMobileDevice()){
			DEVICE_ID = AndroidContextController.getWiFiMacAddress();
			log.info("##### ID is: " + DEVICE_ID);
		}
		else{
			try {
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				NetworkInterface inter;
				while(interfaces.hasMoreElements()){
					inter = interfaces.nextElement();
					if(null != inter.getHardwareAddress() &&  ! inter.isLoopback() && inter.isUp() && ! inter.isPointToPoint() && !inter.isVirtual()){
						StringBuilder builder = new StringBuilder();
						log.info("##### IP is: " + inter.getInetAddresses().nextElement().getHostAddress());
						
						byte[] mac = inter.getHardwareAddress();
						for (int i = 0; i < mac.length; i++) {
							builder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
						}
						DEVICE_ID = builder.toString();
						log.info("##### ID is: " + DEVICE_ID + " for interface: " + inter.getDisplayName());
						break;
					}
				}

			} catch (SocketException e) {
				log.error("##### " + "Problem during getting interfaces " + e.getMessage() );
			}
		}
		if(null != getIPAddress()){
			socketReceiver = new SocketReceiver(PORT);
			Thread socketThread = new Thread(socketReceiver);
			socketThread.start();
		}
		startStateMonitor();
	}
	
	public static String getIPAddress(){
		String ipAddressString = null;
		if(AndroidContextController.isMobileDevice()){
			ipAddressString = AndroidContextController.getWifiIpAddress();
			//log.info("##### IP is: " + ipAddressString);
		}
		else{
			try {
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				NetworkInterface inter;
				while(interfaces.hasMoreElements()){
					inter = interfaces.nextElement();
					if(null != inter.getHardwareAddress() &&  ! inter.isLoopback() && inter.isUp() && ! inter.isPointToPoint() && !inter.isVirtual()){
						ipAddressString = inter.getInetAddresses().nextElement().getHostAddress();
						//log.info("##### IP is: " + ipAddressString);
					}
				}
	
			} catch (SocketException e) {
				log.error("##### " + "Problem during getting IP Address " + e.getMessage() );
			}
		}
		return ipAddressString;
	}
	
	private static String generateDisplayName(){
		int num = new Double((Math.random() * 100)).intValue() % 100;
		return "User" + num;
	}
	
	public static String getPort(){
		return ""+PORT;
	}
	
	public static HashMap<Object, Object> getBusinessCard(){
		HashMap<Object, Object> data = new HashMap<>();
		data.put(Constants.SENDER_ID.toString(), ApplicationContext.getDeviceID());
		data.put(Constants.DISPLAY_NAME, ApplicationContext.getDisplayName());
		if(AndroidContextController.isMobileDevice()){
			data.put(Constants.CLIENT_PHONE.toString(), AndroidContextController.getCurrentDevicePhoneNumber());
		}
		String ipAddress = ApplicationContext.getIPAddress();
		if(null != ipAddress){
			data.put(Constants.CLIENT_IP_ADDRESS.toString(), ipAddress);
			data.put(Constants.IP_PORT.toString(), "" + PORT);
		}
		if(AndroidContextController.hasBluetooth()){
			String btNameString = AndroidContextController.getInstance().getBluetooth().getDeviceBluetoothName();
			data.put(Constants.CLIENT_BT_ID.toString(), btNameString);
		}
		return data;
		
	}
	
	public static void connectToExternalServer(String address, String port, List<String> initialMessages){
		try{
			if(null == connectionController){
				log.info("##### Creating WebSocket communication channel");
				connectionController = new WebSocketCommunicationChannel(address + ":" + port, initialMessages);
				Thread thread = new Thread(connectionController);
				thread.start();
			}
		}
		catch(Exception ex){
			log.error("##### Unable to connect to remote server");
			connectionController.exit();
			connectionController = null;
		}
	}
	
	public static Set<ConnectionType> getAvailableChannels(){
		Set<ConnectionType> availableChannels = new HashSet<>();
		if(AndroidContextController.isMobileDevice()){
			availableChannels.add(ConnectionType.PHONE);
		}
		String ipAddress = ApplicationContext.getIPAddress();
		if(null != ipAddress){
			availableChannels.add(ConnectionType.SOCKET);
			availableChannels.add(ConnectionType.WEBSOCKET);
		}
		if(AndroidContextController.hasBluetooth()){
			availableChannels.add(ConnectionType.BLUETOOTH);
		}
		return availableChannels;
		
	}
	
	private static void startStateMonitor(){
		monitor = new ApplicationStateMonitor(getAvailableChannels());
		Thread thread = new Thread(monitor);
		thread.start();
	}
	
	public static WebSocketCommunicationChannel getWebSocketCommunicationChannel(){
		return connectionController;
	}
	
	public static void exit(){
		log.info("##### Application context cleanup");
		monitor.exit();
		if(null != getWebSocketCommunicationChannel()){
			getWebSocketCommunicationChannel().exit();
		}
		if(null != socketReceiver){
			socketReceiver.closeChannel();
		}
	}

	public static final double getScreenWidth() {
		return screenWidth;
	}

	public static final void setScreenWidth(double screenWidth) {
		ApplicationContext.screenWidth = screenWidth;
	}

	public static final double getScreenHeight() {
		return screenHeight;
	}

	public static final void setScreenHeight(double screenHeight) {
		ApplicationContext.screenHeight = screenHeight;
	}
	
}
