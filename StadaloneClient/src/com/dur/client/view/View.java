package com.dur.client.view;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.MainClass;

public abstract class View {
	
	private final static Log log = LogFactory.getLog(View.class);
	protected Pane layout = null;
	private FXMLLoader loader = null;
	private Scene scene = null;
	private Initializable controller;
	
	protected abstract String getViewFileName();
	
	protected abstract void doExtraWorkAfterInitalisation();
	
	public abstract void onReload();
	
	protected View(){
		initalize();
	}
	
	private final void initalize(){
		try{
			log.info("#####  loading FXML " + getViewFileName());
			loader = new FXMLLoader(MainClass.class.getResource("/" + getViewFileName()));
			layout = (Pane) loader.load();
			controller = loader.getController();
		}
		catch (Exception ex){
			log.error("##### Error while loading FXML " + ex.getMessage());
		}
		scene = new Scene(layout);
		doExtraWorkAfterInitalisation();
	}
	
	public final Initializable getController() {
		return controller;
	}
	
	public final Scene getScene(){
		return scene;
	}

}
