package it.processmining.clustering.hierarchical;

import it.processmining.clustering.metric.JaccardDistance;
import it.processmining.clustering.model.process.SetRepresentation;

import java.text.DecimalFormat;
import java.util.Set;
import java.util.Vector;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * This class represents the distance matrix of a set of elements
 * 
 * @author Andrea Burattin
 * @version 0.1
 */
public class DistanceMatrix {
	
	private Vector<SetRepresentation> elements = null;
	private SparseDoubleMatrix2D matrix = null;

	
	/**
	 * Class constructor, it also calculates the matrix
	 * 
	 * @param set the set of elements
	 */
	public DistanceMatrix(Set<? extends SetRepresentation> set, double alpha) {
		elements = new Vector<SetRepresentation>(set.size());
		elements.addAll(set);
		calculateMatrix(alpha);
	}
	
	
	/**
	 * This private method calculates the distance matrix values
	 * 
	 * @param alpha the convex combinator
	 */
	private void calculateMatrix(double alpha) {
		matrix = new SparseDoubleMatrix2D(elements.size(), elements.size());
		
//		matrix = new Vector<Vector<Double>>(elements.size());
//		for (int i = 0; i < elements.size(); i++) {
//			matrix.add(new Vector<Double>(i + 1));
//			for (int j = 0; j < i + 1; j++) {
//				matrix.get(i).add(0.0);
//			}
//		}
		
		for (int i = 0; i < elements.size(); i++) {
			for (int j = 0; j < i + 1; j++) {
				Double m = JaccardDistance.getDistance(elements.get(i), elements.get(j), alpha);
				setValue(i, j, m);
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
		return matrix.get(i, j);
	}
	
	
	private void setValue(int i, int j, double value) {
		if (j > i) {
			int tmp = j;
			j = i;
			i = tmp;
		}
		matrix.set(i, j, value);
	}
	
	
	/**
	 * Gets the sorted vector of elements of the distance matrix
	 * 
	 * @return the vector of elements
	 */
	public Vector<? extends SetRepresentation> getElements() {
		return elements;
	}
	
	
	/**
	 * This method returns the index of the given elements into the distance
	 * matrix
	 * 
	 * @param element the current element
	 * @return the position of the element in the distance matrix, -1 if the
	 * element is not present
	 */
	public Integer getIndexOfElements(SetRepresentation element) {
		return elements.indexOf(element);
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
