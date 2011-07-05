package it.processmining.clustering.model.process;

import java.util.HashSet;

import it.processmining.clustering.model.Activity;
import it.processmining.clustering.model.BinaryConstraint;
import it.processmining.clustering.model.ConstraintType;

/**
 * This is a class for the representation of a process as two set of relations
 * (one is the "follow" and the other is the "not ).
 * 
 * @author Andrea Burattin
 * @version 0.1
 *
 */
public class SetRepresentation implements ProcessRepresentation {
	
	protected HashSet<BinaryConstraint> followedConstraints = new HashSet<BinaryConstraint>();
	protected HashSet<BinaryConstraint> notFollowedConstraints = new HashSet<BinaryConstraint>();
	protected String name;
	protected Object processRepresentative;
	
	
	/**
	 * Get the size of the given sets
	 * 
	 * @param ct the type of constraint to consider
	 * @return the size of the set
	 */
	public int getSize(ConstraintType ct) {
		if (ct.equals(ConstraintType.FOLLOWED_BY)) {
			return followedConstraints.size();
		} else if (ct.equals(ConstraintType.NOT_FOLLOWED_BY)) {
			return notFollowedConstraints.size();
		}
		return -1;
	}
	
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public HashSet<BinaryConstraint> getFollowedConstraints() {
		return followedConstraints;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public HashSet<BinaryConstraint> getNotFollowedConstraints() {
		return notFollowedConstraints;
	}
	
	
	/**
	 * 
	 * @param processRepresentative
	 */
	public void setProcessRepresentative(Object processRepresentative) {
		this.processRepresentative = processRepresentative;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Object getProcessRepresentative() {
		return processRepresentative;
	}
	
	
	/**
	 * Add a new constraint as a followed relation, given two activities
	 * 
	 * @param first
	 * @param second
	 */
	protected void addFollowedConstraint(Activity first, Activity second) {
		BinaryConstraint bc = new BinaryConstraint(first, ConstraintType.FOLLOWED_BY, second);
		followedConstraints.add(bc);
	}
	
	
	/**
	 * Add a new constraint as a followed relation, given two activity names
	 * 
	 * @param firstName
	 * @param secondName
	 */
	protected void addFollowedConstraint(String firstName, String secondName) {
		Activity first = new Activity(firstName);
		Activity second = new Activity(secondName);
		addFollowedConstraint(first, second);
	}
	
	
	/**
	 * Add a new constraint as a not followed relation, given two activities
	 * 
	 * @param first
	 * @param second
	 */
	protected void addNotFollowedConstraint(Activity first, Activity second) {
		BinaryConstraint bc = new BinaryConstraint(first, ConstraintType.NOT_FOLLOWED_BY, second);
		notFollowedConstraints.add(bc);
	}
	
	
	/**
	 * Add a new constraint as a not followed relation, given two activity names
	 * 
	 * @param firstName
	 * @param secondName
	 */
	protected void addNotFollowedConstraint(String firstName, String secondName) {
		Activity first = new Activity(firstName);
		Activity second = new Activity(secondName);
		addNotFollowedConstraint(first, second);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SetRepresentation) {
			return name.equals(((SetRepresentation) obj).getName());
		}
		return false;
	}
	
	
	@Override
	public String toString() {
		String s = "";
		s += "Positive constraints\n";
		s += "====================\n";
		int i = 1;
		for (BinaryConstraint c : followedConstraints) {
			s += i++ + ". " + c + "\n";
		}
		s += "\n";
		s += "Negative constraints\n";
		s += "====================\n";
		i = 1;
		for (BinaryConstraint c : notFollowedConstraints) {
			s += i++ + ". " + c + "\n";
		}
		return s;
	}
	
}
