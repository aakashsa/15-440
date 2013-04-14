package communication;

import interfaces.Task;

import java.io.Serializable;

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
	 * Max record size of key and value output concatenated from mapper
	 */
	public int mapperOutputRecordSize;
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
	 * @param mapperOutputRecordSize Max record size of mapper output key and value concatenated
	 * @param jobName Job name that this map task is for
	 * @param wi Worker info of worker to which this task is given
	 */
	public MapTask(ChunkObject chunk, Class<?> fileInputFormatClass, Class<?> mapperClass, int mapperOutputRecordSize, String jobName, WorkerInfo wi) {
		this.chunk = chunk;
		this.fileInputFormat = fileInputFormatClass;
		this.mapperClass = mapperClass;
		this.mapperOutputRecordSize = mapperOutputRecordSize;
		this.jobName = jobName;
		this.wi = wi;
	}
	
}
