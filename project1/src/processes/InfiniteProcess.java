package processes;

import interfaces.MigratableProcess;

/**
 * Infinite Process For Testing : Specially to Test with Load Balancing
 * @author aakashsabharwal
 *
 */
public class InfiniteProcess implements MigratableProcess {
	private volatile boolean suspending;

	public InfiniteProcess(String args[]){
		
	}
	
	@Override
	public void run() {
		while(!suspending){
		}
		suspending = false;
	}

	@Override
	public void suspend()
	{
		suspending = true;
		while (suspending);
	}
	
	public String toString() {		
		return "InfiniteProcess";
	}
}
