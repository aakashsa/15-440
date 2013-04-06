package communication;

import java.io.Serializable;

import interfaces.Task;

/**
 * A message class that represents all information
 * needed to carry out a reduce task by a worker node
 *
 */
public class ReduceTask extends Task implements Serializable {

	private static final long serialVersionUID = 1L;

	public Class<?> reducerClass;
	public int reducerNumber;
	public Class<?> reducerInputKeyClass;
	public Class<?> reducerInputValueClass;
	public String outputDir;
	
	/**
	 * Constructor
	 * @param reducerNumber - ID of reducer to send this task to
	 * @param reducerClass - reducer class
	 * @param reducerInputKeyClass - input key type
	 * @param reducerInputValueClass - input value type
	 * @param outputDir - directory to put results of reduce in
	 */
	public ReduceTask(int reducerNumber, Class<?> reducerClass, Class<?> reducerInputKeyClass, Class<?> reducerInputValueClass, String outputDir) {
		this.reducerClass = reducerClass;
		this.reducerNumber = reducerNumber;
		this.reducerInputKeyClass = reducerInputKeyClass;
		this.reducerInputValueClass = reducerInputValueClass;
		this.outputDir = outputDir;
	}
	
	/**
	 * Get the task type of this task, i.e. reduce
	 */
	@Override
	public TaskType getTaskType() {
		return TaskType.REDUCE;
	}

}
