package com.dur.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.view.PrimaryView;

public class PopupController implements Initializable {
	
	private final Log log = LogFactory.getLog(PopupController.class);
	
	@FXML 
	private Button confirmButton;
	
	@FXML 
	private TextField phoneNumber;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		confirmButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				log.error("##### You clicked confirm! #####");
				String number = phoneNumber.getText();
				AndroidContextController.setSourcePhoneNumber(number);
				AndroidContextController.storePhoneNumberIntoFile(number);
				MainViewController.loadView(PrimaryView.class);
			}
		});		
	}
}
