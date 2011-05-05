package it.processmining.metric;

import it.processmining.metric.metric.JaccardDistance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.processmining.importing.heuristicsnet.HeuristicsNetFromFile;

public class MetricCalculator {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		if (args.length != 2) {
			
			System.err.println("Use: java -jar metric.jar FIRST-HN-FILE SECOND-HN-FILE");
			
		} else {
		
			HeuristicsNetFromFile hnff = new HeuristicsNetFromFile(new FileInputStream(args[0]));
			HeuristicsNetFromFile hnff2 = new HeuristicsNetFromFile(new FileInputStream(args[1]));
			
			System.out.println("Jaccard: " + JaccardDistance.getDistance(hnff.getNet(), hnff2.getNet()));
		}

	}
}
