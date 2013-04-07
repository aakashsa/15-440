package communication;

import java.io.Serializable;

/**
 * A type to store all information for a particular worker node
 *
 */
public class WorkerInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private int workerNum;
	private String host;
	private int port;
	
	/**
	 * Constructor
	 * @param workerNum - id of worker
	 * @param host - host of worker
	 * @param port - port the worker is listening on
	 */
	public WorkerInfo(int workerNum, String host, int port) {
		this.workerNum = workerNum;
		this.host = host;
		this.port = port;
	}

	/**
	 * Get worker number
	 * @return worker number
	 */
	public int getWorkerNum() {
		return workerNum;
	}

	/**
	 * Get worker host
	 * @return host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Get worker port
	 * @return port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Equals method for a worker information
	 */
	@Override
	public boolean equals(Object wi) {
		if (wi == null) return false;
		if (!(wi.getClass().getName().equals(WorkerInfo.class.getName()))) return false;
		WorkerInfo w = (WorkerInfo) wi;
		return (this.port == w.getPort() && this.host.equals(w.getHost()) && this.workerNum == w.getWorkerNum());
	}
}
