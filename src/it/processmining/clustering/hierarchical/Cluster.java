package it.processmining.clustering.hierarchical;

import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import it.processmining.clustering.metric.JaccardDistance;
import it.processmining.clustering.model.process.SetRepresentation;
import it.processmining.clustering.ui.Coordinates;
import it.processmining.clustering.ui.DendrogramWidget;
import it.processmining.clustering.utils.Utils;

/**
 * A representation for cluster of processes
 * 
 * @author Andrea Burattin
 * @version 0.1
 *
 */
public class Cluster {

	private static Integer globalId = 1;
	
	@SuppressWarnings("unused") private Integer id;
	private Integer maxDepth = 0;
	private Integer minDepth = 0;
	private SetRepresentation element = null;
	private Cluster leftChild = null;
	private Cluster rightChild = null;
	private Double alpha = 0.0;
	
	private DecimalFormat df = new DecimalFormat("#.##");
	
	
	/**
	 * Constructor of a cluster, and add the first element
	 * 
	 * @param element
	 * @param alpha the convex combinator
	 */
	public Cluster(SetRepresentation element, Double alpha) {
		this.alpha = alpha;
		id = globalId++;
		maxDepth = minDepth = 0;
		addElement(element);
	}
	
	
	/**
	 * Constructor of a new cluster composed of two children (as in dendrogram
	 * representation)
	 * 
	 * @param childLeft
	 * @param childRight
	 */
	public Cluster(Cluster childLeft, Cluster childRight) {
		id = globalId++;
		setChildren(childLeft, childRight);
		maxDepth = 1 + Math.max(childLeft.getMaxDepth(), childRight.getMaxDepth());
		minDepth = 1 + Math.min(childLeft.getMaxDepth(), childRight.getMaxDepth());
	}
	
	
	/**
	 * Add a new element to the cluster
	 * 
	 * @param element
	 */
	public void addElement(SetRepresentation element) {
		this.element = element;
	}
	
	
	/**
	 * Set the two children of the current cluster
	 * 
	 * @param childLeft
	 * @param childRight
	 */
	public void setChildren(Cluster childLeft, Cluster childRight) {
		this.leftChild = childLeft;
		this.rightChild = childRight;
	}
	
	
	/**
	 * Get an array with, exactly, two elements: the left and right child 
	 * 
	 * @return
	 */
	public Cluster[] getChildren() {
		return new Cluster[]{leftChild, rightChild};
	}
	
	
	/**
	 * Get a set with all the elements of the cluster (including the children
	 * cluster)
	 * 
	 * @return
	 */
	public Set<SetRepresentation> getAllElements() {
		Set<SetRepresentation> s = new HashSet<SetRepresentation>();
		if (element != null) {
			s.add(element);
		}
		if (leftChild != null) {
			s.addAll(leftChild.getAllElements());
		}
		if (rightChild != null) {
			s.addAll(rightChild.getAllElements());
		}
		return s;
	}
	
	
	/**
	 * This method calculates the distance between two clusters as the
	 * average linkage (the mean distance between all possible pairs of nodes
	 * in the two clusters, UPGMA)
	 * 
	 * @param c the second cluster
	 * @return the distance between the medoid of the two clusters
	 */
	public Double getDistance(Cluster c) {
		Double sumDistances = 0.0;
		
		Set<SetRepresentation> currentElements = getAllElements();
		Set<SetRepresentation> otherElements = c.getAllElements();
		
		Double size = 0.0;
		for (SetRepresentation c1 : currentElements) {
			for (SetRepresentation c2 : otherElements) {
				if (!c1.equals(c2)) {
					sumDistances += JaccardDistance.getDistance(c1, c2, alpha);
					size ++;
				}
			}
		}
		
		return (sumDistances / size);
	}
	
	
	/**
	 * This method provides the depth from the current cluster to the most far
	 * leaf
	 *  
	 * @return the maximum depth
	 */
	public Integer getMaxDepth() {
		return maxDepth;
	}
	
	
	/**
	 * This method provides the depth from the current cluster to the closest
	 * leaf
	 *  
	 * @return the minimum depth
	 */
	public Integer getMinDepth() {
		return minDepth;
	}
	
	
	/**
	 * This method recalculates the medoid of the cluster
	 * 
	 * The medoid is calculated as the element that minimizes the total distance
	 * to other objects in the same cluster
	 * 
	 * @return the medoid of the current cluster
	 */
	@SuppressWarnings("unused")
	private SetRepresentation getMedoid() {
		Set<SetRepresentation> cluster = getAllElements();
		// if the cluster is empty, return null
		if (cluster.isEmpty()) {
			return null;
		}
		
		// initial values for the two temporary variables
		SetRepresentation medoid = null;
		Double currentMedoidDistance = Double.MAX_VALUE;
		
		// iterate through all the couples of processes
		for (SetRepresentation e1 : cluster) {
			Double currentDistance = 0.0;
			// calculate the distance with all the other processes
			for (SetRepresentation e2 : cluster) {
				if (!e1.equals(e2)) {
					currentDistance += JaccardDistance.getDistance(e1, e2, alpha);
				}
			}
			// the current process is actually better then the current medoid
			if (currentDistance < currentMedoidDistance) {
				currentMedoidDistance = currentDistance;
				medoid = e1;
			}
		}
		
		return medoid;
	}
	
	
	/**
	 * This method is recursively used to draw the dendrogram of the current
	 * clusters structure
	 * 
	 * @param dw the current dendrogram widget
	 * @param g the graphics where the dendrogram is supposed to be drawn
	 * @return the coordinates of the point connecting the children below the
	 * 			current cluster
	 */
	public Coordinates drawDendrogram(DendrogramWidget dw, Graphics g) {
		
		if (getMaxDepth() == 0) {
			
			// we are on a leaf, just draw the dot
			return new Coordinates(dw.askForX(), dw.askForY(element));
			
		} else {
			
			// we are on the central body of the dendrogram
			
			Coordinates left = leftChild.drawDendrogram(dw, g);
			Coordinates right= rightChild.drawDendrogram(dw, g);
			
			g.setColor(DendrogramWidget.dendroColor);
			
			int maxX = (left.getX() > right.getX())? left.getX() : right.getX();
			int minY = (left.getY() < right.getY())? left.getY() : right.getY();
			int gapY = Math.abs(left.getY() - right.getY());
			
			// fill the gaps
			if (left.getX() < maxX) {
				g.drawLine(left.getX(), left.getY(), maxX-1, left.getY());
				left.setX(maxX);
			}
			if (right.getX() < maxX) {
				g.drawLine(right.getX(), right.getY(), maxX-1, right.getY());
				right.setX(maxX);
			}
			
			// calculate the length of the line, proportional to the distance of the cluster
			Double clusterDistance = leftChild.getDistance(rightChild);
			int lineLength = dw.getMatrixBorderE() + (int) (DendrogramWidget.dendroWidth * clusterDistance) - maxX;
			
			// draw the three lines
			g.drawLine(left.getX(), left.getY(), left.getX()+lineLength, left.getY());
			g.drawLine(right.getX(), right.getY(), right.getX()+lineLength, right.getY());
			g.drawLine(maxX+lineLength, minY, maxX+lineLength, minY+gapY);
			// draw the cluster oval
			g.fillOval(maxX+lineLength-(DendrogramWidget.dendroCircleSize/2), minY+(gapY/2)-(DendrogramWidget.dendroCircleSize/2),
					DendrogramWidget.dendroCircleSize, DendrogramWidget.dendroCircleSize);
			// draw the distance of the cluster
			int below = ((minY+(gapY/2)-2) > (dw.getMatrixBorderS()+dw.getMatrixBorderN())/2)? 15 : 0;
			g.setColor(DendrogramWidget.labelColor);
			g.drawString(df.format(clusterDistance), maxX+lineLength+3, minY+(gapY/2)-2+below);
			
			return new Coordinates(maxX+lineLength, minY+(gapY/2));
		}
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cluster) {
			Cluster c = (Cluster) obj;
			return Utils.xor(element, c.element) && 
					Utils.xor(leftChild, c.leftChild) &&
					Utils.xor(rightChild, c.rightChild);
			
		}
		return false;
	}
	
	
	@Override
	public int hashCode() {
		return 1;
	}
}
