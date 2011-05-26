package it.processmining.clustering.test;

import it.processmining.clustering.hierarchical.Cluster;
import it.processmining.clustering.hierarchical.DistanceMatrix;
import it.processmining.clustering.hierarchical.HierarchicalClustering;
import it.processmining.clustering.model.process.HeuristicsNetSetRepresentation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.processmining.importing.heuristicsnet.HeuristicsNetFromFile;

public class ClusterCalculator {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		HeuristicsNetFromFile hnff1 = new HeuristicsNetFromFile(new FileInputStream("/home/delas/desktop/hn-demo/demo.hn"));
		HeuristicsNetFromFile hnff2 = new HeuristicsNetFromFile(new FileInputStream("/home/delas/desktop/hn-xor/model.hn"));
		HeuristicsNetFromFile hnff3 = new HeuristicsNetFromFile(new FileInputStream("/home/delas/desktop/hn-and/model.hn"));
		
		HashSet<HeuristicsNetSetRepresentation> set = new HashSet<HeuristicsNetSetRepresentation>(3);
		set.add(new HeuristicsNetSetRepresentation("hn-demo", hnff1.getNet()));
		set.add(new HeuristicsNetSetRepresentation("hn-xor", hnff2.getNet()));
		set.add(new HeuristicsNetSetRepresentation("hn-and", hnff3.getNet()));
		
		DistanceMatrix dm = new DistanceMatrix(set, 0.5);
		System.out.println(dm);
//		Cluster root = HierarchicalClustering.cluster(set);
//		System.out.println(root.getAllElements());
	}
}
