package pl.dur.client.view.decorators;

import pl.dur.client.MainClass;
import javafx.scene.image.Image;

public class ImageDecorator {
	
	private Image image;
	private double x;
	private double y;
	private double width;
	private double height;
	private String fileName;
	
	public ImageDecorator(double x, double y, double width, double height, String fileName) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.fileName = fileName;
		image = new Image(MainClass.class.getResourceAsStream("/" + fileName), width, height, true, true);
	}
	
	
	public ImageDecorator(String fileName) {
		super();
		this.fileName = fileName;
		image = new Image(MainClass.class.getResourceAsStream("/" + fileName));
	}
	
	public final Image getImage() {
		return image;
	}
	
	public final void setImage(Image image) {
		this.image = image;
	}
	
	public final double getX() {
		return x;
	}
	
	public final void setX(double x) {
		this.x = x;
	}
	
	public final double getY() {
		return y;
	}
	
	public final void setY(double y) {
		this.y = y;
	}
	
	public final double getWidth() {
		return width;
	}
	public final void setWidth(double width) {
		this.width = width;
	}
	
	public final double getHeight() {
		return height;
	}
	
	public final void setHeight(double height) {
		this.height = height;
	}
	
	public final String getFileName() {
		return fileName;
	}
	
	public final void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
