package processmanagerstuff;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MasterServer implements Runnable {

	private int port;
	private static int maxClientsCount = 500;
	//private static final ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();
	public static ArrayList<ObjectOutputStream> clientOutputStreamList = new ArrayList<ObjectOutputStream>();
	public static int clientNumbers = 1;

	public MasterServer(int port) {
		this.port = port;
		LoadBalancer lb = new LoadBalancer(clientOutputStreamList);
		(new Thread(lb)).start();
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Could not listen on port: " + port);
			System.exit(-1);
		}

		while (true) {
			try {
				System.out.println("Listening in Master Server, clientsocket: " + clientSocket);
				clientSocket = serverSocket.accept();
				OutputStream output = clientSocket.getOutputStream();
				InputStream input = clientSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				ObjectInputStream in = new ObjectInputStream(input);
				
				if (clientOutputStreamList.size() == maxClientsCount) {
					PrintWriter os = new PrintWriter(
							output, true);
					os.print("Server Busy Try Later \n");
					os.flush();
					os.close();
					clientSocket.close();
				} else {
					System.out.println("accepted connection, spawning client thread...");
					clientOutputStreamList.add(out);
					ClientThread ct = new ClientThread(output, input, clientNumbers, out, in);
					//clientThreads.add(ct);
					new Thread(ct).start();
					clientNumbers++;
				}
			}
			// need to spawn a new thread for each client thread
			catch (IOException e) {
				System.out.println("Accept failed: " + port);
				System.exit(-1);
			}
		}
	}
}
