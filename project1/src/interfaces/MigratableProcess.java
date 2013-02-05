package interfaces;
import java.io.Serializable;

/** 
 * Each process will implement MigratableProcess and 
 * the processes will limit their I/O to files accessed via the 
 * TransactionalFileInputStream and TransactionalFileOutputStream classes
 * 
 */
public interface MigratableProcess extends Runnable, Serializable {

	public String toString();	

	public void suspend();
	
}
