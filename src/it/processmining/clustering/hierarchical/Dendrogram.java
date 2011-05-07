package it.processmining.clustering.hierarchical;

import java.io.IOException;
import java.io.Writer;

import org.processmining.framework.models.ModelGraph;

public class Dendrogram extends ModelGraph {
	
	private Cluster root = null;

	public Dendrogram(String graphName, Cluster root) {
		super(graphName);
		this.root = root;
	}
	
	
	public void writeToDot(Writer bw) throws IOException {
		nodeMapping.clear();
		
		if (root == null) {
			return;
		}
		
		bw.write("digraph G {\n");
		
		bw.write(root.getDotOfSubgraph());
		
		bw.write("}\n");
	}

}
