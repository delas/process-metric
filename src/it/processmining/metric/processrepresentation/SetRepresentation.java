package it.processmining.metric.processrepresentation;

import java.util.HashSet;

import it.processmining.metric.model.Activity;
import it.processmining.metric.model.BinaryConstraint;
import it.processmining.metric.model.ConstraintType;

public class SetRepresentation implements ProcessRepresentation {
	
	protected HashSet<BinaryConstraint> followedConstraints = new HashSet<BinaryConstraint>();
	protected HashSet<BinaryConstraint> notFollowedConstraints = new HashSet<BinaryConstraint>();

	
	public int getSize(ConstraintType ct) {
		if (ct.equals(ConstraintType.FOLLOWED_BY)) {
			return followedConstraints.size();
		} else if (ct.equals(ConstraintType.NOT_FOLLOWED_BY)) {
			return notFollowedConstraints.size();
		}
		return -1;
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
