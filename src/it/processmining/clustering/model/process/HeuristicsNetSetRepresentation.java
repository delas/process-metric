package it.processmining.clustering.model.process;

import java.util.Vector;

import org.processmining.framework.models.heuristics.HNSet;
import org.processmining.framework.models.heuristics.HNSubSet;
import org.processmining.framework.models.heuristics.HeuristicsNet;


/**
 * Class for the representation of a Heuristics Net as a set of constraints
 * 
 * @author Andrea Burattin
 * @version 0.1
 *
 */
public class HeuristicsNetSetRepresentation extends SetRepresentation {
	
	private HeuristicsNet hn;
	private int activityCounter;

	
	/**
	 * Constructor of the representation, given the process name and its
	 * representation as Heuristics Net
	 * 
	 * @param name
	 * @param hn
	 */
	public HeuristicsNetSetRepresentation(String name, HeuristicsNet hn) {
		this.hn = hn;
		this.activityCounter = hn.size();
		setName(name);
		generateSets(hn);
	}
	
	
	/**
	 * This method converts an Heuristics Net into two sets of constraints
	 * 
	 * @param hn the Heuristics Net to be converted
	 */
	private void generateSets(HeuristicsNet hn) {
		
		for (int i = 0; i < activityCounter; i++) {
			HNSet set = hn.getOutputSet(i);
			
			if (set.size() == 1) {
				HNSubSet subset = set.get(0);
				if (subset.size() == 1) {
					// it is a sequence
					addSequenceRelation(i, set.get(0).get(0));
				} else {
					// it is a xor split
					addXorRelation(i, fromHNSubSetToVector(subset));
				}
			} else if (set.size() > 1) {
				// it is an and split
				addAndRelation(i, fromHNSetToVector(set));
			}
		}
	}
	
	
	/**
	 * This method adds a new sequence relation (derived) between the two
	 * activities described by the indexes
	 * 
	 * @param firstIndex index of the first activity
	 * @param secondIndex index of the second activity
	 */
	private void addSequenceRelation(int firstIndex, int secondIndex) {
		String firstName = fromIndexToName(firstIndex);
		String secondName = hn.getLogEvents().get(secondIndex).getModelElementName();
		// A > B
		addFollowedConstraint(firstName, secondName);
		// B !> A
		addNotFollowedConstraint(secondName, firstName);
	}
	
	
	/**
	 * This method adds the constraints in the case of an AND split
	 * 
	 * @param splitIndex index of the split activity
	 * @param branchIndexes vector with the indexes of all the branches
	 */
	private void addAndRelation(int splitIndex, Vector<Integer> branchIndexes) {
		// add the sequence relations to all the branches
		for (Integer i : branchIndexes) {
			addSequenceRelation(splitIndex, i);
		}
		// for all the couples of branches A > B and B > A
		for (Integer i : branchIndexes) {
			for (Integer j : branchIndexes) {
				if (i != j) {
					addFollowedConstraint(fromIndexToName(i), fromIndexToName(j));
				}
			}
		}
	}
	
	
	/**
	 * This method adds the constraints in the case of a XOR split
	 * 
	 * @param splitIndex index of the split activity
	 * @param branchIndexes vector with the indexes of all the branches
	 */
	private void addXorRelation(int splitIndex, Vector<Integer> branchIndexes) {
		// add the sequence relations to all the branches
		for (Integer i : branchIndexes) {
			addSequenceRelation(splitIndex, i);
		}
		// for all the couples of branches A > B and B > A
		for (Integer i : branchIndexes) {
			for (Integer j : branchIndexes) {
				if (i != j) {
					addNotFollowedConstraint(fromIndexToName(i), fromIndexToName(j));
				}
			}
		}
	}
	
	
	/**
	 * This method is necessary to convert a HNSet into a vector of integers
	 * 
	 * @param set the given set
	 * @return the vector of integer
	 */
	private static Vector<Integer> fromHNSetToVector(HNSet set) {
		Vector<Integer> v = new Vector<Integer>();
		
		for (int i = 0; i < set.size(); i++) {
			v.add(set.get(i).get(0));
		}
		
		return v;
	}
	
	
	/**
	 * This method is necessary to convert a HNSubSet into a vector of integers
	 * 
	 * @param subset the given subset
	 * @return the vector of integer
	 */
	private static Vector<Integer> fromHNSubSetToVector(HNSubSet subset) {
		Vector<Integer> v = new Vector<Integer>();
		
		for (int i = 0; i < subset.size(); i++) {
			v.add(subset.get(i));
		}
		
		return v;
	}
	
	
	private String fromIndexToName(int activityIndex) {
		return hn.getLogEvents().get(activityIndex).getModelElementName();
	}
}
