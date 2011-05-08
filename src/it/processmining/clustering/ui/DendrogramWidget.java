package it.processmining.clustering.ui;

import it.processmining.clustering.hierarchical.Cluster;
import it.processmining.clustering.hierarchical.DistanceMatrix;
import it.processmining.clustering.model.process.SetRepresentation;

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
import java.text.DecimalFormat;
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
	public static int dendroMaxLineLength = 100;
	// minimum length of connectors
	public static final int dendroMinLineLength = 5;
	// colors configuration
	public static final Color background = Color.BLACK;
	public static final Color labelColor = new Color(172, 229, 254, (int) (255*(.8)));
	public static final Color dendroColor = Color.WHITE;
	public static final Color matrixBrightestColor = Color.RED;
	public static final Color infoBoxBackground = new Color(.05f, .05f, .05f, .9f);
	public static final Color infoBoxLines = new Color(.5f, .5f, .5f, .5f);
	public static final Color infoBoxLabels = new Color(1f, 1f, 1f, 0.5f);
	
	
	// internal elements
	private Cluster root;
	private DistanceMatrix dm;
	private int numberOfElements;
	private Vector<Integer> coordinates;
	private FontMetrics fm;
	
	private int currentX;
	private int currentY;
	
	private int offsetX = 10;
	private int offsetY = 10;
	
	private int spaceForLabelX = 100;
	private int spaceForLabelY = 100;
	
	DecimalFormat df = new DecimalFormat("#.###");
	
	// mouse listener indexes
	private int movingX = -1;
	private int movingY = -1;
	private int mouseMovingX = -1;
	private int mouseMovingY = -1;
	private int matrixBeginX = offsetX + spaceForLabelX;
	private int matrixBeginY = offsetY + spaceForLabelY;
	private boolean mouseOverMatrix = false;
	
	
	public DendrogramWidget(DistanceMatrix dm, Cluster root) {
		this.dm = dm;
		this.root = root;
		this.numberOfElements = dm.getElements().size();
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
		
		currentX = offsetX + spaceForLabelX + matrixBlockSize * numberOfElements;
		currentY = offsetY + spaceForLabelY + matrixBlockSize / 2;
		
		// general declaration
		int width = getWidth();
		int height = getHeight();
		int sizeOfMatrix = numberOfElements*matrixBlockSize;
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// draw background
		g2d.setColor(background);
		g2d.fillRect(0, 0, width, height);
		
		// paint of the dendrogram
		g2d.setColor(Color.WHITE);
		root.drawDendrogram(this, g2d);

		// draw the matrix
		int x = 0;
		int y = 0;
		for(int a = 0; a < numberOfElements; a++) {
			y = 0;
			for(int b = 0; b < numberOfElements; b++) {
				Double distanceValue = dm.getValue(coordinates.get(a), coordinates.get(b));
				g2d.setColor(measureColor(distanceValue));
				g2d.fillRect(offsetX + spaceForLabelX + x, offsetY + spaceForLabelY + y, matrixBlockSize, matrixBlockSize);
				y += matrixBlockSize;
			}
			x += matrixBlockSize;
		}
		
		// draw the labels
//		g.setColor(Color.GREEN);
		g.setColor(new Color(172, 229, 254, (int) (255*(.8))));
		for(int i = 0; i < numberOfElements; i++) {
			String s = dm.getElements().get(coordinates.get(i)).getName();
			int internalOffsetX = spaceForLabelX - fm.stringWidth(s) - 5;
			int internalOffsetY = spaceForLabelY - 5;
			// horizontal labels
			g.drawString(s, offsetX + internalOffsetX, (int) (offsetY + spaceForLabelY + (matrixBlockSize/2) + 5 + (i*matrixBlockSize)));
			// vertical labels
//			g2d.rotate(Math.PI*3/2);
//			g.drawString(s, -(offsetY + internalOffsetY), (int) (offsetX + spaceForLabelX + (matrixBlockSize/2) + (i*matrixBlockSize) + 5));
//			g2d.rotate(Math.PI/2);
		}
		
		// paint info box
		if (mouseOverMatrix) {
			int xCoord = (mouseMovingX - matrixBeginX) / matrixBlockSize;
			int yCoord = (mouseMovingY - matrixBeginY) / matrixBlockSize;
			
			g2d.setColor(infoBoxLines);
			// oval on the cell
			g2d.drawOval(
					matrixBeginX + (xCoord*matrixBlockSize) + (matrixBlockSize/2) - 5,
					matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize/2) - 5,
					10, 10);
			// connector from the matrix border to the oval
			g2d.drawLine(
					matrixBeginX,
					matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize/2),
					matrixBeginX + (xCoord*matrixBlockSize) + (matrixBlockSize/2) - 6,
					matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize/2));
			// connector from the oval to the infobox
			g2d.drawLine(
					matrixBeginX + (xCoord*matrixBlockSize) + (matrixBlockSize/2) + 4,
					matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize/2) + 4,
					(int) (matrixBeginX + (xCoord*matrixBlockSize) + (matrixBlockSize*.75) + 2),
					(int) (matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize*.75) + 2));
			
			if (xCoord != yCoord) {
				// second oval
				g2d.drawOval(
						matrixBeginX + (yCoord*matrixBlockSize) + (matrixBlockSize/2) - 5,
						matrixBeginY + (xCoord*matrixBlockSize) + (matrixBlockSize/2) - 5,
						10, 10);
				// connector between the two ovals
				int mult = (xCoord > yCoord)? -1 : 1;
				g2d.drawLine(
						matrixBeginX + (xCoord*matrixBlockSize) + (matrixBlockSize/2) + (4*mult),
						matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize/2) - (4*mult),
						matrixBeginX + (yCoord*matrixBlockSize) + (matrixBlockSize/2) - (4*mult),
						matrixBeginY + (xCoord*matrixBlockSize) + (matrixBlockSize/2) + (4*mult));
				// connector from the second oval to the matrix border
				g2d.drawLine(
						matrixBeginX,
						matrixBeginY + (xCoord*matrixBlockSize) + (matrixBlockSize/2),
						matrixBeginX + (yCoord*matrixBlockSize) + (matrixBlockSize/2) - 6,
						matrixBeginY + (xCoord*matrixBlockSize) + (matrixBlockSize/2));
			}
			
			int infoBoxOffset = (xCoord*matrixBlockSize);
			Double dis = dm.getValue(coordinates.get(xCoord), coordinates.get(yCoord));
			String textLine1 = "Similarity: " + df.format(1-dis);
			String textLine2 = "Distance: " + df.format(dis);
			int infoBoxWidth = fm.stringWidth(textLine1);
			if (fm.stringWidth(textLine2) > infoBoxWidth) infoBoxWidth = fm.stringWidth(textLine2);
			
			// the actual info box
			g.setColor(infoBoxBackground);
			g2d.fillRoundRect(
					(int) (matrixBeginX + infoBoxOffset + (matrixBlockSize*.75)),
					(int) (matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize*.75)),
					infoBoxWidth + 20, 38, 10, 10);
			g2d.setColor(infoBoxLines);
			g2d.drawRoundRect(
					(int) (matrixBeginX + infoBoxOffset + (matrixBlockSize*.75)),
					(int) (matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize*.75)),
					infoBoxWidth + 20, 38, 10, 10);
			
			// texts inside the info box
			g.setColor(infoBoxLabels);
			g2d.drawString(textLine1, 
					(int) (matrixBeginX + infoBoxOffset + (matrixBlockSize*.75) + 10), 
					(int) (matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize*.75) + 17));
			g2d.drawString(textLine2, 
					(int) (matrixBeginX + infoBoxOffset + (matrixBlockSize*.75) + 10), 
					(int) (matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize*.75) + 32));
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
//		Color c = new Color(Color.HSBtoRGB(0f, 0.99f, 1-measure.floatValue()));
//		Color c = new Color(172, 229, 254, (int) (255*(1-measure)));
		Color c = new Color(matrixBrightestColor.getRed(), matrixBrightestColor.getGreen(), matrixBrightestColor.getBlue(), (int) (255*(1-measure)));
		return c;
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		mouseMovingX = e.getX();
		mouseMovingY = e.getY();
		
		mouseOverMatrix = (
				mouseMovingX > matrixBeginX &&
				mouseMovingX < (matrixBeginX + matrixBlockSize*numberOfElements) &&
				mouseMovingY > matrixBeginY &&
				mouseMovingY < (matrixBeginY + matrixBlockSize*numberOfElements));
		
		repaint();
	}


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
		matrixBeginX = offsetX + spaceForLabelX;
		matrixBeginY = offsetY + spaceForLabelY;
		mouseOverMatrix = false;
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
		
		matrixBeginX = offsetX + spaceForLabelX;
		matrixBeginY = offsetY + spaceForLabelY;
		mouseOverMatrix = false;
		
		repaint();
	}

}
