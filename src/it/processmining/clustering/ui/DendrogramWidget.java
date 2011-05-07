package it.processmining.clustering.ui;

import it.processmining.clustering.hierarchical.Cluster;
import it.processmining.clustering.hierarchical.DistanceMatrix;
import it.processmining.metric.processrepresentation.SetRepresentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;

/**
 *
 * @author Andrea Burattin
 */
@SuppressWarnings("serial")
public class DendrogramWidget extends JComponent {
	
	// GRAPHIC CONFIGURATION
	// ---------------------
	// the size of a block of the matrix
	public static final int matrixBlockSize = 40;
	// the size of a circle of the dendrogram
	public static final int dendroCircleSize = 5;
	// maximum length of connectors
	public static final int dendroMaxLineLength = 50;
	// minimum length of connectors
	public static final int dendroMinLineLength = 10;
	
	// internal elements
	private Cluster root;
	private DistanceMatrix dm;
	private Vector<Integer> coordinates;
	
	private int currentX;
	private int currentY;
	
	
	
	public DendrogramWidget(DistanceMatrix dm, Cluster root) {
		this.dm = dm;
		this.root = root;
		coordinates = new Vector<Integer>();
	}
	
	
	public int askForX() {
		return currentX;
	}
	
	
	public int askForY(SetRepresentation c) {
		int index = dm.getIndexOfElements(c);
		if (!coordinates.contains(index)) {
			coordinates.add(index);
		}
		System.out.println(coordinates);
		
		int oldY = currentY;
		currentY += matrixBlockSize;
		return oldY;
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		int size = dm.getElements().size();
		currentX = matrixBlockSize * size;
		currentY = matrixBlockSize / 2;
		// coordinates calculation for the single elements
//		coordinates.clear();
//		for (int i = 0; i < dm.getElements().size(); i++) {
//			coordinates.put(dm.getElements().elementAt(i), new Coordinates(0, currentY));
//			currentY += matrixBlockSize;
//		}
		
		// general declaration
		int width = getWidth();
		int height = getHeight();
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// draw background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);
		
		BufferedImage matrixBuffer;
		matrixBuffer = new BufferedImage(size * matrixBlockSize, size * matrixBlockSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D gBuf = matrixBuffer.createGraphics();

		
		// draw the labels
//		x = (blockSize * size) + 5;
//		y = blockSize - 5;
//		g2d.setColor(Color.BLACK);
//		for (int a = 0; a < size; a++) {
//			g2d.drawString(dm.getElements().get(a).getName(), x, y);
//			y += blockSize;
//		}
		
		// paint of the dendrogram
		g2d.setColor(Color.BLACK);
		Coordinates rootStart = root.drawDendrogram(this, g2d);
//		g2d.fillOval(rootStart.getX()-(dendroCircleSize/2), rootStart.getY()-(dendroCircleSize/2), dendroCircleSize, dendroCircleSize);

		// draw the matrix
		int x = 0;
		int y = 0;
		for(int a = 0; a < size; a++) {
			y = 0;
			for(int b = 0; b < size; b++) {
				System.out.print(a + ":");
				System.out.print(coordinates.get(a) + " ");
				System.out.print(b + ":");
				System.out.print(coordinates.get(b) + "\t");
				Double distanceValue = dm.getValue(coordinates.get(a), coordinates.get(b));
//				Double distanceValue = dm.getValue(a, b);
				gBuf.setColor(measureColor(distanceValue));
				gBuf.fillRect(x, y, matrixBlockSize, matrixBlockSize);
				y += matrixBlockSize;
			}
			System.out.println("");
			x += matrixBlockSize;
		}
//		currentX = x;
		
		gBuf.dispose();
		g2d.drawImage(matrixBuffer, 0, 0, this);

	}


	/**
	 * This method extracts the color from the current cell measure
	 * 
	 * @param measure the current measure (must be between 0 and 1)
	 * @return the color object associated with the measure
	 */
	private Color measureColor(Double measure) {
		Color c = new Color(Color.HSBtoRGB(0f, 1-measure.floatValue(), 1f));
		return c;
	}
}
