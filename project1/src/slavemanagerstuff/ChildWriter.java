package slavemanagerstuff;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

import processes.ThreadProcess;
import processmanagerstuff.ProcessManager2;

public class ChildWriter implements Runnable {

	private ObjectOutputStream out = null;

	public ChildWriter(ObjectOutputStream out){
		System.out.println("Child Writer Spawned");
		this.out = out;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(true){
				System.out.println("Writing Every 7 Seconds");
				Thread.sleep(7000);
				try {
					//System.out.println("Writing the following : ");
					
					for (Long k : ProcessManager2.allProcesses.keySet()) {
						System.out.println("Writing THIS ONE !! ");
						ThreadProcess tp = ProcessManager2.allProcesses.get(k);
						System.out.println(tp.getProcess().toString());
						tp.getProcess().suspend();
						out.writeObject((Object)tp.getProcess());
						System.out.println("Writen !! ");
						ProcessManager2.allProcesses.remove(k);
					}

					
//					System.out.println(ProcessManager2.allProcesses.get(0).getProcess().toString());
//					out.writeObject((Object)ProcessManager2.allProcesses.get(0).getProcess());
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}


