package it.processmining.test;

import it.processmining.clustering.hierarchical.Cluster;
import it.processmining.clustering.hierarchical.DistanceMatrix;
import it.processmining.clustering.hierarchical.HierarchicalClustering;
import it.processmining.clustering.ui.DendrogramWidget;
import it.processmining.metric.processrepresentation.HeuristicsNetSetRepresentation;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.JFrame;

import org.processmining.importing.heuristicsnet.HeuristicsNetFromFile;

public class DendrogramTest {
	
	public static void main(String s[]) throws FileNotFoundException, IOException {
	
		int total = 8;
		
		HashSet<HeuristicsNetSetRepresentation> set = new HashSet<HeuristicsNetSetRepresentation>(total);
		for (int i = 1; i <= total; i++) {
			set.add(new HeuristicsNetSetRepresentation("Process " + i, new HeuristicsNetFromFile(new FileInputStream("/home/delas/desktop/models/"+i+".hn")).getNet()));
		}
		
		DistanceMatrix dm = new DistanceMatrix(set);
		Cluster root = HierarchicalClustering.cluster(set);
		
		DendrogramWidget dw = new DendrogramWidget(dm, root);

		
		JFrame frame = new JFrame("Dendrogram test");
		frame.add(dw);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1930, 1200));
		frame.pack();
		frame.setVisible(true);
	}
}
