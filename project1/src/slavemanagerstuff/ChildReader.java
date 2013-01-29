package slavemanagerstuff;

import interfaces.MigratableProcess;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import processes.ThreadProcess;
import processmanagerstuff.HeaderPacket;
import processmanagerstuff.ProcessManager2;

public class ChildReader  implements Runnable {

	private ObjectInputStream in = null;
	private int id = -1;

	public ChildReader(ObjectInputStream in, int id){
		System.out.println("Child Reader Spawned");
		this.in = in;
		this.id = id;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//System.out.println("Spawned a New Thread for Connection");
		try {
		//int numProcess =  (Integer) in.readObject();
		//System.out.println(" Recieved Int which is Size " + numProcess);
		while(true){
			HeaderPacket header = (HeaderPacket) in.readObject();

			System.out.println(" Recieved Header Packet " + " NumProcess " + header.getNumProcess() + " FilePath "+ header.getFilePath() + " client Id = " + header.getId());
			FileInputStream fileIn = new FileInputStream(header.getFilePath());
			ObjectInputStream oIn = new ObjectInputStream(fileIn);
			
			for (int i =0;i<header.getNumProcess();i++){
				MigratableProcess process = (MigratableProcess) oIn.readObject();
				//System.out.println("Received Process Back in Child Reader= " + process.toString());

				Thread processThread = null;
				processThread = new Thread(process);
				ThreadProcess tp = new ThreadProcess(processThread, process);
				ProcessManager2.allProcesses.put(processThread.getId(), tp);  //Add to all processes collection
				System.out.println("Starting New Process Back");
				processThread.start();
			}
			} 
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
