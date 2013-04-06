package interfaces;

import communication.TaskType;

/**
 * An abstraction that represents a task sent to a worker
 */
public abstract class Task {

	/**
	 * Get the type of task (an abstract function)
	 * @return task type
	 */
	public abstract TaskType getTaskType();
	
}
