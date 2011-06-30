package it.processmining.clustering.test;

import it.processmining.clustering.exceptions.ClusteringException;
import it.processmining.clustering.hierarchical.Cluster;
import it.processmining.clustering.hierarchical.DistanceMatrix;
import it.processmining.clustering.hierarchical.HierarchicalClustering;
import it.processmining.clustering.model.process.HeuristicsNetSetRepresentation;
import it.processmining.clustering.ui.DendrogramWidget;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.JFrame;

import org.processmining.importing.heuristicsnet.HeuristicsNetFromFile;

public class DendrogramTest {
	
	public static void main(String s[]) throws FileNotFoundException, IOException, ClusteringException {
	
		if (s.length != 1) {
			System.err.println("Use: java -jar ... /path/to/examples/ (with trailing slash)");
			System.exit(1);
		}
		
		int total = 10;
		
		HashSet<HeuristicsNetSetRepresentation> set = new HashSet<HeuristicsNetSetRepresentation>(total);
		for (int i = 1; i <= total; i++) {
			set.add(new HeuristicsNetSetRepresentation("Process " + i, new HeuristicsNetFromFile(new FileInputStream(s[0] + i + ".hn")).getNet()));
		}
		
		DistanceMatrix dm = new DistanceMatrix(set, 0.5);
		Cluster root = HierarchicalClustering.cluster(set, 0.5);
		
		DendrogramWidget dw = new DendrogramWidget(dm, root);
		
		dw.getSVG(s[0] + "test.svg");

		
		JFrame frame = new JFrame("Dendrogram test");
		frame.add(dw);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1930, 1200));
		frame.pack();
		frame.setVisible(true);
	}
}
