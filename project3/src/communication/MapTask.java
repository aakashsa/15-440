package communication;

import interfaces.Task;

import java.io.Serializable;

import lib.ConstantsParser;

/**
 * A class that represents all information
 * needed to carry out a map task by a worker node.
 * This class is serializable
 */
public class MapTask extends Task implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Chunk of input file to map
	 */
	public ChunkObject chunk;
	/**
	 * Class of file input format
	 */
	public Class<?> fileInputFormat;
	/**
	 * Mapper class
	 */
	public Class<?> mapperClass;
	/**
	 * Constants parser object
	 */
	public ConstantsParser cp;
	/**
	 * Job name that this map task is for
	 */
	public String jobName;
	/**
	 * Worker info of worker to which this task is given
	 */
	public WorkerInfo wi;
	
	/**
	 * Constructor
	 * @param chunk Chunk of input file to map
	 * @param fileInputFormatClass Class of input format
	 * @param mapperClass Mapper class
	 * @param cp Constants parser object
	 * @param jobName Job name that this map task is for
	 * @param wi Worker info of worker to which this task is given
	 */
	public MapTask(ChunkObject chunk, Class<?> fileInputFormatClass, Class<?> mapperClass, ConstantsParser cp, String jobName, WorkerInfo wi) {
		this.chunk = chunk;
		this.fileInputFormat = fileInputFormatClass;
		this.mapperClass = mapperClass;
		this.cp = cp;
		this.jobName = jobName;
		this.wi = wi;
	}
	
}
