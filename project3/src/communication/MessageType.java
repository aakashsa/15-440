package communication;

/**
 * An enum type that has all message types needed to communicate
 * between worker and master
 */
public enum MessageType {
	/**
	 * Type to instruct to start a map task
	 */
	START_MAP,
	/**
	 * Type to instruct to start a reduce task
	 */
	START_REDUCE,
	/**
	 * Type to indicate a finished map task
	 */
	DONE_MAP,
	/**
	 * Type to indicate a finished reduce task
	 */
	DONE_REDUCE,
	/**
	 * Type to indicate an exception
	 */
	EXCEPTION;
}
