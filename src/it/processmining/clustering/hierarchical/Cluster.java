package it.processmining.clustering.hierarchical;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

import org.processmining.analysis.performance.advanceddottedchartanalysis.ui.CoordinationUtil;

import it.processmining.clustering.ui.Coordinates;
import it.processmining.clustering.ui.DendrogramWidget;
import it.processmining.metric.metric.JaccardDistance;
import it.processmining.metric.processrepresentation.SetRepresentation;
import it.processmining.utils.Utils;

public class Cluster {

	private static Integer globalId = 1;
	
	private Integer id;
	private Integer maxDepth = 0;
	private Integer minDepth = 0;
	private SetRepresentation element = null;
	private Cluster leftChild = null;
	private Cluster rightChild = null;
	
	
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
	 * @return the distance between the medoid of the two clusters
	 */
	public Double getDistance(Cluster c) {
		Double sumDistances = 0.0;
		
		Set<SetRepresentation> currentElements = getAllElements();
		Set<SetRepresentation> otherElements = c.getAllElements();
		Double sumSizes = (double) (currentElements.size() + otherElements.size());
		
		for (SetRepresentation c1 : currentElements) {
			for (SetRepresentation c2 : otherElements) {
				if (!c1.equals(c2)) {
					sumDistances += JaccardDistance.getDistance(c1, c2);
				}
			}
		}
		return (sumDistances / sumSizes);
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
	
	
	public Coordinates drawDendrogram(DendrogramWidget dw, Graphics2D gBuf) {
		
		if (getMaxDepth() == 0) {
			// we are on a leaf, just draw the dot
			int newY = dw.askForY(element);
			int newX = dw.askForX();			
			gBuf.fillOval(newX, newY-(DendrogramWidget.dendroCircleSize/2), DendrogramWidget.dendroCircleSize, DendrogramWidget.dendroCircleSize);
			return new Coordinates(newX, newY);
			
		} else {
			
			// we are on the central body of the dendrogram
			
			Coordinates left = leftChild.drawDendrogram(dw, gBuf);
			Coordinates right= rightChild.drawDendrogram(dw, gBuf);
			
			int minX = (left.getX() < right.getX())? left.getX() : right.getX();
			int maxX = (left.getX() > right.getX())? left.getX() : right.getX();
			int minY = (left.getY() < right.getY())? left.getY() : right.getY();
			int maxY = (left.getY() > right.getY())? left.getY() : right.getY();
			int gapY = Math.abs(left.getY() - right.getY());
			int gapX = Math.abs(left.getX() - right.getX());
			
			// fill the gaps
			if (left.getX() < maxX) {
				gBuf.drawLine(left.getX(), left.getY(), maxX, left.getY());
				left.setX(maxX);
			}
			if (right.getX() < maxX) {
				gBuf.drawLine(right.getX(), right.getY(), maxX, right.getY());
				right.setX(maxX);
			}
			
			// calculate the length of the line, proportional to the distance of the cluster
			Double clusterDistance = rightChild.getDistance(leftChild);
			int lineLength = DendrogramWidget.dendroMinLineLength + (int) (clusterDistance * (DendrogramWidget.dendroMaxLineLength - DendrogramWidget.dendroMinLineLength));
			
			// draw the three lines
			gBuf.drawLine(left.getX(), left.getY(), left.getX()+lineLength, left.getY());
			gBuf.drawLine(right.getX(), right.getY(), right.getX()+lineLength, right.getY());
			gBuf.drawLine(maxX+lineLength, minY, maxX+lineLength, minY+gapY);
			
			return new Coordinates(maxX+lineLength, minY+(gapY/2));
		}
	}
	
	
	protected String getDotOfSubgraph() {
		String s = "";
		
		if (element == null) {
			s += id + " [shape=diamond,style=filled,label=\"\",height=.2,width=.2] \n";
		} else {
			s += id + " [shape=box,regular=1,style=filled,fillcolor=green,label=\""+ element.getName() +"\"] ; \n";	
		}
		
		if (leftChild != null) {
			s += id + " -> " + leftChild.id + "; \n";
			s += leftChild.getDotOfSubgraph();
		}
		if (rightChild != null) {
			s += id + " -> " + rightChild.id + "; \n";
			s += rightChild.getDotOfSubgraph();
		}
		
		return s;
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