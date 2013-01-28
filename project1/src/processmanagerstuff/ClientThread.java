package processmanagerstuff;

import interfaces.MigratableProcess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import processes.ThreadProcess;

public class ClientThread implements Runnable {

	private ObjectOutputStream out ;
	private ObjectInputStream in ;
	
	public ClientThread () {
		
		
	}
	
	public ClientThread(Socket clientSocket) {
		try {
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("accepted a connection from slave");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Spawned a New Thread for Connection");
		while(true){
		try {
			MigratableProcess p = (MigratableProcess) in.readObject();
			System.out.println("Received Process = " + p.toString());
			
//			ConcurrentHashMap<Long, ThreadProcess> allProcesses = (ConcurrentHashMap<Long, ThreadProcess>) in.readObject();
//			System.out.println("Received allProces Printing :");
//			for (Long k : allProcesses.keySet()) {
//				ThreadProcess tp = allProcesses.get(k);
//				System.out.println(tp.getProcess().toString());
//				
//			}
			System.out.println("Done Printing");
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
