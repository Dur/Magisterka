package com.dur.client.view.decorators;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.controllers.MainViewController;
import com.dur.client.controllers.MapController;
import com.dur.client.controllers.PrimaryWindowController;
import com.dur.client.model.ApplicationContext;
import com.dur.client.model.Client;
import com.dur.client.model.Cords;
import com.dur.client.model.PointF;
import com.dur.client.view.PrimaryView;
import com.dur.shared.Constants;
import com.dur.shared.JSONMessage;
import com.dur.shared.MessageTypes;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class MapViewDecorator {
	
	private final Log log = LogFactory.getLog(MapViewDecorator.class);
	
	private final StackPaneDecorator stackDecorator;
	private final CanvasDecorator drawCanvasDecorator;
	private final CanvasDecorator imageCanvasDecorator;
	private MapController mapController;
	private final ImageView imageView;
	private final ImageDecorator unknown;
	
	public MapViewDecorator(StackPane stack, Canvas imageCanvas, Canvas drawCanvas, ImageView imageView){
		this.drawCanvasDecorator = new CanvasDecorator(drawCanvas);
		this.imageCanvasDecorator = new CanvasDecorator(imageCanvas);
		this.stackDecorator = new StackPaneDecorator(stack);
		drawCanvas.setOnMousePressed(getMousePressedHandler(drawCanvasDecorator));
        drawCanvas.setOnMouseDragged(getMouseDraggedHandler(drawCanvasDecorator));
        drawCanvas.setOnMouseReleased(getMouseReleasedHandler(drawCanvasDecorator));
		this.imageView = imageView;
		imageView.toBack();
		imageCanvas.toFront();
		drawCanvas.toFront();
		this.unknown = new ImageDecorator(0.0, 0.0, ApplicationContext.getScreenWidth() * 0.7, ApplicationContext.getScreenHeight() * 0.7, "unknown.png");
	}

	public final MapController getMapController() {
		return mapController;
	}
	
	private void reloadComponents(){
		if(null == mapController.getUserPosition()){
			imageView.setImage(unknown.getImage());
			drawCanvasDecorator.clearCanvas();
			imageCanvasDecorator.clearCanvas();
			return;
		}
		if(mapController.getMap().getCenter() == null || mapController.getCalculator().isPointTooFarFromCenter(mapController.getUserPosition())){
			mapController.getMap().setCenter(mapController.getUserPosition());
	        imageView.setImage(mapController.reloadMap());
	        log.info("#####" + mapController.getMap().getMapAddress());
	        drawCanvasDecorator.setCanvasSize(mapController.calculateMapWidth(), mapController.calculateMapHeight());
			imageCanvasDecorator.setCanvasSize(mapController.calculateMapWidth(), mapController.calculateMapHeight());
			drawCanvasDecorator.clearCanvas();
			imageCanvasDecorator.clearCanvas();
	    }
	    PointF imagePos = mapController.getCalculator().transformToPoint(mapController.getUserPosition());
		imageCanvasDecorator.changeImageLocation("smile.png", imagePos.x, imagePos.y);
		imageView.setImage(mapController.getMapImage());
		if(null != mapController.getLastPath()){
			drawCanvasDecorator.drawFromPoints(mapController.getCalculator().transformToPoints(mapController.getLastPath()));
		}
	}
	
	public void addImage(ImageDecorator imageDecorator){
		this.imageCanvasDecorator.addImage(imageDecorator);
	}
	
	public void setMapController(MapController controller){
		this.mapController = controller;
	}
	
	public void reloadMap(MapController controller){
		if(this.mapController != controller){
			drawCanvasDecorator.clearCanvas();
			imageCanvasDecorator.clearCanvas();
			this.mapController = controller;
			stackDecorator.setSize(mapController.getMap().getWidth(), mapController.getMap().getHeight());
			drawCanvasDecorator.setCanvasSize(mapController.getMap().getWidth(), mapController.getMap().getHeight());
			imageCanvasDecorator.setCanvasSize(mapController.getMap().getWidth(), mapController.getMap().getHeight());
		}
		reloadComponents();
	}
	
	private EventHandler<MouseEvent> getMousePressedHandler(CanvasDecorator decorator) {
		return new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
            	if(mapController.isEnableDraw()){
	            	decorator.getPoints().clear();
	            	decorator.getGraphicsContext().clearRect(0, 0, decorator.getWidth(), decorator.getHeight());
	            	decorator.getGraphicsContext().beginPath();
	            	decorator.getGraphicsContext().moveTo(event.getX(), event.getY());
	            	decorator.getPoints().add(new PointF(event.getX(), event.getY()));
	            	decorator.getGraphicsContext().stroke();
            	}
            }
        };
	}
	
	private EventHandler<MouseEvent> getMouseDraggedHandler(CanvasDecorator decorator) {
		return new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
            	if(mapController.isEnableDraw()){
	            	decorator.getGraphicsContext().lineTo(event.getX(), event.getY());
	            	decorator.getPoints().add(new PointF(event.getX(), event.getY()));
	            	decorator.getGraphicsContext().stroke();
            	}
            }
        };
	}
	
	private EventHandler<MouseEvent> getMouseReleasedHandler(CanvasDecorator decorator) {
		return new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
            	if(mapController.isEnableDraw()){
	            	int counter = 0;
	            	int pointsToSend = 0;
	            	List<PointF> pointsToSendList = new LinkedList<>();
	            	int modPoints = new Double(Math.floor(decorator.getPoints().size() / 20)).intValue();
	            	modPoints = Math.max(modPoints, 1);
	            	log.info("##### Have " + decorator.getPoints().size() + " points");
	            	decorator.getGraphicsContext().closePath();
	            	decorator.getGraphicsContext().clearRect(0, 0, decorator.getWidth(), decorator.getHeight());
	            	decorator.getGraphicsContext().beginPath();
	            	PointF first = decorator.getPoints().get(0);
	            	decorator.getGraphicsContext().moveTo(first.x, first.y);
	            	decorator.getGraphicsContext().stroke();
	            	for(PointF point : decorator.getPoints()){
	            		if(counter == 0){
	            			decorator.getGraphicsContext().lineTo(point.x, point.y);
	            			decorator.getGraphicsContext().stroke();
	            			pointsToSendList.add(point);
	            			pointsToSend++;
	            		}
	            		counter = (counter + 1) % modPoints;
	            	}
	            	decorator.getGraphicsContext().closePath();
	            	PrimaryWindowController controll = (PrimaryWindowController) MainViewController.getControllerForView(PrimaryView.class);
	            	Client current = controll.getCurrentObservedClient();
	            	if(null != current){
		            	log.info("##### Will send " + pointsToSend + " points");
		            	List<Cords> cordsList = mapController.getCalculator().transformToCords(pointsToSendList);
		            	//canvasDecorator.drawFromPoints(mapController.getCalculator().transformToPoints(cordsList));
		            	JSONMessage message = new JSONMessage();
		            	message.addParam(Constants.REQUEST_TYPE, MessageTypes.DRAW_PATH.toString());
		            	message.addParam(Constants.SENDER_ID, ApplicationContext.getDeviceID());
		            	message.addParam(Constants.RECIPIENT_ID, current.getId());
		            	message.addParam(Constants.PATH, cordsList);
						log.error("##### Sending draw request: " + message.toString());
						current.sendMessage(message);  
	            	}
            	}
            }
        };
	}

}
