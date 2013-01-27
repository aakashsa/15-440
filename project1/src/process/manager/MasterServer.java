package process.manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import processes.ThreadProcess;

public class MasterServer implements Runnable {
	
	private static int port;
	
	public MasterServer(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		 try {
			 serverSocket = new ServerSocket(port);
		 }
		 catch (IOException e) {
			 System.out.println("Could not listen on port: " + port);
			 System.exit(-1);
		 }
		 
		 while(true) {
			 try {
				 clientSocket = serverSocket.accept();
				 System.out.println("accepted a connection from slave");
				 // need to spawn a new thread for each client thread
			 }
			 catch (IOException e) {
				 System.out.println("Accept failed: " + port);
				 System.exit(-1);
			 }
		 }
	}
	
	private ConcurrentHashMap<Long, ThreadProcess> getAllMasterProcesses() {
		return ProcessManager2.allProcesses;
	}

}
