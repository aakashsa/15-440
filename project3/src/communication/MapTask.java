package communication;

import interfaces.Task;

import java.io.Serializable;

import lib.ConstantsParser;

/**
 * A message class that represents all information
 * needed to carry out a map task by a worker node
 *
 */
public class MapTask extends Task implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public ChunkObject chunk;
	public Class<?> fileInputFormat;
	public Class<?> mapperClass;
	public ConstantsParser cp;
	
	/**
	 * Constructor
	 * @param chunk - chunk of input file to map
	 * @param fileInputFormatClass
	 * @param mapperClass - mapper class
	 * @param cp - constants parser object
	 */
	public MapTask(ChunkObject chunk, Class<?> fileInputFormatClass, Class<?> mapperClass, ConstantsParser cp) {
		this.chunk = chunk;
		this.fileInputFormat = fileInputFormatClass;
		this.mapperClass = mapperClass;
		this.cp = cp;
	}

	/**
	 * This function returns the type of this task
	 */
	@Override
	public TaskType getTaskType() {
		return TaskType.MAP;
	}
	
}
