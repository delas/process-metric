package it.processmining.clustering.hierarchical;

import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import it.processmining.clustering.metric.JaccardDistance;
import it.processmining.clustering.model.process.SetRepresentation;
import it.processmining.clustering.ui.Coordinates;
import it.processmining.clustering.ui.DendrogramWidget;
import it.processmining.clustering.utils.Utils;

public class Cluster {

	private static Integer globalId = 1;
	
	private Integer id;
	private Integer maxDepth = 0;
	private Integer minDepth = 0;
	private SetRepresentation element = null;
	private Cluster leftChild = null;
	private Cluster rightChild = null;
	
	private DecimalFormat df = new DecimalFormat("#.##");
	
	
	public Cluster(SetRepresentation element) {
		id = globalId++;
		maxDepth = minDepth = 0;
		addElement(element);
	}
	
	
	public Cluster(Cluster childLeft, Cluster childRight) {
		id = globalId++;
		setChildren(childLeft, childRight);
		maxDepth = 1 + Math.max(childLeft.getMaxDepth(), childRight.getMaxDepth());
		minDepth = 1 + Math.min(childLeft.getMaxDepth(), childRight.getMaxDepth());
	}
	
	
	public void addElement(SetRepresentation element) {
		this.element = element;
	}
	
	
	public void setChildren(Cluster childLeft, Cluster childRight) {
		this.leftChild = childLeft;
		this.rightChild = childRight;
	}
	
	
	public Cluster[] getChildren() {
		return new Cluster[]{leftChild, rightChild};
	}
	
	
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
	 * @param alpha the convex combinator
	 * @return the distance between the medoid of the two clusters
	 */
	public Double getDistance(Cluster c, double alpha) {
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
	
	
//	/**
//	 * This method recalculates the medoid of the cluster
//	 */
//	private void recalculateMedoid() {
//		if (cluster != null && cluster.size() > 0) {
//			if (cluster.size() == 1 || cluster.size() == 2) {
//				medoid = cluster.iterator().next();
//			} else {
//				Double minDistance = 2.0;
//				for (SetRepresentation e1 : cluster) {
//					for (SetRepresentation e2 : cluster) {
//						if (!e1.equals(e2)) {
//							
//						}
//					}
//				}
//			}
//		}
//	}
	
	
	public Coordinates drawDendrogram(DendrogramWidget dw, Graphics2D g, double alpha) {
		
		if (getMaxDepth() == 0) {
			
			// we are on a leaf, just draw the dot
			return new Coordinates(dw.askForX(), dw.askForY(element));
			
		} else {
			
			// we are on the central body of the dendrogram
			
			Coordinates left = leftChild.drawDendrogram(dw, g, alpha);
			Coordinates right= rightChild.drawDendrogram(dw, g, alpha);
			
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
			Double clusterDistance = leftChild.getDistance(rightChild, alpha);
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
