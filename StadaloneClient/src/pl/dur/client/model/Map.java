package pl.dur.client.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Map {
	
	private int height;
	private int width;
	private int zoom;
	private List<Marker> markers;
	private Cords center;
	private MapType mapType;
	
	public Map(int height, int width, int zoom, Cords center, MapType mapType) {
		this.height = height;
		this.width = width;
		this.zoom = zoom;
		this.center = center;
		this.mapType = mapType;
		this.markers = new ArrayList<Marker>();
	}
	
	public final int getHeight() {
		return Math.min(640, height);
	}
	public final void setHeight(int height) {
		this.height = height;
	}
	public final int getWidth() {
		return Math.min(640, width);
	}
	public final void setWidth(int width) {
		this.width = width;
	}
	public final int getZoom() {
		return zoom;
	}
	public final void setZoom(int zoom) {
		this.zoom = zoom;
	}
	public final List<Marker> getMarkers() {
		return markers;
	}
	public final void setMarkers(List<Marker> markers) {
		this.markers = markers;
	}
	public final Cords getCenter() {
		return center;
	}
	public final void setCenter(Cords center) {
		this.center = center;
	}
	public final MapType getMapType() {
		return mapType;
	}
	public final void setMapType(MapType mapType) {
		this.mapType = mapType;
	}
	
	
	public enum MapType {
		roadmap, satellite, terrain, hybrid;
	}
}

