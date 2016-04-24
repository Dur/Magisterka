package pl.dur.client;

import javafx.application.Application;
import javafx.stage.Stage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.controllers.AndroidContextController;
import pl.dur.client.controllers.MainViewController;
import pl.dur.client.model.ApplicationContext;
import pl.dur.client.view.PopupView;
import pl.dur.client.view.PrimaryView;

public class MainClass extends Application {

	private final static Log log = LogFactory.getLog(MainClass.class);

	public static void main(String[] args) {
//		
//		String message = "[{CLIENTS_LIST:[],REQUEST_TYPE:CLIENTS_LIST}]";
//		Map parsed = JSONMessage.parseJson(message);
//		List clients = (List) parsed.get(MessageTypes.CLIENTS_LIST.toString());
		Application.launch(args);
		
	}

	@Override
	public void start(Stage stage) {
		log.info("##### Before initialise");
		ApplicationContext.initialise();
		stage.setTitle("My awesome application - " + ApplicationContext.getDisplayName());
		MainViewController.initalise(stage);
        
        if(AndroidContextController.isMobileDevice() && AndroidContextController.hasSimCard()){
        	if(AndroidContextController.getInstance().isFileWithPhoneNumberExists()){
        		log.info("##### File with number already exists");
        		AndroidContextController.getInstance().retrievePhoneNumberFromFile();
        		MainViewController.loadView(PrimaryView.class);
        	}
        	else{
        		MainViewController.loadView(PopupView.class);
        	}
        }
        else{
        	//MainViewController.loadView(PopupView.class);
        	//MainViewController.loadView(ConnectionPopupView.class);
        	MainViewController.loadView(PrimaryView.class);
        }
        log.info("##### After initialise");
        
	}
}