package com.dur.client.view.decorators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.model.PointF;

public class CanvasDecorator {
	
	private final Log log = LogFactory.getLog(CanvasDecorator.class);
	private final Canvas canvas;
	private final GraphicsContext graphicsContext;
	private List<PointF> points = new LinkedList<>();
	private Map<String, ImageDecorator> imagesMap;
	
	public CanvasDecorator(Canvas canvas){
		imagesMap = new HashMap<>();
		this.canvas = canvas;
		this.canvas.setOpacity(1);
		this.graphicsContext = canvas.getGraphicsContext2D();
		this.graphicsContext.setStroke(Color.BLUE);
		this.graphicsContext.setLineWidth(3);
		this.graphicsContext.setFill(Color.RED);
		log.info("##### Canvas initialized");
	}
	
	public void drawFromPoints(List<PointF> drawPoints){
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
				graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		    	graphicsContext.beginPath();
		    	PointF first = drawPoints.get(0);
		    	graphicsContext.moveTo(first.x, first.y);
		    	graphicsContext.stroke();
		    	for(PointF point : drawPoints){
		    		graphicsContext.lineTo(point.x, point.y);
		    		graphicsContext.stroke();
		    	}
		    	graphicsContext.closePath();
            }
        });
	}
	
	public final void addImage(ImageDecorator decorator){
		imagesMap.put(decorator.getFileName(), decorator);
	}
	
	public void drawImages(){
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
            	graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            	for(ImageDecorator dec : imagesMap.values()){
            		graphicsContext.drawImage(dec.getImage(), dec.getX(), dec.getY(), dec.getWidth(), dec.getHeight());
            		log.info("##### draw image " + dec.getFileName() + " on " + dec.getX() + " " + dec.getY());
            	}
            }
        });
	}
	
	public void setCanvasSize(int width, int height){
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
            	canvas.setWidth(width);
        		canvas.setHeight(height);
            }
        });
	}
	
	public void clearCanvas(){
		Platform.runLater(new Runnable() { 
            @Override
            public void run() {
            	graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }
        });
	}
	
	public void changeImageLocation(String fileName, double x, double y){
		ImageDecorator decorator = imagesMap.get(fileName);
		if(decorator == null){
			decorator = new ImageDecorator(x, y, 20, 20, fileName);
			addImage(decorator);
		}
		decorator.setX(x);
		decorator.setY(y);
		drawImages();
	}
	
	public final List<PointF> getPoints() {
		return points;
	}

	public final GraphicsContext getGraphicsContext() {
		return graphicsContext;
	}
	
	public double getHeight(){
		return canvas.getHeight();
	}
	
	public double getWidth(){
		return canvas.getWidth();
	}
}
