package it.processmining.metric.model;

/**
 * Possible constraint types between activities
 * 
 * @author Andrea Burattin
 * @version 0.1
 */
public enum ConstraintType {
	FOLLOWED_BY {
		public String toString() {
			return "\u227B";
		}
	},
	NOT_FOLLOWED_BY {
		public String toString() {
			return "\u2281";
		}
	}
}
