package communication;

import java.io.Serializable;

public class WorkerInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private int workerNum;
	private String host;
	private int port;
	
	public WorkerInfo(int workerNum, String host, int port) {
		this.workerNum = workerNum;
		this.host = host;
		this.port = port;
	}

	public int getWorkerNum() {
		return workerNum;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}	
}
