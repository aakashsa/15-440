package interfaces;

import lib.Job;

/**
 * An interface for all classes defining a job configuration.
 * This will be used by the user to implement a job setup class.
 */
public interface JobConfiguration {

	/**
	 * The method that does setup for a job
	 * @return Job to carry out
	 */
	public Job setup();

}
