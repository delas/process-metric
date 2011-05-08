package it.processmining.clustering.utils;

public class Utils {

	/**
	 * This method provides a kind of "xor" function between two objects. It
	 * returns true if both are null or if both have the same value
	 * 
	 * @param o1 the first object
	 * @param o2 the second object
	 * @return a "xor" function of the two objects
	 */
	public static boolean xor(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return true;
		} else {
			if (o1 != null && o2 != null) {
				return o1.equals(o2);
			} else {
				return false;
			}
		}
	}
}
