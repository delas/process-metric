package it.processmining.metric.model;

/**
 * Class that describes an activity as its name
 * 
 * @author Andrea Burattin
 * @version 0.1
 */
public class Activity {

	private String name;

	/**
	 * Activity constructor
	 * @param name the name of the new activity
	 */
	public Activity(String name) {
		this.name = name;
	}

	
	/**
	 * Getter of the activity name
	 * @return the name of the activity
	 */
	public String getName() {
		return name;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Activity) {
			return getName().equals(((Activity)obj).getName());
		}
		return false;
	}
	
	
	@Override
	public String toString() {
		return name;
	}
	
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
}
