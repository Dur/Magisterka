package com.dur.controllers;

public class ServerController {
	
	private static ConnectedClientsController clientsController;
	
	public static ConnectedClientsController getClientsController(){
		if(null == clientsController){
			clientsController = new ConnectedClientsController();
		}
		return clientsController;
	}

}
