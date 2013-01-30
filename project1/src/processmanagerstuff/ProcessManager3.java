package processmanagerstuff;

import interfaces.MigratableProcess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import processes.ThreadProcess;
import slavemanagerstuff.SlaveHelper;

public class ProcessManager3 {
	// For both Master and Slave
	private static boolean isSlave;
	private static int id = -1;

	// Only For master
	private static int numProcesses = 0;
	public static ConcurrentHashMap<Integer, String> allProcesses = new ConcurrentHashMap<Integer, String>();

	// Only for Slave
	public static ConcurrentHashMap<Integer, MigratableProcess> runningProcesses = new ConcurrentHashMap<Integer, MigratableProcess>();

	
	public ProcessManager3(boolean isSlave) {
		// TODO Auto-generated constructor stub
	}

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

			ObjectOutputStream out = null; // Slave output stream
			ObjectInputStream in = null; // Slave input stream
			Socket clientSocket = null; // Client socket
			try {
				clientSocket = new Socket(hostname, port);
				in = new ObjectInputStream(clientSocket.getInputStream());
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				out.flush();
				id = (Integer) in.readObject();
				System.out.println(" Recieved id " + id + " Back");
				// out.writeObject((Object)new String("HEY !\n"));
			} catch (UnknownHostException e) {
				System.out.println("Unknown host: " + hostname);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error in IO for host: " + hostname);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// If everything was initialized properly, we need to spawn two
			// threads
			// for reading from server, and writing to server every 5 seconds
			if (clientSocket != null && out != null && in != null) {

			}
			System.out.println("after if statement");
			//Child Reader
			
			Thread slaveThread = new Thread(new SlaveHelper(in,out,id));
			slaveThread.start();
				
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

			// Spawn a master server thread
			MasterServer server = new MasterServer(port);
			Thread serverThread = new Thread(server);
			serverThread.start();

		}
		ArrayList<String> cliArgs = new ArrayList<String>(); // cli arguments to
																// processes

		Scanner sc = new Scanner(System.in);

		while (sc.hasNextLine()) {

			Scanner sc2 = new Scanner(sc.nextLine());

			// Scan the first word for process name or a different command
			if (sc2.hasNext()) {
				String name = sc2.next();
				if (name.equals("ps")) {
					if (sc2.hasNext()) {
						System.out
								.println("Invalid command: ps does not take any arguments!");
					} else {
						for (Integer k : allProcesses.keySet()) {
							MigratableProcess proc = runningProcesses.get(k);
							System.out.println(proc.toString());
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

						Constructor ctor = null;
						Thread processThread = null;

						try {
							Class<?> processClass = Class
									.forName("processes.GrepProcess");
							System.out.println("Process class: "
									+ processClass.toString());
							Class[] ctorArgs = new Class[1];
							ctorArgs[0] = String[].class;
							ctor = processClass.getConstructor(ctorArgs);

							String[] processArgs = new String[cliArgs.size()];

							for (int i = 0; i < cliArgs.size(); i++)
								processArgs[i] = cliArgs.get(i);

							MigratableProcess process = (MigratableProcess) ctor
									.newInstance((Object) processArgs);

							String filePath = "/tmp/master"
									+ numProcesses + ".dat";
							
							File processFile = new File(filePath);
							if (!processFile.exists()) {
								processFile.createNewFile();
							}

							// System.out.println("Writing the following : ");
							FileOutputStream f_out = new FileOutputStream(
									processFile, false);
							ObjectOutputStream oos = new ObjectOutputStream(
									f_out);
							oos.writeObject((Object) process);
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
					}
				}
			}

		}
	}

}
