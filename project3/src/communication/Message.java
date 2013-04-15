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
}
