package it.processmining.clustering.model;

/**
 * This class describes a binary constraint between activities
 * 
 * @author Andrea Burattin
 * @version 0.1
 */
public class BinaryConstraint {

	private ConstraintType type;
	private Activity firstActivity;
	private Activity secondActivity;
	
	
	/**
	 * Binary constraint constructor
	 * 
	 * @param firstActivity the first activity
	 * @param type the type of the constraint
	 * @param secondActivity the second activity
	 */
	public BinaryConstraint(Activity firstActivity, ConstraintType type, Activity secondActivity) {
		this.firstActivity = firstActivity;
		this.type = type;
		this.secondActivity = secondActivity;
	}
	
	
	/**
	 * Binary constraint constructor, starting from the activity names
	 * 
	 * @param firstActivity the name of first activity
	 * @param type the type of the constraint
	 * @param secondActivity the name of second activity
	 */
	public BinaryConstraint(String firstActivity, ConstraintType type, String secondActivity) {
		this.firstActivity = new Activity(firstActivity);
		this.type = type;
		this.secondActivity = new Activity(secondActivity);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public ConstraintType getType() {
		return type;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Activity getFirstActivity() {
		return firstActivity;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Activity getSecondActivity() {
		return secondActivity;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BinaryConstraint) {
			BinaryConstraint bc = (BinaryConstraint) obj;
			if (getFirstActivity().equals(bc.getFirstActivity()) &&
					getSecondActivity().equals(bc.getSecondActivity()) &&
					getType().equals(bc.getType())) {
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public int hashCode() {
	    int hash = 1;
	    hash = hash * 31 + (firstActivity == null ? 0 : firstActivity.hashCode());
	    hash = hash * 31 + (secondActivity == null ? 0 : secondActivity.hashCode());
	    hash = hash * 31 + (type == null ? 0 : type.hashCode());
	    return hash;
	}
	
	
	@Override
	public String toString() {
		return firstActivity + " " + type + " " + secondActivity;
	}
}
