package it.processmining.clustering.hierarchical;

import java.text.DecimalFormat;
import java.util.Set;
import java.util.Vector;

import it.processmining.metric.metric.JaccardDistance;
import it.processmining.metric.processrepresentation.SetRepresentation;

/**
 * This class represents the distance matrix of a set of elements
 * 
 * @author Andrea Burattin
 * @version 0.1
 */
public class DistanceMatrix {
	
	private Vector<? extends SetRepresentation> elements = null;
	private Vector<Vector<Double>> matrix = null;

	
	/**
	 * Class constructor, it also calculates the matrix
	 * 
	 * @param set the set of elements
	 */
	public DistanceMatrix(Set<? extends SetRepresentation> set) {
		elements = new Vector<SetRepresentation>(set.size());
		elements.addAll(set);
		calculateMatrix();
	}
	
	
	/**
	 * This private method calculates the distance matrix values
	 */
	private void calculateMatrix() {
		matrix = new Vector<Vector<Double>>(elements.size());
		for (int i = 0; i < elements.size(); i++) {
			matrix.add(new Vector<Double>(i + 1));
			for (int j = 0; j < i + 1; j++) {
				matrix.get(i).add(0.0);
			}
		}
		
		for (int i = 0; i < elements.size(); i++) {
			for (int j = 0; j < i + 1; j++) {
				Double m = JaccardDistance.getDistance(elements.get(i), elements.get(j));
				matrix.get(i).set(j, m);
			}
		}
	}
	
	
	/**
	 * This method returns the distance value of two elements 
	 * 
	 * @param i the row number
	 * @param j the column number
	 * @return the distance value
	 */
	public Double getValue(int i, int j) {
		if (j > i) {
			int tmp = j;
			j = i;
			i = tmp;
		}
		return matrix.get(i).get(j);
	}
	
	
	/**
	 * Gets the sorted vector of elements of the distance matrix
	 * 
	 * @return the vector of elements
	 */
	public Vector<? extends SetRepresentation> getElements() {
		return elements;
	}
	
	
	@Override
	public String toString() {
		String t = "";
		
		// table header
		t = t.concat("\t");
		for (int i = 0; i < elements.size(); i++) {
			t = t.concat(i + "\t");
		}
		t = t.concat("\n");
		
		// table content
		for (int i = 0; i < elements.size(); i++) {
			for (int j = 0; j < elements.size(); j++) {
				if (j == 0) {
					t = t.concat(i + "\t");
				}
				DecimalFormat form = new DecimalFormat("#.###");
				Double m = getValue(i, j);
				t = t.concat(form.format(m) + "\t");
			}
			t = t.concat("\n");
		}
		
		return t;
	}
}
