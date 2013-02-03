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

/**
 * This class is responsible for listening for new slaves.
 * It accepts connections from upto a limited number.
 * For each slave that successfully connects, it spawns a new
 * client thread. It also maintains a list of output streams of all
 * slaves and passes that down to the Load Balancer.
 * @author nikhiltibrewal
 *
 */
public class MasterServer implements Runnable {

	private int port;
	private static int maxClientsCount = 500;
	
	//Store output streams of all clients to write things to the client
	public static ArrayList<ObjectOutputStream> clientOutputStreamList = new ArrayList<ObjectOutputStream>();
	public static int clientNumbers = 1;

	public MasterServer(int port) {
		this.port = port;
		
		//Start the load balancer list. Pass it clients output streams
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
			System.out.println("ERROR: Could not listen on port: " + port);
			System.exit(-1);
		}

		while (true) {
			try {
				//Accept connections and initialize client streams
				System.out.println("Listening in Master Server, clientsocket: " + clientSocket);
				clientSocket = serverSocket.accept();
				OutputStream output = clientSocket.getOutputStream();
				InputStream input = clientSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				ObjectInputStream in = new ObjectInputStream(input);
				
				if (clientOutputStreamList.size() == maxClientsCount) {
					PrintWriter os = new PrintWriter(output, true);
					os.print("Server Busy. Try Later \n");
					os.flush();
					os.close();
					clientSocket.close();
				} else {
					System.out.println("accepted connection, spawning client thread... (master server)");
					clientOutputStreamList.add(out);
					ClientThread ct = new ClientThread(clientNumbers, out, in);
					new Thread(ct).start();
					clientNumbers++;
				}
			}
			catch (IOException e) {
				System.out.println("ERROR: Server failure in accepting connections on port: " + port + " or in initializing client streams");
				System.exit(-1);
			}
		}
	}
}
