package it.processmining.clustering.hierarchical;

import java.util.HashSet;

import it.processmining.metric.metric.JaccardDistance;
import it.processmining.metric.processrepresentation.SetRepresentation;

public class Cluster {

	private SetRepresentation medoid = null;
	private HashSet<SetRepresentation> cluster = null;
	
	
	public void Cluster() {
		
	}
	
	
	public Double getDistance(Cluster c) {
		return JaccardDistance.getDistance(medoid, c.medoid);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cluster) {
			Cluster c = (Cluster) obj;
			return (c.medoid.equals(medoid)) && (c.cluster.equals(cluster));
		}
		return false;
	}
}
