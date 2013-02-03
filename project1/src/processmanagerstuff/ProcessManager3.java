package processmanagerstuff;

import interfaces.MigratableProcess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import processes.ThreadProcess;

import slavemanagerstuff.SlaveHelper;

//processes.GP hi input.txt output.txt
//processes.GrepProcess hi input.txt output.txt
//processes.GrepProcess hi input.txt output.txt
//processes.ZipProcess input.txt output.txt 
public class ProcessManager3 {
	// For both Master and Slave
	private static boolean isSlave;
	private static int id = -1;

	// Only For master
	private static int numProcesses = 0;
	public static ConcurrentHashMap<Integer, String> allProcesses = new ConcurrentHashMap<Integer, String>();

	// Only for Slave
	public static ConcurrentHashMap<Integer, ThreadProcess> runningProcesses = new ConcurrentHashMap<Integer, ThreadProcess>();
	public static String fileDirectory = "/tmp/";//"/afs/andrew.cmu.edu/usr5/aakashsa/public/";

	/**
	 * The main routine. It does the following:
	 * - Parse command line arguments to decide whether to run Master or Slave
	 * - If slave, create socket, and spawn a helper thread to do the client job
	 * - If master, spawn a thread for listening to more clients
	 * - Once either master or slave are done, this thread starts accepting
	 *   command line options to run processes for Master, and for commands like
	 *   quit and ps for slave.
	 * @param args
	 */
	public static void main(String[] args) {

		int port = 4444; // Default port
		String hostname = "localhost"; // Default hostname for client

		if (args.length < 1) {
			System.out.println("ERROR: Need at least one argument.");
			System.out.println("Usage for Master PM: java ProcessManager <portNumber>");
			System.out.println("Usage for Slave PM: java ProcessManager -c <hostname> <portNumber>");
			return;
		}
		if (args[0].equals("-c")) {
			
			//Check if slave has been supplied with proper arguments
			if (args.length <= 1) {
				System.out.println("ERROR: Usage for Slave PM: java ProcessManager -c <hostname> <portNumber>");
				return;
			} else if (args.length > 3) {
				System.out.println("ERROR: Extra arguments supplied! Usage for Slave PM: java ProcessManager -c <hostname> <portNumber>");
				return;
			}
			//Slave is supplied with either 2 or 3 arguments
			isSlave = true;
			System.out.println("Slave PM");
			hostname = args[1]; // Hostname for the client
			
			if (args.length == 3) {
				try {
					port = Integer.valueOf(args[2]).intValue();
					if (port < 1024 || port > 49151) {
						System.out.println("ERROR: Port number must range from 1024 - 49151 (including)");
						return;
					}
				} catch (Exception e) {
					System.out.println("ERROR: Port number must be an integer!");
				}
			}
			
			System.out.println("Connecting to Master on port number: " + port + " and hostname: " + hostname + " ... ");
			OutputStream output = null;
			InputStream input = null;
			ObjectOutputStream out = null; //Slave output stream
			ObjectInputStream in = null; //Slave input stream
			Socket clientSocket = null; //Client socket
			try {
				clientSocket = new Socket(hostname, port);
				output = clientSocket.getOutputStream();
				input = clientSocket.getInputStream();
				out = new ObjectOutputStream(output);
				out.flush();
				in = new ObjectInputStream(input);

				id = (Integer) in.readObject();
				System.out.println("Connected to Master!");
				System.out.println(" Recieved id " + id + " Back (process manager)");

			} catch (UnknownHostException e) {
				System.out.println("ERROR: Unknown host: " + hostname + ". Please check the hostname with the Master!");
			} catch (IOException e) {
				System.out.println("ERROR: IO error for host: " + hostname + ". Make sure the Master is running!");
			} catch (ClassNotFoundException e) {
				System.out.println("ERROR: Couldn't find class of searlized object!");
			}

			// If everything was initialized properly, we need to spawn a thread
			// for reading from server, and writing to server
			if (clientSocket != null && output != null && input != null) {
				Thread slaveThread = new Thread(new SlaveHelper(id, clientSocket, out, in));
				slaveThread.start();
			} else {
				System.out.println("ERROR: Client wasn't able to open socket or streams! Quitting...");
				System.exit(-1);
			}
		} else {
			isSlave = false;
			System.out.println("Master PM");

			//Check if master has been supplied with proper arguments
			if (args.length > 1) {
				System.out.println("ERROR: Extra arguments supplied. Usage for Master PM: java ProcessManager <portNumber>");
			}
			
			// Scan for a user preferred port number; if none provided, go to default
			if (args.length == 1) {
				try {
					port = Integer.valueOf(args[0]).intValue();
					if (port < 1024 || port > 49151) {
						System.out.println("ERROR: Port number must range from 1024 - 49151 (including)");
						return;
					}
				} catch (Exception e) {
					System.out.println("ERROR: Port number must be an integer!");
					return;
				}
			}
			try {
				hostname = InetAddress.getLocalHost().getHostName();
				System.out.println("Master running on port number: " + port + " and hostname: " + hostname);
			} catch (UnknownHostException e) {
				System.out.println("ERROR: Master couldn't get own hostname");
			}

			// Spawn a master server thread to listen for slaves
			MasterServer server = new MasterServer(port);
			Thread serverThread = new Thread(server);
			serverThread.start();
		}

		// cli arguments to processes
		ArrayList<String> cliArgs = new ArrayList<String>();
		Scanner sc = new Scanner(System.in);

		while (sc.hasNextLine()) {

			Scanner sc2 = new Scanner(sc.nextLine());

			// Scan the first word for process name or a different command
			if (sc2.hasNext()) {
				String name = sc2.next().trim(); //trim any white spaces around command
				if (name.equals("ps")) {
					if (sc2.hasNext()) {
						System.out.println("Invalid command: ps does not take any arguments!");
					} else {
						if (runningProcesses.size() == 0)
							System.out.println("No running process");
						for (ThreadProcess tp : runningProcesses.values()) {
							System.out.println(tp.getProcess().toString());
						}
					}
				} else if (name.equals("quit")) {
					if (sc2.hasNext()) {
						System.out.println("Invalid commant: quit does not take any arguments!");
					} else {
						System.out.println("Quitting...");
						System.exit(-1);
					}
				} else {
					if (!isSlave) {
						// Parse arguments to a new process
						cliArgs.clear();
						while (sc2.hasNext()) {
							cliArgs.add(sc2.next());
						}

						// Parse process and run it in a new thread
						Constructor<?> ctor = null;
						try {
							Class<?> processClass = Class.forName(name);
							Class<?>[] ctorArgs = new Class[1];
							ctorArgs[0] = String[].class;
							ctor = processClass.getConstructor(ctorArgs);

							String[] processArgs = new String[cliArgs.size()];

							for (int i = 0; i < cliArgs.size(); i++)
								processArgs[i] = cliArgs.get(i);

							MigratableProcess process = (MigratableProcess) ctor.newInstance((Object) processArgs);

							//Write process to file on disk
							String filePath = fileDirectory + numProcesses + ".dat";

							File processFile = new File(filePath);
							if (!processFile.exists()) {
								processFile.createNewFile();
							}

							FileOutputStream f_out = new FileOutputStream(processFile, false);
							ObjectOutputStream oos = new ObjectOutputStream(f_out);
							oos.writeObject((Object) process);
							oos.flush();
							oos.close();
							
							//Add new process to all processes collection
							allProcesses.put(numProcesses, filePath);
							numProcesses++;
							System.out.println("Done Writing Process (process manager)");
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							System.out.println("ERROR: Process " + name + " is not supported!");
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
							System.out.println("ERROR: Couldn't parse process: " + name);
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("ERROR: Trouble starting process: " + name);
						}
					} else {
						System.out.println("ERROR: Command not supported by slave PM");
					}
				}
			}
		}
	}
}
