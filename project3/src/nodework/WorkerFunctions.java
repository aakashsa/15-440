package nodework;

import interfaces.Task;
import java.io.ObjectOutputStream;

import communication.MapTask;
import communication.MessageType;
import communication.ReduceTask;

/**
 * Class that contains that calls appropriate functions for map or reduce
 */
public class WorkerFunctions implements Runnable {

	private MessageType type;
	private Task task;
	private ObjectOutputStream out;
	
	/**
	 * Constructor for a new map/reduce function performer
	 * @param type Type of message
	 * @param task Task to perform
	 * @param out Output stream to write results to
	 */
	public WorkerFunctions(MessageType type, Task task, ObjectOutputStream out) {
		this.type = type;
		this.task = task;
		this.out = out;
	}
	
	/**
	 * The run method of this runnable. It just checks
	 * if it needs to perform a map or reduce, and calls the appropriate function.
	 */
	@Override
	public void run() {
		if (this.type == MessageType.START_MAP) {
			MapFunction.doMap((MapTask) this.task, this.out);
		} else if (this.type == MessageType.START_REDUCE) {
			ReduceFunction.doReduce((ReduceTask) this.task, this.out);
		}
	}
	
}	