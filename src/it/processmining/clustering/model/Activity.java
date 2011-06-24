package it.processmining.clustering.model;

/**
 * Class that describes an activity as its name
 * 
 * @author Andrea Burattin
 * @version 0.1
 */
public class Activity {

	private String name;

	/**
	 * 
	 * @param name
	 */
	public Activity(String name) {
		this.name = name;
	}

	
	/**
	 * 
	 * @return
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
