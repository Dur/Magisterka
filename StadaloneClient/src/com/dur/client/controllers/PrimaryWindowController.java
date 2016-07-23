package com.dur.client.controllers;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.util.Callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.ClientManager;
import com.dur.client.model.ApplicationContext;
import com.dur.client.model.Client;
import com.dur.client.model.Cords;
import com.dur.client.model.JSONMessage;
import com.dur.client.model.Map;
import com.dur.client.model.SimpleLocation;
import com.dur.client.model.StaticMap;
import com.dur.client.view.ConnectionPopupView;
import com.dur.client.view.decorators.MapViewDecorator;
import com.dur.shared.Constants;
import com.dur.shared.MessageTypes;

public class PrimaryWindowController implements Initializable {
	
	private final Log log = LogFactory.getLog(PrimaryWindowController.class);
	
	@FXML
	private ComboBox<Client> clientsList;
	@FXML 
	private Button exitButton;
	@FXML 
	private Button switchButton;
	@FXML
	private Button connectToClient;
	@FXML
	private Canvas drawCanvas;
	@FXML
	private StackPane stackPane;
	@FXML
	private ImageView imageView;
	@FXML
	private GridPane parentPane;
	@FXML
	private Canvas imageCanvas;
	@FXML
	private TextArea currentLocationText;
	
	private MapViewDecorator mapViewDecorator;
	private SimpleLocation currentLocation;
	private MapController myMapController;
	private MapController userMapController;
	
	public PrimaryWindowController(){
		myMapController = new MapController(false);
		userMapController = new MapController(true);
		StaticMap userMap = new StaticMap( userMapController.calculateMapWidth(), userMapController.calculateMapHeight(), 16, null, Map.MapType.roadmap);
		userMapController.setMap(userMap);
		StaticMap myMap = new StaticMap( myMapController.calculateMapWidth(), myMapController.calculateMapHeight(), 16, null, Map.MapType.roadmap);
		myMapController.setMap(myMap);
	}
	
	public void reloadMyMap(SimpleLocation location){
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
            	Cords newLocation = location.getCords();
            	myMapController.setUserPosition(newLocation);
            }
        });
        log.info("##### Static map reloaded !");
	}
	
	public void reloadUserMap(SimpleLocation location){
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
            	Cords newLocation = location.getCords();
            	userMapController.setUserPosition(newLocation);
            	if(mapViewDecorator.getMapController() == null || mapViewDecorator.getMapController() == userMapController){
            		mapViewDecorator.reloadMap(userMapController);
        		}
            	
            }
        });
        log.info("##### Static map reloaded !");
	}
	
