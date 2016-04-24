package pl.dur.client.controllers;

import java.util.HashMap;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.MainClass;
import pl.dur.client.model.ApplicationContext;
import pl.dur.client.view.View;

public class MainViewController {
	
	private final static Log log = LogFactory.getLog(MainViewController.class);
	private static MainViewController controller;
	private final Stage mainStage;
	
	private final HashMap<String, View> viewMap = new HashMap<>();
	
	public static void initalise(Stage stage){
		if(null == controller){
			controller = new MainViewController(stage);
		}
		/*stage.setOnShown(new EventHandler<WindowEvent>() {
		      public void handle(WindowEvent e) {
		        if(null != currentView){
		        	log.info("##### handling stage initialised");
		        	AndroidContextController.turnOnGps();
		        }
		      }
		    });*/
	}
	
	private void showNewScene(){
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
            	mainStage.show();
            }
        });
	}
	
	private MainViewController(Stage stage){
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(ApplicationContext.getScreenWidth());
        stage.setHeight(ApplicationContext.getScreenHeight());
		this.mainStage = stage;
	}
	
	public static void loadView(Class<? extends View> viewClass){
		View currentView = null;
		if(null == controller){
			log.error("##### Controller is null. Cannot load new view");
			return;
		}
		if(controller.viewMap.containsKey(viewClass.toString())){
			currentView = controller.viewMap.get(viewClass.toString());
			currentView.onReload();
		}
		else{
			try {
				currentView = viewClass.newInstance();
				controller.viewMap.put(viewClass.toString(), currentView);
			} 
			catch (InstantiationException | IllegalAccessException e) {
				log.error("##### Unable to initalize instance of class " + viewClass.toString() + " " + e.getMessage());
			}
		}
		if(null != currentView){
			log.error("##### View initalized");
			//controller.mainStage.hide();
			Scene scene = currentView.getScene();
			scene.getStylesheets().add(MainClass.class.getResource("/background.css").toExternalForm());
			controller.mainStage.setScene(scene);
			controller.showNewScene();
		}
	}
	
	public static Initializable getControllerForView(Class<? extends View> viewClass){
		if(controller.viewMap.containsKey(viewClass.toString())){
			return controller.viewMap.get(viewClass.toString()).getController();
		}
		else{
			return null;
		}
	}
	
	
	
	

}
