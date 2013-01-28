package processmanagerstuff;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import processes.ThreadProcess;

public class MasterServer implements Runnable {

	private static int port;
	private static int maxClientsCount = 500;
	private static final ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();

	
	public MasterServer(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Could not listen on port: " + port);
			System.exit(-1);
		}

		while (true) {
			try {
				clientSocket = serverSocket.accept();
				if (clientThreads.size()== maxClientsCount) {
			          System.out.println("Server Busy");
			          clientSocket.close();
			    }	
				else {
					clientThreads.add(new ClientThread(clientSocket));	
					new Thread(clientThreads.get(clientThreads.size()-1)).start();
			    }
			}
			// need to spawn a new thread for each client thread
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
