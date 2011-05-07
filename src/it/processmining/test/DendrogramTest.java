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
	
		
		HeuristicsNetFromFile hnff1 = new HeuristicsNetFromFile(new FileInputStream("/home/delas/desktop/hn-demo/demo.hn"));
		HeuristicsNetFromFile hnff2 = new HeuristicsNetFromFile(new FileInputStream("/home/delas/desktop/hn-xor/model.hn"));
		HeuristicsNetFromFile hnff3 = new HeuristicsNetFromFile(new FileInputStream("/home/delas/desktop/hn-and/model.hn"));
		
		HashSet<HeuristicsNetSetRepresentation> set = new HashSet<HeuristicsNetSetRepresentation>(3);
		set.add(new HeuristicsNetSetRepresentation("hn-demo", hnff1.getNet()));
		set.add(new HeuristicsNetSetRepresentation("hn-xor", hnff2.getNet()));
		set.add(new HeuristicsNetSetRepresentation("hn-and", hnff3.getNet()));
		
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
