package processes;
import interfaces.MigratableProcess;

/**
 * A helper class type that is used to enclose a migratable
 * process and the thread that is running that process
 * @author nikhiltibrewal
 *
 */
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