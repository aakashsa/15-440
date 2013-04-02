package communication;

public class WorkerInfo {

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
