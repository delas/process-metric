package it.processmining.metric.metric;

import java.util.HashSet;
import java.util.Set;

import org.processmining.framework.models.heuristics.HeuristicsNet;

import it.processmining.metric.processrepresentation.HeuristicsNetSetRepresentation;
import it.processmining.metric.processrepresentation.SetRepresentation;


/**
 * This class is just a container for distance based on the Jaccard similarity
 * 
 * @author Andrea Burattin
 * @version 0.1
 */
public class JaccardDistance {

	
	/**
	 * This method calculates the distance between two processes represented as
	 * two sets of relations. The distance is calculated as the average of the
	 * Jaccard distances between the positive and the negative constraints of
	 * the two processes
	 * 
	 * @param hn1 the first process
	 * @param hn2 the second process
	 * @return the Jaccard distance between the two processes
	 */
	public static double getDistance(HeuristicsNet hn1, HeuristicsNet hn2) {
		HeuristicsNetSetRepresentation hnsr1 = new HeuristicsNetSetRepresentation(hn1);
		HeuristicsNetSetRepresentation hnsr2 = new HeuristicsNetSetRepresentation(hn2);
		return getDistance(hnsr1, hnsr2);
	}
	
	
	/**
	 * This method calculates the distance between two processes represented as
	 * two sets of relations. The distance is calculated as the average of the
	 * Jaccard distances between the positive and the negative constraints of
	 * the two processes
	 * 
	 * @param first the first process
	 * @param second the second process
	 * @return the Jaccard distance between the two processes
	 */
	public static double getDistance(SetRepresentation first, SetRepresentation second) {
		double distancePositive = 1 - getJaccard(first.getFollowedConstraints(), second.getFollowedConstraints());
		double distanceNegative = 1 - getJaccard(first.getNotFollowedConstraints(), second.getNotFollowedConstraints());
		return (distancePositive + distanceNegative) / 2;
	}
	
	
	/**
	 * This method calculates the Jaccard similarity between two sets.
	 * The Jaccard similarity is defined as:
	 * $$
	 * 		J(A,B) = \frac{|A \cap B|}{|A \cup B|}
	 * $$
	 * 
	 * @param <E>
	 * @param s1 the first set of elements
	 * @param s2 the second set of elements
	 * @return the Jaccard similarity
	 */
	private static <E> double getJaccard(Set<E> s1, Set<E> s2) {	
		// union
		Set<E> s3 = new HashSet<E>(s1);
		s3.addAll(s2);
		double sizeUnion = s3.size();

		// intersection
		s1.retainAll(s2);
		double sizeIntersection = s1.size();

		return sizeIntersection / sizeUnion;
	}

}
