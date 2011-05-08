package it.processmining.clustering.model.process;

import java.util.HashSet;

import it.processmining.clustering.model.Activity;
import it.processmining.clustering.model.BinaryConstraint;
import it.processmining.clustering.model.ConstraintType;

public class SetRepresentation implements ProcessRepresentation {
	
	protected HashSet<BinaryConstraint> followedConstraints = new HashSet<BinaryConstraint>();
	protected HashSet<BinaryConstraint> notFollowedConstraints = new HashSet<BinaryConstraint>();
	protected String name;
	
	
	public int getSize(ConstraintType ct) {
		if (ct.equals(ConstraintType.FOLLOWED_BY)) {
			return followedConstraints.size();
		} else if (ct.equals(ConstraintType.NOT_FOLLOWED_BY)) {
			return notFollowedConstraints.size();
		}
		return -1;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public HashSet<BinaryConstraint> getFollowedConstraints() {
		return followedConstraints;
	}
	
	
	public HashSet<BinaryConstraint> getNotFollowedConstraints() {
		return notFollowedConstraints;
	}
	
	
	protected void addFollowedConstraint(Activity first, Activity second) {
		BinaryConstraint bc = new BinaryConstraint(first, ConstraintType.FOLLOWED_BY, second);
		followedConstraints.add(bc);
	}
	
	
	protected void addFollowedConstraint(String firstName, String secondName) {
		Activity first = new Activity(firstName);
		Activity second = new Activity(secondName);
		addFollowedConstraint(first, second);
	}
	
	
	protected void addNotFollowedConstraint(Activity first, Activity second) {
		BinaryConstraint bc = new BinaryConstraint(first, ConstraintType.NOT_FOLLOWED_BY, second);
		notFollowedConstraints.add(bc);
	}
	
	
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
