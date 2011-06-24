package it.processmining.clustering.hierarchical;

import java.util.Set;
import java.util.Vector;

import it.processmining.clustering.exceptions.ClusteringException;
import it.processmining.clustering.model.process.SetRepresentation;

/**
 * This class performs hierarchical clustering for the given set of elements
 * 
 * @author Andrea Burattin
 * @version 0.1
 *
 */
public class HierarchicalClustering {
	
	/**
	 * The only, static, method of the class: here is where the hierarchical
	 * clustering is actually performed
	 * 
	 * @param elements the set of element used to perform the clustering
	 * @param alpha the parameter to balance the difference between positive and
	 * 			negated relations
	 * @return the cluster that is the root of the cluster
	 * @throws ClusteringException
	 */
	public static Cluster cluster(Set<? extends SetRepresentation> elements, double alpha) throws ClusteringException {
		
		if (elements.size() < 2) {
			throw new ClusteringException("Unsufficient number of elements");
		}
		
		Vector<Cluster> active = new Vector<Cluster>();
		for (SetRepresentation sr : elements) {
			active.add(new Cluster(sr));
		}
		
		Cluster root = null;
		while(active.size() > 1) {
			
			Double bestDistance = 2.0;
			Cluster left = null;
			Cluster right = null;
			
			for (Cluster cluster1 : active) {
				for (Cluster cluster2 : active) {
					if (!cluster1.equals(cluster2)) {
						Double currentDistance = cluster1.getDistance(cluster2, alpha);
						if (currentDistance < bestDistance) {
							bestDistance = currentDistance;
							left = cluster1;
							right = cluster2;
						}
					}
				}
			}
			
			active.remove(left);
			active.remove(right);
			
			root = new Cluster(left, right);
			active.add(root);
		}
		
		return root;
		
	}
}
