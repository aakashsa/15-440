package slavemanagerstuff;

import interfaces.MigratableProcess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import processes.ThreadProcess;
import processmanagerstuff.ProcessManager2;

public class ChildReader  implements Runnable {

	private ObjectInputStream in = null;

	public ChildReader(ObjectInputStream in){
		System.out.println("Child Reader Spawned");
		this.in = in;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		System.out.println("Spawned a New Thread for Connection");
		while(true){
		try {
			MigratableProcess process = (MigratableProcess) in.readObject();
			System.out.println("Received Process Back in Child Reader= " + process.toString());

			Thread processThread = null;
			processThread = new Thread(process);
			ThreadProcess tp = new ThreadProcess(processThread, process);
			ProcessManager2.allProcesses.put(processThread.getId(), tp);  //Add to all processes collection
			System.out.println("Starting New Process Back");
			processThread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
	}

}
