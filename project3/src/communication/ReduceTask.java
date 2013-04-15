package communication;

import java.io.Serializable;

import interfaces.Task;

/**
 * A message class that represents all information
 * needed to carry out a reduce task by a worker node.
 * This class is serializable.
 */
public class ReduceTask extends Task implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * ID of reducer to send this task to
	 */
	public Class<?> reducerClass;
	/**
	 * Reducer class
	 */
	public int reducerNumber;
	/**
	 * Reducer input key type
	 */
	public Class<?> reducerInputKeyClass;
	/**
	 * Reducer input value type
	 */
	public Class<?> reducerInputValueClass;
	/**
	 * Directory to put results of reduce in
	 */
	public String outputDir;
	/**
	 * Job name to which this reduce task belongs to
	 */
	public String jobName;
	/**
	 * Record size of concatenation of key and value of mapper output
	 */
	public long mapperOutputSize;
	/**
	 * File number of reducer input file
	 */
	public int reducerInputFileNumber;
	
	/**
	 * Constructor
	 * @param reducerNumber ID of reducer to send this task to
	 * @param reducerClass Reducer class
	 * @param reducerInputKeyClass Reducer input key type
	 * @param reducerInputValueClass Reducer input value type
	 * @param outputDir Directory to put results of reduce in
	 * @param jobName Job name to which this reduce task belongs to
	 * @param mapperOutputSize Record size of concatenation of key and value of mapper output
	 */
	public ReduceTask(int reducerNumber, Class<?> reducerClass, Class<?> reducerInputKeyClass, Class<?> reducerInputValueClass, String outputDir, String jobName, long mapperOutputSize, int reduceInputFileNumber) {
		this.reducerClass = reducerClass;
		this.reducerNumber = reducerNumber;
		this.reducerInputKeyClass = reducerInputKeyClass;
		this.reducerInputValueClass = reducerInputValueClass;
		this.outputDir = outputDir;
		this.jobName = jobName;
		this.mapperOutputSize = mapperOutputSize;
		this.reducerInputFileNumber = reduceInputFileNumber;
	}	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object a){
		if (a == null)
			throw new IllegalArgumentException("Can't compare a null ReduceTask");
		if (!(a instanceof ReduceTask ))
			return false;
		if (((ReduceTask)a).reducerClass != this.reducerClass)
			return false;
		if (((ReduceTask)a).reducerNumber != this.reducerNumber)
			return false;
		if (((ReduceTask)a).reducerInputKeyClass != this.reducerInputKeyClass)
			return false;
		if (((ReduceTask)a).reducerInputValueClass != this.reducerInputValueClass)
			return false;
		if (((ReduceTask)a).outputDir.equals(this.outputDir))
			return false;
		if (((ReduceTask)a).jobName.equals(this.jobName))
			return false;
		if (((ReduceTask)a).mapperOutputSize != this.mapperOutputSize)
			return false;
		return true;
	}
}
