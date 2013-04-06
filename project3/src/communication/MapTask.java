package communication;

import interfaces.Task;

import java.io.Serializable;

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
	
	public MapTask(ChunkObject chunk, Class<?> fileInputFormatClass, Class<?> mapperClass) {
		this.chunk = chunk;
		this.fileInputFormat = fileInputFormatClass;
		this.mapperClass = mapperClass;
	}

	@Override
	public TaskType getTaskType() {
		return TaskType.MAP;
	}
	
}
