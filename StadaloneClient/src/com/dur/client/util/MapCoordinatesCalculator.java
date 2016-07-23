package com.dur.client.util;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.model.Cords;
import com.dur.client.model.PointF;

public class MapCoordinatesCalculator {

	private final Log log = LogFactory.getLog(MapCoordinatesCalculator.class);
	private final int TILE_SIZE = 256;
	private final double OUT_OF_RANGE_PERCENT = 0.2;
	private final PointF pixelOrigin = new PointF(TILE_SIZE / 2.0, TILE_SIZE / 2.0);
	private final double pixelsPerLonDegree = TILE_SIZE / 360.0;
	private final double pixelsPerLonRadian = TILE_SIZE / (2 * Math.PI);
	private PointF mapCenter;
	private PointF upperLeftCorner;
	private PointF bottomRightCorner;
	private double maxX;
	private double minX;
	private double maxY;
	private double minY;
	private int zoom;

	public MapCoordinatesCalculator(Cords center, int zoom, int width, int heigth){
		this.zoom = zoom;
		mapCenter = fromCordsToPoint(center.getLatitude(), center.getLongitude(), zoom);
		upperLeftCorner = new PointF(mapCenter.x - (width / 2), mapCenter.y - (heigth / 2));
		bottomRightCorner = new PointF(mapCenter.x + (width / 2), mapCenter.y + (heigth / 2));
		Cords bottomRightCords = fromPointToCords(bottomRightCorner, zoom);
		Cords upperLeftCords = fromPointToCords(upperLeftCorner, zoom);
		double deltaY = upperLeftCords.getLatitude() - bottomRightCords.getLatitude();
		double deltaX = bottomRightCords.getLongitude() - upperLeftCords.getLongitude();
		maxX =  bottomRightCords.getLongitude() - (deltaX * OUT_OF_RANGE_PERCENT);
		maxY =  upperLeftCords.getLatitude() - (deltaY * OUT_OF_RANGE_PERCENT);
		minX =  upperLeftCords.getLongitude() + (deltaX * OUT_OF_RANGE_PERCENT);
		minY =  bottomRightCords.getLatitude() + (deltaY * OUT_OF_RANGE_PERCENT);
		log.info("##### Upper left corner: " + fromPointToCords(upperLeftCorner, zoom));
		log.info("##### Bottom right corner: " + fromPointToCords(bottomRightCorner, zoom));
		log.info("##### maxX " + maxX + " maxY " + maxY + " minX " + minX + " minY " + minY );
	}
	
	public List<PointF> transformToPoints(List<Cords> cords){
		LinkedList<PointF> points = new LinkedList<>(); 
		for(Cords cord : cords){
			points.add(transformToPoint(cord));
		}
		return points;
	}
	
	public List<Cords> transformToCords(List<PointF> points){
		LinkedList<Cords> cords = new LinkedList<>();
		for(PointF point : points){
			cords.add(transformToCords(point));
		}
		return cords;
	}
	
	public PointF transformToPoint(Cords cords){
		return denormalizePoint(fromCordsToPoint(cords.getLatitude(), cords.getLongitude(), zoom));
	}
	
	public Cords transformToCords(PointF point){
		return fromPointToCords(normalizePoint(point), zoom);
	}
	
	public boolean isPointTooFarFromCenter(Cords cords){
		return cords.getLongitude() > maxX || cords.getLongitude() < minX || cords.getLatitude() > maxY || cords.getLatitude() < minY;
	}
	
	private PointF normalizePoint(PointF point){
		return new PointF(point.x + upperLeftCorner.x, point.y + upperLeftCorner.y);
	}
	
	private PointF denormalizePoint(PointF point){
		return new PointF(point.x - upperLeftCorner.x,  point.y - upperLeftCorner.y);
	}

	private double bound(double val, double valMin, double valMax) {
		double res;
        res = Math.max(val, valMin);
        res = Math.min(res, valMax);
        return res;
	}

	private double degreesToRadians(double deg) {
		return deg * (Math.PI / 180);
	}

	private double radiansToDegrees(double rad) {
		return rad / (Math.PI / 180);
	}

	private PointF fromCordsToPoint(double lat, double lng, int zoom) {
		PointF point = new PointF(0, 0);
		point.x = pixelOrigin.x + lng * pixelsPerLonDegree;
		// Truncating to 0.9999 effectively limits latitude to 89.189. This is
		// about a third of a tile past the edge of the world tile.
		double siny = bound(Math.sin(degreesToRadians(lat)), -0.9999, 0.9999);
		point.y = pixelOrigin.y + 0.5 * Math.log((1 + siny) / (1 - siny)) * -pixelsPerLonRadian;
		int numTiles = 1 << zoom;
		point.x = point.x * numTiles;
		point.y = point.y * numTiles;
		return point;
	}

	private Cords fromPointToCords(PointF point, int zoom) {
		int numTiles = 1 << zoom;
		PointF newPoint = new PointF(point.x / numTiles, point.y / numTiles);
		double lng = (newPoint.x - pixelOrigin.x) / pixelsPerLonDegree;
		double latRadians = (newPoint.y - pixelOrigin.y) / - pixelsPerLonRadian;
		double lat = radiansToDegrees(2 * Math.atan(Math.exp(latRadians)) - Math.PI / 2);
		return new Cords(lat, lng);
	}
}