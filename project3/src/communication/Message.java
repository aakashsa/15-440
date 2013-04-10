package communication;

import interfaces.Task;

import java.io.Serializable;

/**
 * This class represents a communication message passed between
 * master node and worker nodes
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public MessageType type = null;
	public Task task = null;
	public Exception e = null;
	
	/**
	 * Constructor for only a message type
	 * @param type - message type
	 */
	public Message(MessageType type) {
		this.type = type;
	}
	
	/**
	 * Constructor for a message that contains a task
	 * @param type - message type
	 * @param task - task in message
	 */
	public Message(MessageType type, Task task) {
		this.type = type;
		this.task = task;
	}
	
	/**
	 * Constructor for a message that contains an exception
	 * @param type - message type
	 * @param e - exception in message
	 */
	public Message(MessageType type, Exception e) {
		this.type = type;
		this.e = e;
	}
}
