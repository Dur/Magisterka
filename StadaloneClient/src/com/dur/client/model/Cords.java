package com.dur.client.model;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * Represents geographical coordinates of point
 * Coordinates should have at most 6 decimal places.
 * If passed coordinate have more decimal places then it's rounded to 6 decimal.
 * @author ddr
 *
 */
public class Cords {
	
	private final double latitude;
	private final double longitude;
	
	/**
	 * Coordinates should have at most 6 decimal places.
	 * If passed coordinate have more decimal places then it's rounded to 6 decimal.
	 * @param latitude
	 * @param longitude
	 */
	public Cords(double latitude, double longitude) {
		super();
		this.latitude  = BigDecimal.valueOf(latitude).setScale(6, RoundingMode.HALF_UP).doubleValue();
		this.longitude = BigDecimal.valueOf(longitude).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}


	public final double getLatitude() {
		return latitude;
	}


	public final double getLongitude() {
		return longitude;
	}
	
	@Override
	public String toString(){
		return "[" + latitude + " , " + longitude + "]";
	}

}
