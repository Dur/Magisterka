package pl.dur.client.connection;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

public class SmsSender {
	
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
	private final Log log = LogFactory.getLog(SmsSender.class);
	private final Context androidContext;
	private final int MAX_SMS_SIZE = 159;
	
	public SmsSender(Context context){
		this.androidContext = context;
        androidContext.registerReceiver(getSendReceiver(), new IntentFilter(SENT));
        androidContext.registerReceiver(getDeliveryReceiver(), new IntentFilter(DELIVERED));
	}
	
	
	/**
	 * Send SMS to selected number.
	 * @param message - message to send
	 * @param phoneNumber - receiver number
	 */
	public void sendSms(String message, String phoneNumber){
        
        SmsManager sms = SmsManager.getDefault();
        if(message.length() > MAX_SMS_SIZE){
        	ArrayList<PendingIntent> sentPis = new ArrayList<>();
        	ArrayList<PendingIntent> deliveredPIs = new ArrayList<>();
        	ArrayList<String> parts = sms.divideMessage(message);
        	for(int i = 0; i < parts.size(); i++ ){
        		sentPis.add(PendingIntent.getBroadcast(androidContext.getApplicationContext(), 0, new Intent(SENT), 0));
        		deliveredPIs.add(PendingIntent.getBroadcast(androidContext.getApplicationContext(), 0, new Intent(DELIVERED), 0));
        	}
        	log.info("##### Sending Multiple SMSs : " + message + " to " + phoneNumber);
        	sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
        }
        else{
        	PendingIntent sentPI = PendingIntent.getBroadcast(androidContext.getApplicationContext(), 0, new Intent(SENT), 0);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(androidContext.getApplicationContext(), 0, new Intent(DELIVERED), 0);
        	log.info("##### Sending SMS : " + message + " to " + phoneNumber);
        	sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI); 
        }
	}
 
        
    private BroadcastReceiver getDeliveryReceiver(){
    	BroadcastReceiver delivery = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                    	log.info("##### SMS delivered");
                        break;
                    case Activity.RESULT_CANCELED:
                    	log.error("##### Unable to deliver SMS");
                        break;                        
                }
            }
        }; 
    	return delivery;
    }
        
    private BroadcastReceiver getSendReceiver(){
    	BroadcastReceiver sendBroadcastReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                    	log.info("##### SMS sent successfully");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    	log.error("##### Unable to send SMS. General failer");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                    	log.error("##### Unable to send SMS. Device was not able to sent SMS");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    	log.error("##### Unable to send SMS. Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    	log.error("##### Unable to send SMS. Radio is OFF");
                        break;
                }
            }
        };
        return sendBroadcastReceiver;
    }       
}
