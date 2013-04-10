package lib;

import master.HadoopMaster;

/**
 * A type that represents a job. This is used by the user
 * to set certain configurations before starting the map reduce job
 *
 */
public class Job {

	private String jobName = null;
	
	private Class<?> mapperClass = null;
	private Class<?> reducerClass = null;
	
	private Class<?> mapperOutputKeyClass = null;
	private Class<?> mapperOutputValueClass = null;
	
	private Class<?> reducerOutputKeyClass = null;
	private Class<?> reducerOutputValueClass = null;
	
	private Class<?> fileInputFormatClass = null;
	
	public Job() {
		
	}

	/**
	 * Getters and setters for all fields
	 */
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName+"_"+HadoopMaster.counter;
	}

	public Class<?> getFileInputFormatClass() {
		return fileInputFormatClass;
	}

	public void setFileInputFormatClass(Class<?> fileInputFormatClass) {
		this.fileInputFormatClass = fileInputFormatClass;
	}

	public Class<?> getMapperClass() {
		return mapperClass;
	}

	public void setMapperClass(Class<?> mapperClass) {
		this.mapperClass = mapperClass;
	}

	public Class<?> getReducerClass() {
		return reducerClass;
	}

	public void setReducerClass(Class<?> reducerClass) {
		this.reducerClass = reducerClass;
	}

	public Class<?> getMapperOutputKeyClass() {
		return mapperOutputKeyClass;
	}

	public void setMapperOutputKeyClass(Class<?> mapperOutputKeyClass) {
		this.mapperOutputKeyClass = mapperOutputKeyClass;
	}

	public Class<?> getMapperOutputValueClass() {
		return mapperOutputValueClass;
	}

	public void setMapperOutputValueClass(Class<?> mapperOutputValueClass) {
		this.mapperOutputValueClass = mapperOutputValueClass;
	}

	public Class<?> getReducerOutputKeyClass() {
		return reducerOutputKeyClass;
	}

	public void setReducerOutputKeyClass(Class<?> reducerOutputKeyClass) {
		this.reducerOutputKeyClass = reducerOutputKeyClass;
	}

	public Class<?> getReducerOutputValueClass() {
		return reducerOutputValueClass;
	}

	public void setReducerOutputValueClass(Class<?> reducerOutputValueClass) {
		this.reducerOutputValueClass = reducerOutputValueClass;
	}


}
