package pl.dur.client.connection;

import java.net.URI;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

public class WebSocket extends WebSocketClient {

	private final Log log = LogFactory.getLog(WebSocket.class);
	
	private WebSocketCommunicationChannel webSocketChannel;
	private List<String> messages; 
	
	public WebSocket(URI serverUri, Draft draft, WebSocketCommunicationChannel webSocketChannel, List<String> messages) {
		super(serverUri, draft);
		this.webSocketChannel = webSocketChannel;
		this.messages = messages;
		log.info("##### Connecting to: " + serverUri);
	}

	public WebSocket(URI serverURI) {
		super(serverURI);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		log.error("##### Websocket conection opend");
		for(String message : messages){
			log.info("##### sending after connect :" + message);
			this.send(message);
		}
		log.error("##### Websocket conection opend");
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		log.info("closing websocket");
		synchronized(this){
			this.notify();
		}
	}

	@Override
	public void onMessage(String message) {
		log.info("##### Received message " + message);
		webSocketChannel.onMessageReceive(message);
	}

	@Override
	public void onError(Exception ex) {
		log.error("Socket error");
		log.error(ex);
		super.close();
		synchronized(this){
			this.notify();
		}
	}
}
