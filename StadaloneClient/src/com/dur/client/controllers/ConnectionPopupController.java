package com.dur.client.controllers;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.connection.CommunicationChannel;
import com.dur.client.connection.CommunicationChannelFactory;
import com.dur.client.connection.ConnectionType;
import com.dur.client.model.ApplicationContext;
import com.dur.client.model.Client;
import com.dur.client.model.JSONMessage;
import com.dur.client.view.PrimaryView;
import com.dur.client.view.decorators.StackPaneDecorator;
import com.dur.shared.Constants;
import com.dur.shared.MessageTypes;

public class ConnectionPopupController implements Initializable{

	private final Log log = LogFactory.getLog(ConnectionPopupController.class);
	@FXML
	private TextField clientPhone;
	@FXML
	private Button connectToClient;
	@FXML
	private ComboBox<ConnectionType> connectionType;
	@FXML
	private Button returnButton;
	@FXML
	private TextField ipTextField;
	@FXML
	private TextField portTextField;
	@FXML
	private StackPane stack;
	@FXML
	private GridPane phonePane;
	@FXML
	private GridPane bluetoothPane;
	@FXML
	private GridPane socketPane;
	@FXML
	private ProgressIndicator progress;
	@FXML
	private ListView<String> bluetoothDevices;
	private StackPaneDecorator decorator;
	private HashSet<String> devices = new HashSet<String>();
	private boolean stayAlive = false;
	private final static int DISCOVERY_TIME = 60;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("##### Initialize connection popup controller");
		initializeConnectionTypeList();
		initializeConnectButton();
		initializeReturnButton();
		initalizeBluetoothList();
		decorator = new StackPaneDecorator(stack);
		onEnter();
	}
	
	public void onEnter(){
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
            	returnButton.requestFocus();
            	bluetoothPane.setVisible(false);
    			phonePane.setVisible(false);
    			socketPane.setVisible(false);
    			progress.setVisible(false);
            }
        });
	}
	
	private void initializeConnectionTypeList(){
		loadAvailableChannels();
		addListenerToConnectionType();
	}
	
	private void loadAvailableChannels() {
		connectionType.setEditable(false);
		Set<ConnectionType> types = ApplicationContext.getAvailableChannels();
		connectionType.getItems().clear();
		connectionType.getItems().addAll(types);
	}
	
	private void addListenerToConnectionType(){
		connectionType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ConnectionType>() {
	        @Override
	        public void changed(ObservableValue<? extends ConnectionType> observable, ConnectionType oldValue, ConnectionType newValue) {
            	if(null != newValue){
            		log.info("##### Connecting using " + newValue.toString());
            		if(newValue == ConnectionType.SOCKET || newValue == ConnectionType.WEBSOCKET){
            			decorator.hideAllExcept(socketPane);
            			stayAlive = false;
            		}
            		else if(newValue == ConnectionType.BLUETOOTH){
            			decorator.hideAllExcept(bluetoothPane);
            			onBluetoothSelection();
            		}
            		else if(connectionType.getValue() == ConnectionType.PHONE){
            			decorator.hideAllExcept(phonePane);
            			stayAlive = false;
            		}
				}
            }    
        }); 
	}
	
	private void onWebSocketConnection(){
		String address = ipTextField.getText();
		String port = portTextField.getText();
		if(null == address || address.isEmpty()){
			ipTextField.setText("192.168.1.100");
			address = "192.168.1.100";
			//address = "10.13.193.163";
		}
		if(null == port || port.isEmpty()){
			portTextField.setText("8080");
			port = "8080";
		}
		connectUsingConnection(address, port);
	}
	
	private void initializeConnectButton(){
		connectToClient.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				log.info("##### You clicked connect button! #####");
				stayAlive = false;
				if(connectionType.getValue() == ConnectionType.SOCKET){
					onSocketConnection();
				}
				else if(connectionType.getValue() == ConnectionType.WEBSOCKET){
					onWebSocketConnection();
				}
				else if(connectionType.getValue() == ConnectionType.BLUETOOTH){
					onBluetoothConnection();
				}
				else if(connectionType.getValue() == ConnectionType.PHONE){
					onPhoneConnection();
				}
			}
		});
	}
	
	
	
	private void onBluetoothConnection(){
		log.info("##### Selected Bluetooth connection type");
		String selectedDevice = bluetoothDevices.getSelectionModel().getSelectedItem();
		if(null != selectedDevice && AndroidContextController.getInstance().getBluetooth().getDeviceByName(selectedDevice) != null){
			log.info("##### trying to connect using: " + selectedDevice);
			connectUsingConnection(selectedDevice, null);
		}
	}
	
	private void onSocketConnection(){
		String address = ipTextField.getText();
		String port = portTextField.getText();
		log.info("##### Address is " + address + ":" + port);
		if(port != null && address != null && ! address.isEmpty() && ! port.isEmpty()){
			connectUsingConnection(address, port);
		}
	}
	
	private void onPhoneConnection(){
		String address = clientPhone.getText();
		log.info("##### Phone is " + address);
		if(address != null && ! address.isEmpty()){
			connectUsingConnection(address, null);
		}
	}
	
	private void connectUsingConnection(String address, String port){
		HashMap<Object, Object> data = ApplicationContext.getBusinessCard();
		data.put(Constants.REQUEST_TYPE.toString(), MessageTypes.REGISTER.toString());
    	CommunicationChannel channel = CommunicationChannelFactory.constructCommunicationChannel(connectionType.getValue(), new String[] {address, port});
    	if(connectionType.getValue() != ConnectionType.WEBSOCKET){
    		List<CommunicationChannel> channels = new LinkedList<>();
    		channels.add(channel);
    		Client temp = new Client("Temp", channels, "Temp");
    		temp.sendMessage(JSONMessage.toJson(data));
    		temp.destroy();
    	}
    	MainViewController.loadView(PrimaryView.class);
	}
	
	private void initializeReturnButton(){
		returnButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				log.info("##### You clicked return button! #####");
				stayAlive = false;
				MainViewController.loadView(PrimaryView.class);
			}
		});
	}
	
	public void reload(){
		loadAvailableChannels();
		clientPhone.clear();
		ipTextField.clear();
		portTextField.clear();
		onEnter();
	}
	
	private void initalizeBluetoothList(){
		bluetoothDevices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
				log.info("##### User selected device: " + newValue);
				
			}
        });
	}
	
	private void onBluetoothSelection(){
		AndroidContextController.getInstance().getBluetooth().startDiscovery();
		stayAlive = true;
		Runnable checker = new Runnable(){
			@Override
			public void run(){
				progress.setVisible(true);
				int counter = DISCOVERY_TIME;
				while(stayAlive && AndroidContextController.getInstance().getBluetooth().isSearching()){
					Platform.runLater(new Runnable() { 
			            @Override
			            public void run() {
			            	Set<String> devicesSet = AndroidContextController.getInstance().getBluetooth().getDevicesNames();
							for(String single : devicesSet){
								if( ! devices.contains(single)){
									devices.add(single);
									bluetoothDevices.getItems().add(single);
									log.info("##### Added new device to list: " + single);
								}
							}
			            }
					});
					try {
						counter--;
						Thread.sleep(1000);
						if(counter <= 0){
							stayAlive = false;
						}
					} 
					catch (InterruptedException e) {
						log.error("##### Unable to go sleep");
					}
				}
				AndroidContextController.getInstance().getBluetooth().stopDiscovery();
				progress.setVisible(false);
				log.info("##### Discovery finished");
			}
		};
		Thread thread = new Thread(checker);
		thread.start();
	}

}
