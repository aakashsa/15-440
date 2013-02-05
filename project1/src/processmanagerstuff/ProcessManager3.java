package processmanagerstuff;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.concurrent.ConcurrentHashMap;
import processes.ThreadProcess;

import slavemanagerstuff.SlaveHelper;

//processes.GP hi input.txt output.txt
//processes.GrepProcess hi input.txt output.txt
//processes.GrepProcess hi input.txt output.txt
//processes.ZipProcess input.txt output.txt 
//processes.ROT13 rot13input.txt outputrot2.txt
//processes.ROT13 rinput.txt outputrot2.txt

/**
 * Main ProcessManager Class That starts both the Master as well as the Slave.
 * 
 * @author aakashsa @author nikhiltibrewal
 */
public class ProcessManager3 {
	// For both Master and Slave
	private static boolean isSlave;
	private static int id = -1;

	// Only For master
	public static int numProcesses = 0;
	// Store all processes that are started on Master
	public static ConcurrentHashMap<Integer, String> allProcesses = 
			new ConcurrentHashMap<Integer, String>();

	// Only for Slave
	// Store processes running on slave
	public static ConcurrentHashMap<Integer, ThreadProcess> runningProcesses = 
			new ConcurrentHashMap<Integer, ThreadProcess>();
	// Directory where slave serializes and writes processes
	public static String fileDirectory = "serializedFiles/";

	/**
	 * The main routine. It does the following: - Parse command line arguments
	 * to decide whether to run Master or Slave - If slave, create socket, and
	 * spawn a helper thread to do the client job - If master, spawn a thread
	 * for listening to more clients - Once either master or slave are done,
	 * this thread starts a new thread that starts scanning stdin for
	 * commands
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		int port = 4444; // Default port
		String hostname = "localhost"; // Default hostname for client

		if (args.length >= 1 && args[0].equals("-c")) {

			// Check if slave has been supplied with proper arguments
			if (args.length <= 1) {
				System.out.println("ERROR: Usage for Slave PM: java " +
						"ProcessManager -c <host> <port>");
				System.exit(0);
			} else if (args.length > 3) {
				System.out.println("ERROR: Extra arguments supplied! " +
						"Usage for Slave PM: java ProcessManager -c <host> <port>");
				System.exit(0);
			}
			// Slave is supplied with either 2 or 3 arguments
			isSlave = true;
			hostname = args[1]; // Hostname for the client

			//Parse for a port number if the slave was supplied with one
			if (args.length == 3) {
				try {
					port = Integer.valueOf(args[2]).intValue();
					if (port < 1024 || port > 49151) {
						System.out.println("ERROR: Port number must " +
								"range from 1024 - 49151 (including)");
						System.exit(0);
					}
				} catch (Exception e) {
					System.out.println("ERROR: Port number must be an integer!");
					System.exit(0);
				}
			}

			System.out.println("Connecting to Master on port number: " + port + 
					" and hostname: " + hostname);
			OutputStream output = null;
			InputStream input = null;
			ObjectOutputStream out = null; // Slave output stream
			ObjectInputStream in = null; // Slave input stream
			Socket clientSocket = null; // Client socket
			try {
				clientSocket = new Socket(hostname, port);
				output = clientSocket.getOutputStream();
				input = clientSocket.getInputStream();
				out = new ObjectOutputStream(output);
				out.flush();
				in = new ObjectInputStream(input);

				// Master Assigns Every Slave a unique id ( integer ).
				id = (Integer) in.readObject();
				if (id != -1)
					System.out.println("Slave Number: " + id);
				else {
					System.out.println("Server Busy. Shutting Down... Try Again");
					System.exit(0);
				}
			} catch (UnknownHostException e) {
				System.out.println("ERROR: Unknown host: " + hostname + 
						". Please check the hostname with the Master!");
			} catch (IOException e) {
				System.out.println("ERROR: IO error for host: " + hostname + 
						". Make sure the Master is running!");
			} catch (ClassNotFoundException e) {
				System.out.println("ERROR: Couldn't find class of " +
						"serialized object!");
			}

			// If everything was initialized properly, we need to spawn a thread
			// for reading and writing to server
			if (clientSocket != null && output != null && input != null) {
				System.out.println("Connected to Master!");
				Thread slaveThread = 
						new Thread(new SlaveHelper(id, clientSocket, out, in));
				slaveThread.start();
			} else {
				System.out.println("ERROR: Client wasn't able to open " +
						"socket or streams! Quitting...");
				System.exit(0);
			}
		} else {
			isSlave = false;
			
			//Delete any previous files in the serialized files directory
			File folder = new File(fileDirectory);
			File[] files = folder.listFiles();
			for (File f : files) {
				f.delete();
			}

			// Check if master has been supplied with proper arguments
			if (args.length > 1) {
				System.out.println("ERROR: Extra arguments supplied. " +
						"Usage for Master PM: java ProcessManager <port>");
				System.exit(0);
			}

			// Scan for preferred port number; if not provided, go to default
			if (args.length == 1) {
				try {
					port = Integer.valueOf(args[0]).intValue();
					if (port < 1024 || port > 49151) {
						System.out.println("ERROR: Port number must " +
								"range from 1024 - 49151 (including)");
						System.exit(0);
					}
				} catch (Exception e) {
					System.out.println("ERROR: Port number must be an integer!");
					System.exit(0);
				}
			}
			//Print the hostname of master process manager for slaves 
			try {
				hostname = InetAddress.getLocalHost().getHostName();
				System.out.println("Master running on port number: " + port
						+ " and hostname: " + hostname);
			} catch (UnknownHostException e) {
				System.out.println("ERROR: Master couldn't get own hostname. " +
						"Quitting...");
				System.exit(0);
			}

			// Spawn a master server thread to listen for slaves
			MasterServer server = new MasterServer(port);
			Thread serverThread = new Thread(server);
			serverThread.start();
		}

		// Main Scanner Class That Does ps , quit and process scanning
		Scan s = new Scan(isSlave);
		Thread t = new Thread(s);
		t.start();
	}
}
