package pl.dur.client.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import javafxports.android.FXActivity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.connection.SmsReceiver;
import pl.dur.client.connection.SmsSender;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Controller responsible for dealing with mobile device API.
 * 
 * @author ddr
 *
 */
public class AndroidContextController {
	
	private static AndroidContextController instance = null;
	private static final String PHONE_NUMBER_FILE_PATH = "/data/data/pl.dur.client/files/number.dat";
	
	private static final Log log = LogFactory.getLog(AndroidContextController.class);
	private FXActivity androidContext;
	private boolean isMobileDevice;
	private SmsSender smsSender;
	private SmsReceiver smsReceiver;
	private String sourcePhoneNumber;
	private BluetoothController bluetooth;
	private LocationController locationService ;
	private boolean hasSimCard = false;
	
	/**
	 * This class is singleton. 
	 * Default constructor is forbidden.
	 */
	private AndroidContextController(){
		try{
			androidContext = FXActivity.getInstance();
			isMobileDevice = true;
			TelephonyManager manager = (TelephonyManager) androidContext.getSystemService(Context.TELEPHONY_SERVICE);
			log.info("##### Managaer is null = " + manager == null);
			hasSimCard = manager != null && manager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM;
			if(hasSimCard){
				smsSender = new SmsSender(androidContext);
				smsReceiver = new SmsReceiver(androidContext);
			}
			log.info("##### Android context initalized");
			bluetooth = new BluetoothController(androidContext);
			if(bluetooth.hasBluetooth()){
				bluetooth.turnOnBluetooth();
			}
		}
		catch(ExceptionInInitializerError exception){
			isMobileDevice = false;
			log.info("##### Unable to initalize android context. Device is not mobile device");
		}
	}
	
	
	/**
	 * Singleton class. Only this kind of object creation is allowed
	 * @return
	 */
	public final static AndroidContextController getInstance(){
		if(null == instance){
			instance = new AndroidContextController();
		}
		return instance;
	}
	
	/**
	 * Closes android application
	 */
	public final static void exit(){
		if(instance.isMobileDevice){
			if(instance.bluetooth != null){
				instance.bluetooth.cleanUp();
			}
			if(instance.locationService != null){
				instance.locationService.stopUsingGPS();
			}
			instance.androidContext.finish();
		}
	}
	
	/**
	 * If user is operating on mobile device.
	 * @return
	 */
	public static boolean isMobileDevice(){
		return getInstance().isMobileDevice;
	}
	
	/**
	 * Send SMS to selected number.
	 * @param message - message to send
	 * @param phoneNumber - receiver number
	 */
	public void sendSms(String message, String phoneNumber){
		if(isMobileDevice){
			smsSender.sendSms(message, phoneNumber);
		}
	}
	
	/**
	 * Returns phone number of current device or null if there is no phone number.
	 * @return
	 */
	public static String getCurrentDevicePhoneNumber(){
		if(null != instance && instance.isMobileDevice){
			log.info("##### Source phone number is: " + instance.sourcePhoneNumber);
			return instance.sourcePhoneNumber;
		}
		log.info("##### Unable to get device phone number. Device is not mobile device");
		return null;
	}
	
	public static void setSourcePhoneNumber(String phone){
		if(null != instance){
			instance.sourcePhoneNumber = phone;
		}
		else{
			log.info("##### Unable to set device phone number. Device is not mobile device");
		}
	}
	
	public void retrievePhoneNumberFromFile(){
		File file = new File(PHONE_NUMBER_FILE_PATH);
        if( file.exists()){
        	BufferedReader br = null;
            try {
            	br = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }
                sourcePhoneNumber = sb.toString();
                br.close();
            }
            catch (IOException e) {
        		log.error("##### Error while loading file with phone number " + e.getMessage());
        		try{
        			br.close();
        		}
        		catch(Exception ex){
        			log.error("##### Error while closing file " + e.getMessage());
        		}
        	}
        }
	}
	
	
	public static void storePhoneNumberIntoFile(String phoneNumber){
		if(null != instance && null != phoneNumber && ! phoneNumber.isEmpty()){
			File file = new File(PHONE_NUMBER_FILE_PATH);
			log.info("##### Local file path: " + getInstance().androidContext.getFilesDir());
			try {
				file.createNewFile();
				OutputStream out = new FileOutputStream(file);
		        out.write(phoneNumber.getBytes());
		        out.close();
		        setSourcePhoneNumber(phoneNumber);
			} 
			catch (IOException e1) {
				log.error("##### Unnable to create new file!!!! " + e1.getMessage());
			}
			log.info("##### File created: " + file.exists() + " " + file.getAbsolutePath());
		}
		else{
			log.info("##### Unable to set device phone number. Device is not mobile device");
		}
	}
	
	public static boolean hasSimCard(){
		return getInstance().hasSimCard;
	}
	
	public boolean isFileWithPhoneNumberExists(){
		if( ! isMobileDevice){
			return false;
		}
		log.info("##### check if phone number exists");
		File file = new File(PHONE_NUMBER_FILE_PATH);
        return file.exists();
	}
	
	public static String getWifiIpAddress() {
	    WifiManager wifiManager = (WifiManager) getInstance().androidContext.getSystemService(Context.WIFI_SERVICE);
	    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

	    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
	        ipAddress = Integer.reverseBytes(ipAddress);
	    }

	    byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

	    String ipAddressString;
	    try {
	        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
	    } catch (UnknownHostException ex) {
	        log.error("##### WIFIIP " + "Unable to get host address.");
	        ipAddressString = null;
	    }

	    return ipAddressString;
	}
	
	public static String getWiFiMacAddress(){
		WifiManager wifiManager = (WifiManager) getInstance().androidContext.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getConnectionInfo().getMacAddress();
	}
	
	public static String getDeviceID(){
		TelephonyManager telephonyManager = (TelephonyManager)getInstance().androidContext.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	public final SmsSender getSmsSender() {
		return smsSender;
	}

	public final SmsReceiver getSmsReceiver() {
		return smsReceiver;
	}
	
	public void showAlert(String message){
		androidContext.runOnUiThread(new Runnable() {
			  public void run() {
				  Toast.makeText(androidContext.getBaseContext(), message, Toast.LENGTH_LONG).show();
			  }
		});
	}
	
	public static void turnOnGps(){
		if(isMobileDevice() && instance.locationService == null){
			instance.androidContext.runOnUiThread(new Runnable() {
				  public void run() {
					  log.info("##### Starting GPS from android context");
					  instance.locationService = new LocationController(instance.androidContext);
					  instance.locationService.startGps();
				  }
			});
		}
	}
	
	public static boolean hasBluetooth(){
		return isMobileDevice() && instance.bluetooth.isBluetoothEnabled();
	}

	public final BluetoothController getBluetooth() {
		return bluetooth;
	}
}
