package it.processmining.clustering.exceptions;

/**
 * Class for catching a general clustering error
 * 
 * @author Andrea Burattin
 * @version 0.1
 *
 */
@SuppressWarnings("serial")
public class ClusteringException extends Exception {

	/**
	 * Class constructor
	 * 
	 * @param error an error message
	 */
	public ClusteringException(String error) {
		super(error);
	}
	
}
