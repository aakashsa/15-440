package communication;

/**
 * An enum type that has all message types needed to communicate
 * between worker and master
 */
public enum MessageType {
	START_MAP,     // type to start a map task
	START_REDUCE,  // type to start a reduce task
	DONE_MAP,      // type to indicate a finished map task
	DONE_REDUCE,   // type to indicate a finished reduce task
	EXCEPTION,     // type to indicate an exception
	PING_REQUEST,  // type to indicate a ping request from master to worker
	PING_REPLY;    // type to indicate a ping reply from worker to master
}
