package communication;

import interfaces.Task;

import java.io.Serializable;

/**
 * This class represents a communication message passed between
 * master node and worker nodes. This class is serializable.
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Type of this message
	 */
	public MessageType type = null;
	/**
	 * Task in this message, if any
	 */
	public Task task = null;
	/**
	 * Exception in this message, if any
	 */
	public Exception e = null;
	/**
	 * Input file path
	 */
	public String inputFilePath = null;
	/**
	 * Job config files dir
	 */
	public String configFilesDir = null;
	/**
	 * Job name
	 */
	public String jobName = null;
	
	/**
	 * Constructor for only a message type
	 * @param type Message type
	 */
	public Message(MessageType type) {
		this.type = type;
	}
	
	/**
	 * Constructor for a message that contains a task
	 * @param type Message type
	 * @param task Task in message
	 */
	public Message(MessageType type, Task task) {
		this.type = type;
		this.task = task;
	}
	
	/**
	 * Constructor for a message that contains an exception
	 * @param type Message type
	 * @param e Exception in message
	 */
	public Message(MessageType type, Exception e) {
		this.type = type;
		this.e = e;
	}
	
	/**
	 * A message constructor for runjob commands
	 * @param type Message type
	 * @param inputFilePath Path to input file
	 * @param configFilesDir Path to job config files
	 */
	public Message(MessageType type, String inputFilePath, String configFilesDir) {
		this.type = type;
		this.inputFilePath = inputFilePath;
		this.configFilesDir = configFilesDir;
	}
	
	/**
	 * Constructor for kill job commands
	 * @param type Message type
	 * @param jobName Job name
	 */
	public Message(MessageType type, String jobName) {
		this.type = type;
		this.jobName = jobName;
	}
}
