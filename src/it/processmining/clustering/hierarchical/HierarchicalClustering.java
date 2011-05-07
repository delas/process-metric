package it.processmining.clustering.hierarchical;

import java.util.Set;
import java.util.Vector;

import it.processmining.metric.processrepresentation.SetRepresentation;

public class HierarchicalClustering {
	
	public static Cluster cluster(Set<? extends SetRepresentation> elements) {
		
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
						Double currentDistance = cluster1.getDistance(cluster2);
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
