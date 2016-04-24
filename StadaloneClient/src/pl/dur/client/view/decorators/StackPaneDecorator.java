package pl.dur.client.view.decorators;


import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StackPaneDecorator {
	
	private final Log log = LogFactory.getLog(StackPaneDecorator.class);
	private final StackPane stackPane;
	
	public StackPaneDecorator(StackPane stack){
		this.stackPane = stack;
	}
	
	public void setSize(int width, int height){
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
            	stackPane.setMaxWidth(width);
        		stackPane.setMaxHeight(height);
            }
        });
	}
	
	public void bringToFront(Node node){
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
            	List<Node> nodes = new LinkedList<>();
				for(Node temp : stackPane.getChildren()){
					if(temp != node){
						nodes.add(temp);
					}
				}
				stackPane.getChildren().clear();
				stackPane.getChildren().addAll(nodes);
				stackPane.getChildren().add(node);
            }
		});
	}
	
	public void hideAllExcept(Node node){
		log.info("##### Hiding all nodes except " + node);
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
				for(Node temp : stackPane.getChildren()){
					if(temp != node){
						temp.setVisible(false);
					}
					else{
						temp.setVisible(true);
					}
				}
            }
		});
	}

}
