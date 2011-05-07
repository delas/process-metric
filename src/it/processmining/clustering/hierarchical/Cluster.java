package it.processmining.clustering.hierarchical;

import java.util.HashSet;
import java.util.Set;

import it.processmining.metric.metric.JaccardDistance;
import it.processmining.metric.processrepresentation.SetRepresentation;
import it.processmining.utils.Utils;

public class Cluster {

	private static Integer globalId = 1;
	
	private Integer id;
	private SetRepresentation element = null;
	private Cluster leftChild = null;
	private Cluster rightChild = null;
	
	
	public Cluster(SetRepresentation element) {
		id = globalId++;
		addElement(element);
//		System.out.println("New cluster with element");
	}
	
	
	public Cluster(Cluster childLeft, Cluster childRight) {
		id = globalId++;
		setChildren(childLeft, childRight);
//		System.out.println("New cluster with clusters");
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
		
		for (SetRepresentation c1 : currentElements) {
			for (SetRepresentation c2 : otherElements) {
				sumDistances += JaccardDistance.getDistance(c1, c2);
			}
		}
		Double sumSizes = (double) (currentElements.size() + otherElements.size());
		return (sumDistances / sumSizes);
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
