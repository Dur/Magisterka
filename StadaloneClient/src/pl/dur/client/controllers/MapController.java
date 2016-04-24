package pl.dur.client.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import javafx.scene.image.Image;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.dur.client.model.ApplicationContext;
import pl.dur.client.model.Cords;
import pl.dur.client.model.PointF;
import pl.dur.client.model.StaticMap;
import pl.dur.client.util.MapCoordinatesCalculator;

public class MapController {

	private final Log log = LogFactory.getLog(MapController.class);
	private MapCoordinatesCalculator calculator;
	private StaticMap map;
	private List<Cords> lastPath;
	private Cords userPosition;
	private final boolean enableDraw;
	
	public MapController(boolean enableDraw) {
		this.enableDraw = enableDraw;
	}	
	
	public List<PointF> getDrawPoints(List<Cords> mapPoints){
		lastPath = mapPoints;
		return calculator.transformToPoints(mapPoints);
	}
	
	public List<PointF> getLastDrawPoints(){
		return calculator.transformToPoints(lastPath);
	}

	public void loadFromInternalFile(String resource) {
		File file = null;
		if(resource == null){
			resource = "/pl/dur/client/Map.html";
		}
		URL res = getClass().getResource(resource);
		if (res.toString().startsWith("jar:")) {
			try {
				InputStream input = getClass().getResourceAsStream(resource);
				file = new File("/data/data/pl.dur.client/app_dex/Map.html");
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				OutputStream out = new FileOutputStream(file);
				int read;
				byte[] bytes = new byte[1024];

				while ((read = input.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.close();
			} 
			catch (IOException ex) {
				log.error(ex.getStackTrace());
			}
			
		}
	}
	
	public int calculateMapWidth(){
		double columnWidth = Math.floor(ApplicationContext.getScreenWidth() * 0.7);
		return new Double(Math.min(columnWidth, 640.0)).intValue();
	}
	
	public int calculateMapHeight(){
		double columnHeight = Math.floor(ApplicationContext.getScreenHeight());
		return new Double(Math.min(columnHeight, 640.0)).intValue();
	}

	public final MapCoordinatesCalculator getCalculator() {
		return calculator;
	}

	public final void setCalculator(MapCoordinatesCalculator calculator) {
		this.calculator = calculator;
	}

	public final StaticMap getMap() {
		return map;
	}

	public final void setMap(StaticMap map) {
		this.map = map;
	}
	
	public Image getMapImage(){
		return new Image(getMap().getMapAddress(), getMap().getWidth(), getMap().getHeight(), false, false);
	}
	
	public final Image reloadMap(){
		if(map.getCenter() == null){
			map.setCenter(getUserPosition());
		}
		if(calculator == null){
			calculator = new MapCoordinatesCalculator(map.getCenter(), map.getZoom(), map.getWidth(), map.getHeight());
		}
		return getMapImage();
	}

	public final Cords getUserPosition() {
		return userPosition;
	}

	public final void setUserPosition(Cords userPosition) {
		this.userPosition = userPosition;
	}

	public final List<Cords> getLastPath() {
		return lastPath;
	}

	public final void setLastPath(List<Cords> lastPath) {
		this.lastPath = lastPath;
	}

	public final boolean isEnableDraw() {
		return enableDraw;
	}
}
