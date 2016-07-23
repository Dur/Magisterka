package com.dur.client.model;


public class StaticMap extends Map {
	
	private final String BASE_ADDRESS = "https://maps.googleapis.com/maps/api/staticmap?";
	
	
	public StaticMap(int width, int height, int zoom, Cords center, MapType mapType) {
		super(height, width, zoom, center, mapType);
	}
	
	public String getMapAddress(){
		StringBuilder builder = new StringBuilder(BASE_ADDRESS);
		builder.append("center=" + this.getCenter().getLatitude() + "," + this.getCenter().getLongitude() + "&" );
		builder.append("maptype=" + this.getMapType() + "&");
		builder.append("zoom=" + this.getZoom() + "&");
		builder.append("size=" + this.getWidth() +  "x" + this.getHeight());
		if(this.getMarkers() != null){
			for(Marker marker : this.getMarkers()){
				builder.append(marker.toString());
			}
		}
		return builder.toString();
	}
}
