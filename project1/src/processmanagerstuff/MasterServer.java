package processmanagerstuff;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.omg.CORBA.portable.OutputStream;

import processes.ThreadProcess;
import slavemanagerstuff.ChildWriter;
import sun.misc.Cleaner;

public class MasterServer implements Runnable {

	private static int port;
	private static int maxClientsCount = 500;
	private static final ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();
	public static ArrayList<Socket> clientSocketList = new ArrayList<Socket>();
	public static int clientNumbers = 1;

	public MasterServer(int port) {
		this.port = port;
		LoadBalancer lb = new LoadBalancer(clientSocketList);
		(new Thread(lb)).start();
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
				System.out.println("Listening in Master Server");
				clientSocket = serverSocket.accept();

				if (clientThreads.size() == maxClientsCount) {
					PrintWriter os = new PrintWriter(
							clientSocket.getOutputStream(), true);
					os.print("Server Busy Try Later \n");
					clientSocket.close();
				} else {
					clientSocketList.add(clientSocket);
					clientThreads.add(new ClientThread(clientSocket,clientNumbers));
					new Thread(clientThreads.get(clientThreads.size() - 1))
							.start();
			
				}
				clientNumbers++;
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
