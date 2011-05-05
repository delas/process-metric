package it.processmining.clustering.hierarchical;

import java.util.Vector;

import it.processmining.metric.processrepresentation.SetRepresentation;

public class HierarchicalClustering {

	private DistanceMatrix distanceMatrix = null;
	
	public void cluster(DistanceMatrix distanceMatrix) {
		this.distanceMatrix = distanceMatrix;
		
		Vector<Cluster> active = new Vector<Cluster>();
		// populate "active" with all the singleton cluster
		
		while(active.size() > 1) {
			
			Double bestDistance = 2.0;
			Cluster left = null;
			Cluster right = null;
			
			for (Cluster cluster1 : active) {
				for (Cluster cluster2 : active) {
					if (!cluster1.equals(cluster2)) {
						Double currentDistance = cluster1.getDistance(cluster2);
						if (currentDistance < bestDistance) {
							bestDistance = currentDistance;
							left = cluster1;
							right = cluster2;
						}
					}
				}
			}
			
			// remove left and right
			// add a new cluster as merge of left and right
			
		}

		
	}
}
