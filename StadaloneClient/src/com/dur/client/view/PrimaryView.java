package com.dur.client.view;

import com.dur.client.controllers.AndroidContextController;



public class PrimaryView extends View{
	
	private static final String FILENAME_STRING = "PrimaryStage.fxml";
	
	public PrimaryView(){
		super();
	}

	@Override
	protected String getViewFileName() {
		return FILENAME_STRING;
	}

	@Override
	protected void doExtraWorkAfterInitalisation() {
		AndroidContextController.turnOnGps();
	}

	@Override
	public void onReload() {
	}

}
