package processes;
import interfaces.MigratableProcess;


public class ThreadProcess {
	
	private MigratableProcess p;
	private Thread t;
	
	public ThreadProcess(Thread t, MigratableProcess p) {
		this.t = t;
		this.p = p;
	}
	
	public Thread getThread() {
		return t;
	}
	
	public MigratableProcess getProcess() {
		return p;
	}
	
	public boolean threadIsAlive() {
		return t.isAlive();
	}
	
	public void assignNewThread(Thread t2) {
		this.t = t2;
	}
}