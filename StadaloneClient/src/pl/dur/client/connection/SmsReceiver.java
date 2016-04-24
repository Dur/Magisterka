package pl.dur.client.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.controllers.MessageDispatcher;
import pl.dur.client.model.JSONMessage;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver {

	private final Log log = LogFactory.getLog(SmsReceiver.class);
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SMS_DELIVERED = "android.provider.Telephony.SMS_DELIVER";
    
    public SmsReceiver(Context context) {
    	IntentFilter receivedFilter = new IntentFilter(SMS_RECEIVED);
    	receivedFilter.setPriority(Integer.MAX_VALUE);
    	IntentFilter deliveredFilter = new IntentFilter(SMS_DELIVERED);
    	deliveredFilter.setPriority(Integer.MAX_VALUE);
    	context.registerReceiver(getSmsReceiver(context), receivedFilter);
    	context.registerReceiver(getSmsReceiver(context), deliveredFilter);
    }
    	
    private BroadcastReceiver getSmsReceiver(Context context){
    	BroadcastReceiver receiver = new BroadcastReceiver(){
		    @Override
		    public void onReceive(Context context, Intent intent) {
		    	log.info("##### Checking Intent. Requested intent is: " + intent.getAction()); 
		        if (intent.getAction().equals(SMS_RECEIVED)) {
		        	log.info("##### Received SMS. Processing message");
		            Bundle bundle = intent.getExtras();
		            if (bundle != null) {
		                // get sms objects
		                Object[] pdus = (Object[]) bundle.get("pdus");
		                if (pdus.length == 0) {
		                	log.error("##### No SMS content");
		                    return;
		                }
		                // large message might be broken into many
		                SmsMessage[] messages = new SmsMessage[pdus.length];
		                StringBuilder sb = new StringBuilder();
		                for (int i = 0; i < pdus.length; i++) {
		                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
		                    sb.append(messages[i].getMessageBody());
		                }
		                String sender = messages[0].getOriginatingAddress();
		                String message = sb.toString();
		                log.info("##### Received message from " + sender + ": " + message);
		                try{
		                	JSONMessage.parseJson(message);
		                	log.info("##### Message is valid for application. Processing...");
		                	MessageDispatcher.dispatch(message);
		                	log.info("##### Message processed");
		                	//abortBroadcast();
		                }
		                catch (Exception ex){
		                	log.info("##### Unable to parse message. Forwarding to other handlers " + ex.getMessage());
		                }
		            }
		        }
		    }
    	};
    	return receiver;
    }
}
