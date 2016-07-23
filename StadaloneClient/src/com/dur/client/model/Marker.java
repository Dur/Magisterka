package com.dur.client.model;

/**
 * Class represents GoogleMaps Marker.
 * Has name, Description and coordinates 
 * @author ddr
 *
 */
public class Marker {
	private Cords cords;
	private String name;
	private String label;
	private Color color;
	private Size size;

	public Marker(Cords cords, String name) {
		super();
		this.cords = cords;
		this.name = name;
		this.label = name;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder("&markers=");
		builder.append("color:" + (color != null ? color : Color.red) + "|" );
		builder.append("label:" + (label != null ? label.toUpperCase().substring(0, 1) : "S") + "|" );
		builder.append("size:" + (size != null ? size : Size.mid) + "|" );
		builder.append(cords.getLatitude() + "," + cords.getLongitude());
		return builder.toString();
	}
	
	public final Cords getCords() {
		return cords;
	}
	public final void setCords(Cords cords) {
		this.cords = cords;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	
	public final String getLabel() {
		return label;
	}

	public final void setLabel(String label) {
		this.label = label;
	}

	public final Color getColor() {
		return color;
	}

	public final void setColor(Color color) {
		this.color = color;
	}

	public final Size getSize() {
		return size;
	}

	public final void setSize(Size size) {
		this.size = size;
	}

	
	public enum Color{
		black, brown, green, purple, yellow, blue, gray, orange, red, white;
	}
	
	public enum Size{
		tiny, mid, small
	}

}
