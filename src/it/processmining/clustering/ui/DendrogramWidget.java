package it.processmining.clustering.ui;

import it.processmining.clustering.hierarchical.Cluster;
import it.processmining.clustering.hierarchical.DistanceMatrix;
import it.processmining.clustering.model.process.SetRepresentation;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JComponent;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

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
//	public static int dendroMaxLineLength = 100;
	public static int dendroWidth = 400;
	// minimum length of connectors
//	public static final int dendroMinLineLength = 5;
	// colors configuration
	public static final Color background = Color.BLACK;
	public static final Color labelColor = new Color(172, 229, 254, (int) (255*(.8)));
	public static final Color dendroColor = Color.WHITE;
	public static final Color matrixBrightestColor = Color.RED;
	public static final Color infoBoxBackground = new Color(.05f, .05f, .05f, .9f);
	public static final Color infoBoxLines = new Color(.5f, .5f, .5f, .5f);
	public static final Color infoBoxLabels = new Color(1f, 1f, 1f, 0.5f);
	public static final Color scaleColor = new Color(1f, 1f, 1f, 0.5f);
	
	public static final Color infoDendrogramBackground = new Color(.1f, .1f, .1f, 1f);
	public static final Color infoDendrogramLines =      new Color(.4f, .4f, .4f, 1f);
	public static final Color infoDendrogramLabels =     new Color(.6f, .6f, .6f, 1f);
	public static final float infoDendrogramFontSize = 16f;
	
	
	// internal elements
	private Cluster root;
	private DistanceMatrix dm;
	private int numberOfElements;
	private Vector<Integer> coordinates;
	private FontMetrics fm;
	private double alpha;
	
	private int currentX;
	private int currentY;
	
	private int offsetX = 10;
	private int offsetY = 10; 
	
	private int spaceForLabelX = 100;
	private int spaceForLabelY = 0;
	
	DecimalFormat df = new DecimalFormat("#.###");
	
	// mouse listener indexes
	private int mouseMovingX = -1;
	private int mouseMovingY = -1;
	private int matrixBeginX = offsetX + spaceForLabelX;
	private int matrixBeginY = offsetY + spaceForLabelY;
	private boolean mouseOverMatrix = false;
	private boolean mouseOverDendrogram = false;
	private int motionPixels = 0;
	
	
	public DendrogramWidget(DistanceMatrix dm, Cluster root, double alpha) {
		this.dm = dm;
		this.root = root;
		this.numberOfElements = dm.getElements().size();
		this.alpha = alpha;
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
	
	
	public int getMatrixBorderN() {
		return offsetY + spaceForLabelY;
	}
	
	
	public int getMatrixBorderE() {
		return offsetX + spaceForLabelX + (numberOfElements*matrixBlockSize);
	}
	
	
	public int getMatrixBorderS() {
		return offsetY + spaceForLabelY + (numberOfElements*matrixBlockSize);
	}
	
	
	public int getMatrixBorderW() {
		return offsetX + spaceForLabelX;
	}
	
	
	public void getSVG(String filename) {
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		paintComponent(svgGenerator);
		
		boolean useCSS = true;
		try {
			svgGenerator.stream(filename, useCSS);
		} catch (SVGGraphics2DIOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		if (fm == null) {
			fm = g.getFontMetrics();
		}
		
		currentX = offsetX + spaceForLabelX + matrixBlockSize * numberOfElements;
		currentY = offsetY + spaceForLabelY + matrixBlockSize / 2;
	
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		drawBackground(g, getWidth(), getHeight());
		drawDendrogram(g);
		drawMatrix(g);
		
		if (mouseOverDendrogram) {
			drawOverlayDendrogram(g);
		}
		
		if (mouseOverMatrix) {
			drawOverlayMatrix(g);
		}
		
		g.dispose();
 
	}
	
	
	private void drawOverlayDendrogram(Graphics g) {
		float defaultFontSize = g.getFont().getSize();
	    g.setFont(g.getFont().deriveFont(infoDendrogramFontSize));
	    
		Double value = (double) ((double)(mouseMovingX - getMatrixBorderE()) / dendroWidth);
		String valueString = df.format(value);
		
		g.setColor(infoDendrogramBackground);
		g.fillRoundRect(
				mouseMovingX,
				getMatrixBorderN() - 30,
				fm.stringWidth(valueString) + 30, 30, 10, 10);
		
		g.setColor(infoDendrogramLines);
		g.drawLine(mouseMovingX, getMatrixBorderN() - 3, mouseMovingX, getMatrixBorderS());
		g.drawRoundRect(
				mouseMovingX,
				getMatrixBorderN() - 30,
				fm.stringWidth(valueString) + 30, 30, 10, 10);
		
		g.setColor(infoDendrogramLabels);
		g.drawString(valueString,
				mouseMovingX + 10,
				getMatrixBorderN() - 8);
		g.setFont(g.getFont().deriveFont(defaultFontSize));
	}
	
	
	private void drawOverlayMatrix(Graphics g) {
		int xCoord = (mouseMovingX - matrixBeginX) / matrixBlockSize;
		int yCoord = (mouseMovingY - matrixBeginY) / matrixBlockSize;
		
		g.setColor(infoBoxLines);
		// oval on the cell
		g.drawOval(
				matrixBeginX + (xCoord*matrixBlockSize) + (matrixBlockSize/2) - 5,
				matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize/2) - 5,
				10, 10);
		// connector from the matrix border to the oval
		g.drawLine(
				matrixBeginX,
				matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize/2),
				matrixBeginX + (xCoord*matrixBlockSize) + (matrixBlockSize/2) - 6,
				matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize/2));
		// connector from the oval to the infobox
		g.drawLine(
				matrixBeginX + (xCoord*matrixBlockSize) + (matrixBlockSize/2) + 4,
				matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize/2) + 4,
				(int) (matrixBeginX + (xCoord*matrixBlockSize) + (matrixBlockSize*.75) + 2),
				(int) (matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize*.75) + 2));
		
		if (xCoord != yCoord) {
			// second oval
			g.drawOval(
					matrixBeginX + (yCoord*matrixBlockSize) + (matrixBlockSize/2) - 5,
					matrixBeginY + (xCoord*matrixBlockSize) + (matrixBlockSize/2) - 5,
					10, 10);
			// connector between the two ovals
			int mult = (xCoord > yCoord)? -1 : 1;
			g.drawLine(
					matrixBeginX + (xCoord*matrixBlockSize) + (matrixBlockSize/2) + (4*mult),
					matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize/2) - (4*mult),
					matrixBeginX + (yCoord*matrixBlockSize) + (matrixBlockSize/2) - (4*mult),
					matrixBeginY + (xCoord*matrixBlockSize) + (matrixBlockSize/2) + (4*mult));
			// connector from the second oval to the matrix border
			g.drawLine(
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
		g.fillRoundRect(
				(int) (matrixBeginX + infoBoxOffset + (matrixBlockSize*.75)),
				(int) (matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize*.75)),
				infoBoxWidth + 20, 38, 10, 10);
		g.setColor(infoBoxLines);
		g.drawRoundRect(
				(int) (matrixBeginX + infoBoxOffset + (matrixBlockSize*.75)),
				(int) (matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize*.75)),
				infoBoxWidth + 20, 38, 10, 10);
		
		// texts inside the info box
		g.setColor(infoBoxLabels);
		g.drawString(textLine1, 
				(int) (matrixBeginX + infoBoxOffset + (matrixBlockSize*.75) + 10), 
				(int) (matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize*.75) + 17));
		g.drawString(textLine2, 
				(int) (matrixBeginX + infoBoxOffset + (matrixBlockSize*.75) + 10), 
				(int) (matrixBeginY + (yCoord*matrixBlockSize) + (matrixBlockSize*.75) + 32));
	}
	
	
	private void drawBackground(Graphics g, int width, int height) {
		g.setColor(background);
		g.fillRect(0, 0, width, height);
	}
	
	
	private void drawMatrix(Graphics g) {
		// draw the background
		g.setColor(background);
		g.fillRect(0, 0,
				spaceForLabelX + offsetX + numberOfElements*matrixBlockSize,
				spaceForLabelY + offsetY + numberOfElements*matrixBlockSize + 20);
		
		// draw the matrix
		int x = 0;
		int y = 0;
		for(int a = 0; a < numberOfElements; a++) {
			y = 0;
			for(int b = 0; b < numberOfElements; b++) {
				Double distanceValue = dm.getValue(coordinates.get(a), coordinates.get(b));
				g.setColor(measureColor(distanceValue));
				g.fillRect(getMatrixBorderW() + x, getMatrixBorderN() + y,
						matrixBlockSize, matrixBlockSize);
				y += matrixBlockSize;
			}
			x += matrixBlockSize;
		}
		
		// draw the labels
		g.setColor(new Color(172, 229, 254, (int) (255*(.8))));
		for(int i = 0; i < numberOfElements; i++) {
			String s = dm.getElements().get(coordinates.get(i)).getName();
			int internalOffsetX = spaceForLabelX - fm.stringWidth(s) - 5;
			// horizontal labels
			g.drawString(s, offsetX + internalOffsetX, (int) (offsetY + spaceForLabelY + (matrixBlockSize/2) + 5 + (i*matrixBlockSize)));
			// vertical labels
//			g2d.rotate(Math.PI*3/2);
//			g.drawString(s, -(offsetY + internalOffsetY), (int) (offsetX + spaceForLabelX + (matrixBlockSize/2) + (i*matrixBlockSize) + 5));
//			g2d.rotate(Math.PI/2);
		}
	}
	
	
	private void drawDendrogram(Graphics g) {
		// draw the background
		g.setColor(background);
		g.fillRect(spaceForLabelX + offsetX + numberOfElements*matrixBlockSize, 0,
				dendroWidth + 10,
				spaceForLabelY + offsetY + numberOfElements*matrixBlockSize + 20);
		
		// paint of the dendrogram
		root.drawDendrogram(this, g, alpha);
		
		// draw the dendrogram scale
		g.setColor(scaleColor);
		g.drawLine(
				getMatrixBorderE(),
				getMatrixBorderS(),
				getMatrixBorderE() + dendroWidth,
				getMatrixBorderS());
		for (int i = 0; i <= 10; i++) {
			String s = Double.toString(i/10.);
			g.drawLine(
					getMatrixBorderE() + (dendroWidth/10*i),
					getMatrixBorderS() + 1,
					getMatrixBorderE() + (dendroWidth/10*i),
					getMatrixBorderS() + 6);
			g.drawString(
					s,
					getMatrixBorderE() + (dendroWidth/10*i) - (fm.stringWidth(s) / 2) + 1,
					getMatrixBorderS() + 20);
		}
	}
	


	/**
	 * This method extracts the color from the current cell measure
	 * 
	 * @param measure the current measure (must be between 0 and 1)
	 * @return the color object associated with the measure
	 */
	private Color measureColor(Double measure) {
		return new Color(matrixBrightestColor.getRed(), matrixBrightestColor.getGreen(), matrixBrightestColor.getBlue(), (int) (255*(1-measure)));
	}


	@Override
	public void mouseClicked(MouseEvent e) {}


	@Override
	public void mouseEntered(MouseEvent e) {}


	@Override
	public void mouseExited(MouseEvent e) {}


	@Override
	public void mousePressed(MouseEvent e) {
		mouseMovingX = e.getX();
		mouseMovingY = e.getY();
	}


	@Override
	public void mouseReleased(MouseEvent e) {}


	@Override
	public void mouseDragged(MouseEvent e) {
		
		offsetX -= mouseMovingX - e.getX();
		offsetY -= mouseMovingY - e.getY();
		
		mouseMovingX = e.getX();
		mouseMovingY = e.getY();
		
		matrixBeginX = offsetX + spaceForLabelX;
		matrixBeginY = offsetY + spaceForLabelY;
		
		mouseOverMatrix = false;
		
		repaint();
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		if (motionPixels++ == 3) {
			mouseMovingX = e.getX();
			mouseMovingY = e.getY();
			
			mouseOverDendrogram = (
					mouseMovingX > getMatrixBorderE() &&
					mouseMovingX < (getMatrixBorderE() + dendroWidth) &&
					mouseMovingY > getMatrixBorderN() &&
					mouseMovingY < getMatrixBorderS());
			
			mouseOverMatrix = (
					mouseMovingX > getMatrixBorderW() &&
					mouseMovingX < getMatrixBorderE() &&
					mouseMovingY > getMatrixBorderN() &&
					mouseMovingY < getMatrixBorderS());
		
			if (mouseOverDendrogram || mouseOverMatrix) {
				repaint();
			}
			motionPixels = 0;
		}
	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		matrixBlockSize += e.getWheelRotation();
		
		if (matrixBlockSize < 20 || matrixBlockSize > 200) {
			if (matrixBlockSize < 20) {
				matrixBlockSize = 20;
			}
			if (matrixBlockSize > 200) {
				matrixBlockSize = 200;
			}
			return;
		}
		
		matrixBeginX = offsetX + spaceForLabelX;
		matrixBeginY = offsetY + spaceForLabelY;
		mouseOverMatrix = false;
		
		repaint();
	}

}
