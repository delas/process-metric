package it.processmining.clustering.ui;

/**
 * Generic class useful to describe one point in the euclidean space
 * 
 * @author Andrea Burattin
 * @version 0.1
 */
public class Coordinates {
	
	int x;
	int y;
	
	
	/**
	 * Class constructor
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	/**
	 * Get the X coordinate
	 * 
	 * @return the x coordinate
	 */
	public int getX() {
		return x;
	}
	
	
	/**
	 * Get the Y coordinate
	 * 
	 * @return the y coordinate
	 */
	public int getY() {
		return y;
	}
	
	
	/**
	 * Set the X coordinate
	 * 
	 * @param x the x coordinate
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	
	/**
	 * Set the Y coordinate
	 * @param y the y coordinate
	 */
	public void setY(int y) {
		this.y = y;
	}
}