//	public void handleButtonAction(ActionEvent event) {
//		log.error("##### You clicked me once!");
//		File file = null;
//	    String resource = "/pl/dur/client/Map.html";
//	    WebEngine webEngine = webView.getEngine();
//	    URL res = getClass().getResource(resource);
//	    if (res.toString().startsWith("jar:")) {
//	        try {
//	            InputStream input = getClass().getResourceAsStream(resource);
//	            file = new File("/data/data/pl.dur.client/app_dex/Map.htmll");
//	            if(file.exists()){
//	            	file.delete();
//	            }
//	            file.createNewFile();
//	            OutputStream out = new FileOutputStream(file);
//	            int read;
//	            byte[] bytes = new byte[1024];
//
//	            while ((read = input.read(bytes)) != -1) {
//	                out.write(bytes, 0, read);
//	            }
//	        } 
//	        catch (IOException ex) {
//	        	int w = 0;
//	        }
//	        webEngine.load("file://" + file.getAbsolutePath());
//	    }
//	    else{
//	    	webEngine.load(MainClass.class.getResource("/Map.html").toExternalForm());
//	    }
//   }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		log.info("##### Start initalizing");
		MessageDispatcher.getInstance(this);
		log.info("##### Dispatcher initalized");
		initalizeClientsList();
		log.info("##### Clients list initalized");
		initializeExtiButton();
		log.info("##### Exit button initalized");
		initializeConnectToClient();
		initializeSwitchButton();
		log.info("##### Connect to client button initalized");
		mapViewDecorator = new MapViewDecorator(stackPane, imageCanvas, drawCanvas, imageView);
		mapViewDecorator.setMapController(myMapController);
		mapViewDecorator.reloadMap(myMapController);
		this.currentLocationText.setEditable(false);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        log.info("##### Bounds " + primaryScreenBounds.getWidth() + " " + primaryScreenBounds.getHeight());
        currentLocationText.setText("Current location is not set");
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
        		switchButton.setText("Switch to client map");
        		switchButton.setDisable(true);
            	exitButton.requestFocus();
            	
            }
        });
	}
	
	private void initializeExtiButton(){
		exitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				log.info("##### You clicked exit button!");
				MessageDispatcher.dispatch(Constants.POISON_PILL.toString());
				AndroidContextController.exit();
				ApplicationContext.exit();
				Platform.exit();
			}
		});
	}
	
	private void initializeSwitchButton(){
		switchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if(mapViewDecorator.getMapController() == userMapController){
					mapViewDecorator.reloadMap(myMapController);
				}
				else if(mapViewDecorator.getMapController() == myMapController){
					mapViewDecorator.reloadMap(userMapController);
				}
				changeSwithLabel();
			}
		});
	}
	
	private void changeSwithLabel(){
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
				if(mapViewDecorator.getMapController() == myMapController){
					switchButton.setText("Switch to client map");
				}
				else if(mapViewDecorator.getMapController() == userMapController){
					switchButton.setText("Switch to my map");
				}
            }
		});
	}
	
	private void initializeConnectToClient(){
		log.info("##### Initializing connectToClient button");
		if(null != connectToClient){
			connectToClient.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					log.info("##### You clicked connect to client button!");
					MainViewController.loadView(ConnectionPopupView.class);
				}
			});
		}
	}
	
	private void initalizeClientsList(){
		clientsList.setEditable(false);
		clientsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Client>() {
	        @Override
	        public void changed(ObservableValue<? extends Client> observable, Client oldValue, Client newValue) {
	        	log.info("##### You selected observable client!");
	        	log.info("##### Client is " + oldValue + " " + newValue);
            	if(null != newValue){
					HashMap<Object, Object> data = new HashMap<>();
					data.put(Constants.REQUEST_TYPE.toString(), MessageTypes.GET_LOCATION.toString());
					data.put(Constants.SENDER_ID.toString(), ApplicationContext.getDeviceID());
					data.put(Constants.RECIPIENT_ID.toString(), newValue.getId());
					String json = JSONMessage.toJson(data);
					log.error("##### Sending location request: " + json);
					newValue.sendMessage(json);
					
					if(currentLocation != null){
						data.put(Constants.REQUEST_TYPE.toString(), MessageTypes.NEW_LOCATION.toString());
						data.put(MessageTypes.NEW_LOCATION.toString(), currentLocation.getCords());
						newValue.sendMessage(JSONMessage.toJson(data));
						log.info("##### Sending location to " + newValue.getDisplayName());
					}
				}
            	else{
            		log.info("##### Reload map new value is null");
            		mapViewDecorator.reloadMap(myMapController);
            	}
            	if(null != oldValue){
            		HashMap<Object, Object> data = new HashMap<>();
					data.put(Constants.REQUEST_TYPE.toString(), MessageTypes.STOP_OBSERV.toString());
					data.put(Constants.SENDER_ID.toString(), ApplicationContext.getDeviceID());
					data.put(Constants.RECIPIENT_ID.toString(), oldValue.getId());
					String json = JSONMessage.toJson(data);
					log.error("##### Sending stop observ request: " + json);
					oldValue.sendMessage(json);  
            	}
            }    
        });              
		clientsList.setCellFactory(new Callback<ListView<Client>, ListCell<Client>>() {
			@Override public ListCell<Client> call(ListView<Client> p) {
                return new ListCell<Client>() {
                    @Override protected void updateItem(Client item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                        	setText(null);
                        } 
                        else {
                        	setText(item.getDisplayName());
                        }
                   }
              };
          }
       });
	}
	
	public final Client getCurrentObservedClient(){
		return clientsList.getSelectionModel().getSelectedItem();
	}
	
	public final void setClientsList(final Collection<Client> clients){
		if(null == clients || clients.size() == 0){
			log.info("##### Clients list is empty");
			return;
		}
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
            	log.info("##### Received " + clients.size() + " clients to load on view");
            	switchButton.setDisable(false);
            	String selectedClientId = null;
            	if(null != clientsList.getSelectionModel().getSelectedItem()){
            		log.info("##### Selected user is not null");
            		selectedClientId = clientsList.getSelectionModel().getSelectedItem().getId();
            	}
            	else{
            		selectedClientId = clients.iterator().next().getId();
            	}
            	clientsList.getItems().clear();
            	clientsList.getItems().addAll(clients);
            	for(Client client : clients){
            		if(client.getId().equals(selectedClientId)){
            			log.info("##### Selecting previous user");
            			clientsList.getSelectionModel().select(client);
            		}
            	}
            }
        });
	}
	
	public void drawOnMyMap(List<Cords> cords){
		myMapController.setLastPath(cords);
		if(myMapController == mapViewDecorator.getMapController()){
			mapViewDecorator.reloadMap(myMapController);
		}
	}
	
	public final SimpleLocation getCurrentLocation() {
		return currentLocation;
	}

	public final void setCurrentLocation(SimpleLocation currentLocation) {
		log.info("##### setting current location for my map");
		this.currentLocation = currentLocation;
		myMapController.setUserPosition(currentLocation.getCords());
		if(mapViewDecorator.getMapController() == null){
			mapViewDecorator.setMapController(myMapController);
		}
		if(mapViewDecorator.getMapController() == myMapController){
			mapViewDecorator.reloadMap(myMapController);
		}
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
            	log.info("##### During location sending to observators");
            	String text = "Lat: " + currentLocation.getCords().getLatitude() + "\n" + "Lon: " + currentLocation.getCords().getLongitude();
            	currentLocationText.setText(text);
				if(ClientManager.getObservators() != null && ClientManager.getObservators().size() > 0){
					log.info("##### New location will be send to " + ClientManager.getObservators().size() + " clients");
					HashMap<Object, Object> message = new HashMap<>();
					message.put(Constants.REQUEST_TYPE.toString(), MessageTypes.NEW_LOCATION.toString());
					message.put(MessageTypes.NEW_LOCATION.toString(), currentLocation.getCords());
					message.put(Constants.SENDER_ID.toString(), ApplicationContext.getDeviceID());
					for(Client client : ClientManager.getObservators()){
						log.info("##### Sending current location to " + client.getDisplayName());
						message.put(Constants.RECIPIENT_ID.toString(), client.getId());
						client.sendMessage(JSONMessage.toJson(message));
					}
				}
				else{
					log.info("No Registered observators");
				}
            }
        });
	}
}
