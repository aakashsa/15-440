package processmanagerstuff;

import java.util.concurrent.ConcurrentHashMap;

import processes.ThreadProcess;

public class LoadBalancer implements Runnable {

	private ConcurrentHashMap<Long, ThreadProcess> allProcesses;
	
	public LoadBalancer(ConcurrentHashMap<Long, ThreadProcess> allProcesses) {
		this.allProcesses = allProcesses;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
