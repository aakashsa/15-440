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
import java.net.InetAddress;

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
	 * @param args
	 */
	public static void main(String[] args) {

		int port = 4444; // Default port
		String hostname = "localhost"; // Default hostname for client
		// *******************Store connections to all clients
		if (args.length < 1) {
			System.out
					.println("ERROR: Need at least one argument.\nUsage for Master PM: java ProcessManager <portNumber>\nUsage for Slave PM: java ProcessManager -c <hostname> <portNumber>");
			return;
		}
		if (args[0].equals("-c")) {
			isSlave = true;
			// ******************* Slave
			System.out.println("Slave PM");
			hostname = args[1]; // Hostname for the client
			if (args.length < 3) {
				System.out
						.println("Usage: java ProcessManager -c <hostname> <portNumber>\n"
								+ "Now using port number: "
								+ port
								+ " and hostname: " + hostname);
			} else {
				port = Integer.valueOf(args[2]).intValue();
			}
			OutputStream output = null;
			InputStream input = null;
			ObjectOutputStream out = null;
			ObjectInputStream in = null; // Slave input stream
			Socket clientSocket = null; // Client socket
			try {
				clientSocket = new Socket(hostname, port);
				output = clientSocket.getOutputStream();
				input = clientSocket.getInputStream();
				System.out.println("Getting Input Stream Slave PM 68");
				out = new ObjectOutputStream(output);
				out.flush();
				in = new ObjectInputStream(input);

				id = (Integer) in.readObject();
				System.out.println(" Recieved id " + id + " Back");

			} catch (UnknownHostException e) {
				System.out.println("Unknown host: " + hostname + ". Please check the hostname with the Master!");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error in IO for host: " + hostname + ". Make sure the Master is running!");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			// If everything was initialized properly, we need to spawn two
			// threads
			// for reading from server, and writing to server every 5 seconds
			if (clientSocket != null && output != null && input != null) {
				Thread slaveThread = new Thread(new SlaveHelper(input, output,
						id, clientSocket, out, in));
				slaveThread.start();
			} else {
				System.out
						.println("ERROR: Client wasn't able to open socket or streams!");
				System.exit(-1);
			}
		} else {
			isSlave = false;
			// ******************* Master
			System.out.println("Master PM");
			// Scan for a user preferred port number; if none provided, go to
			// default
			if (args.length < 1) {
				System.out.println("Usage: java ProcessManager <portNumber>\n"
						+ "Now using port number=" + port);
			} else {
				port = Integer.valueOf(args[0]).intValue();
			}
			try {
				String localhostname = InetAddress.getLocalHost().getHostName();
				System.out.println("Local Host Address is : " + localhostname);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Spawn a master server thread
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
				String name = sc2.next().trim();
				if (name.equals("ps")) {
					if (sc2.hasNext()) {
						System.out
								.println("Invalid command: ps does not take any arguments!");
					} else {
						for (ThreadProcess tp : runningProcesses.values()) {
							System.out.println(tp.getProcess().toString());
						}
					}
				} else if (name.equals("quit")) {
					if (sc2.hasNext()) {
						System.out
								.println("Invalid commant: quit does not take any arguments!");
					} else {
						System.out.println("Quitting...");
						System.exit(-1);
					}
				} else {
					if (!isSlave) {
						// need to start a new process; parse arguments
						cliArgs.clear();
						while (sc2.hasNext()) {
							cliArgs.add(sc2.next());
						}
						// System.out.println("Starting new process: name: "+
						// name + ", arguments: " + cliArgs.toString());

						// Now need to parse process and run it in a new thread

						Constructor<?> ctor = null;

						try {
							Class<?> processClass = Class.forName(name);
							System.out.println("Process class: "
									+ processClass.toString());
							Class<?>[] ctorArgs = new Class[1];
							ctorArgs[0] = String[].class;
							ctor = processClass.getConstructor(ctorArgs);

							String[] processArgs = new String[cliArgs.size()];

							for (int i = 0; i < cliArgs.size(); i++)
								processArgs[i] = cliArgs.get(i);

							MigratableProcess process = (MigratableProcess) ctor
									.newInstance((Object) processArgs);

							String filePath = fileDirectory + numProcesses + ".dat";

							File processFile = new File(filePath);
							if (!processFile.exists()) {
								processFile.createNewFile();
							}

							FileOutputStream f_out = new FileOutputStream(
									processFile, false);
							ObjectOutputStream oos = new ObjectOutputStream(
									f_out);
							oos.writeObject((Object) process);
							oos.flush();
							oos.close();
							allProcesses.put(numProcesses, filePath);
							numProcesses++;
							System.out.println("Done Writing Process \n");
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							System.out.println("ERROR: Process " + name
									+ " is not supported!");
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
							System.out.println("ERROR: Process " + name
									+ " is a weird process!");
						} catch (Exception e) {
							e.printStackTrace();
							System.out
									.println("ERROR: trouble starting process: "
											+ name);
						}
					} else {
						System.out
								.println("ERROR: Command not supported by slave PM");
					}
				}
			}
		}
	}
}
