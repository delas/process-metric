package it.processmining.clustering.ui;

import it.processmining.clustering.hierarchical.Cluster;
import it.processmining.clustering.hierarchical.DistanceMatrix;
import it.processmining.metric.processrepresentation.SetRepresentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.sql.Blob;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;

/**
 *
 * @author Andrea Burattin
 */
@SuppressWarnings("serial")
public class DendrogramWidget extends JComponent implements MouseListener,
	MouseMotionListener, MouseWheelListener {
	
	// GRAPHIC CONFIGURATION
	// ---------------------
	// the size of a block of the matrix
	public static int matrixBlockSize = 40;
	// the size of a circle of the dendrogram
	public static final int dendroCircleSize = 5;
	// maximum length of connectors
	public static int dendroMaxLineLength = 50;
	// minimum length of connectors
	public static final int dendroMinLineLength = 5;
	
	// internal elements
	private Cluster root;
	private DistanceMatrix dm;
	private Vector<Integer> coordinates;
	private FontMetrics fm;
	
	private int currentX;
	private int currentY;
	
	private int offsetX = 10;
	private int offsetY = 10;
	
	private int spaceForLabelX = 100;
	private int spaceForLabelY = 100;
	
	// mouse listener indexes
	private int movingX = -1;
	private int movingY = -1;
	
	public DendrogramWidget(DistanceMatrix dm, Cluster root) {
		this.dm = dm;
		this.root = root;
		coordinates = new Vector<Integer>();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	
	public int askForX() {
		return currentX;
	}
	
	
	public int askForY(SetRepresentation c) {
		int index = dm.getIndexOfElements(c);
		if (!coordinates.contains(index)) {
			coordinates.add(index);
		}
		
		int oldY = currentY;
		currentY += matrixBlockSize;
		return oldY;
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		if (fm == null) {
			fm = g.getFontMetrics();
		}
		
		int size = dm.getElements().size();
		currentX = offsetX + spaceForLabelX + matrixBlockSize * size;
		currentY = offsetY + spaceForLabelY + matrixBlockSize / 2;
		
		// general declaration
		int width = getWidth();
		int height = getHeight();
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// draw background
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, width, height);
		
		// paint of the dendrogram
		g2d.setColor(Color.WHITE);
		root.drawDendrogram(this, g2d);

		// draw the matrix
		int x = 0;
		int y = 0;
		for(int a = 0; a < size; a++) {
			y = 0;
			for(int b = 0; b < size; b++) {
				Double distanceValue = dm.getValue(coordinates.get(a), coordinates.get(b));
				g2d.setColor(measureColor(distanceValue));
				g2d.fillRect(offsetX + spaceForLabelX + x, offsetY + spaceForLabelY + y, matrixBlockSize, matrixBlockSize);
				y += matrixBlockSize;
			}
			x += matrixBlockSize;
		}
		
		// draw the labels
		g.setColor(Color.GREEN);
		for(int i = 0; i < size; i++) {
			String s = dm.getElements().get(coordinates.get(i)).getName();
			int internalOffsetX = spaceForLabelX - fm.stringWidth(s) - 5;
			int internalOffsetY = spaceForLabelY - 5;
			
			g.drawString(s, offsetX + internalOffsetX, (int) (offsetY + spaceForLabelY + (matrixBlockSize/2) + 5 + (i*matrixBlockSize)));
			
			g2d.rotate(Math.PI*3/2);
			g.drawString(s, -(offsetY + internalOffsetY), (int) (offsetX + spaceForLabelX + (matrixBlockSize/2) + (i*matrixBlockSize)));
			g2d.rotate(Math.PI/2);
		}
		
		g2d.dispose();

	}


	/**
	 * This method extracts the color from the current cell measure
	 * 
	 * @param measure the current measure (must be between 0 and 1)
	 * @return the color object associated with the measure
	 */
	private Color measureColor(Double measure) {
		Color c = new Color(Color.HSBtoRGB(0f, 0.9f, 1-measure.floatValue()));
		return c;
	}


	@Override
	public void mouseClicked(MouseEvent e) {}


	@Override
	public void mouseEntered(MouseEvent e) {}


	@Override
	public void mouseExited(MouseEvent e) {}


	@Override
	public void mousePressed(MouseEvent e) {
		movingX = e.getX();
		movingY = e.getY();
	}


	@Override
	public void mouseReleased(MouseEvent e) {}


	@Override
	public void mouseDragged(MouseEvent e) {
		offsetX += e.getX() - movingX;
		offsetY += e.getY() - movingY;
		repaint();
		movingX = e.getX();
		movingY = e.getY();
	}


	@Override
	public void mouseMoved(MouseEvent e) {}


	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		matrixBlockSize += e.getWheelRotation();
		dendroMaxLineLength += e.getWheelRotation();
		
		if (matrixBlockSize < 20 || matrixBlockSize > 200 || dendroMaxLineLength < 20 || dendroMaxLineLength > 200) {
			if (matrixBlockSize < 20) {
				matrixBlockSize = 20;
			}
			if (matrixBlockSize > 200) {
				matrixBlockSize = 200;
			}
			if (dendroMaxLineLength < 20) {
				dendroMaxLineLength = 20;
			}
			if (dendroMaxLineLength > 200) {
				dendroMaxLineLength = 200;
			}
			return;
		}
		repaint();
	}

}
