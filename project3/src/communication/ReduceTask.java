package communication;

import java.io.Serializable;

import interfaces.Task;

public class ReduceTask extends Task implements Serializable {

	private static final long serialVersionUID = 1L;

	public Class<?> reducerClass;
	public int reducerNumber;
	public Class<?> reducerInputKeyClass;
	public Class<?> reducerInputValueClass;
	public String outputDir;
	
	public ReduceTask(int reducerNumber, Class<?> reducerClass, Class<?> reducerInputKeyClass, Class<?> reducerInputValueClass, String outputDir) {
		this.reducerClass = reducerClass;
		this.reducerNumber = reducerNumber;
		this.reducerInputKeyClass = reducerInputKeyClass;
		this.reducerInputValueClass = reducerInputValueClass;
		this.outputDir = outputDir;
	}
	
	@Override
	public TaskType getTaskType() {
		return TaskType.REDUCE;
	}

}
