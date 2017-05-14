/****************************************************************
 * Bean.java
 * Do not modify this file!
 * This file is for GUI, you don't need to read or understand this file.
 * ------------------------------
 * Licensing Information: You are free to use or extend these projects for educational purposes provided that
 * (1) you do not distribute or publish solutions, (2) you retain the notice, and (3) you provide clear attribution to UW-Madison
 * 
 * Attribute Information: The Mancala Game was developed at UW-Madison.
 * 
 * The initial project was developed by Chuck Dyer(dyer@cs.wisc.edu) and his TAs.
 * 
 * Current Version with GUI was developed by Fengan Li(fengan@cs.wisc.edu).
 * Some GUI componets are from Mancala Project in Google code.
 */


public class Bean {
	private double relativeX;
	private double relativeY;

	public Bean(double x, double y) {
		this.relativeX = x;
		this.relativeY = y;
	}

	public double getX() {
		return relativeX;
	}

	public void setX(double x) {
		this.relativeX = x;
	}

	public double getY() {
		return relativeY;
	}

	public void setY(double y) {
		this.relativeY = y;
	}

	public double distanceFrom(Bean bean, int width, int height)
	{
		double dx = 1.0 * (this.relativeX - bean.relativeX) * width;
		double dy = 1.0 * (this.relativeY - bean.relativeY) * height;
		double dist =  Math.sqrt(dx * dx + dy * dy);
		return dist;
	}

}
