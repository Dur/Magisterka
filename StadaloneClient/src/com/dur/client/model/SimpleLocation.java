package com.dur.client.model;

public class SimpleLocation {
	
	private Cords cords;
	private String name;

	@Override
	public String toString() {
		return name;
	}

	public SimpleLocation(Cords cords, String name) {
		this.cords = cords;
		this.name = name;
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
}
