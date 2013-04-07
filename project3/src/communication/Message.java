package communication;

import interfaces.Task;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public MessageType type = null;
	public Task task = null;
	public Exception e = null;
	
	public Message(MessageType type) {
		this.type = type;
	}
	
	public Message(MessageType type, Task task) {
		this.type = type;
		this.task = task;
	}
	
	public Message(MessageType type, Exception e) {
		this.type = type;
		this.e = e;
	}
}
