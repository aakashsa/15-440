package processmanagerstuff;

import interfaces.MigratableProcess;

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


/**
 * The process manager is started with arguments. Must have portNumber as one argument
 * @author nikhiltibrewal
 *
 */
public class ProcessManager2 implements Runnable {
	
	public static ConcurrentHashMap<Long, ThreadProcess> allProcesses = new ConcurrentHashMap();
	
	public static void main(String[] args) {
		
		int port = 4444; //Default port
		String hostname = "localhost"; //Default hostname for client
		// *******************Store connections to all clients
		if (args.length < 1) {
			System.out.println("ERROR: Need at least one argument.\nUsage for Master PM: java ProcessManager <portNumber>\nUsage for Slave PM: java ProcessManager -c <hostname> <portNumber>");
			return;
		}
		if (args[0].equals("-c")){
			//    ******************* Slave
			System.out.println("Slave PM");
			hostname = args[1]; //Hostname for the client
			if (args.length < 3) {
				System.out.println("Usage: java ProcessManager -c <hostname> <portNumber>\n"
			              + "Now using port number: " + port + " and hostname: " + hostname);
			} else {
				port = Integer.valueOf(args[2]).intValue();
			}
			
			ObjectOutputStream out = null; //Slave output stream
			ObjectInputStream in = null; //Slave input stream
			Socket clientSocket = null; //Client socket
			
			try {
				clientSocket = new Socket(hostname, port);
				in = new ObjectInputStream(clientSocket.getInputStream());
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				out.flush();
			} catch (UnknownHostException e) {
				System.out.println("Unknown host: " + hostname);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error in IO for host: " + hostname);
			}
			
			// If everything was initialized properly, we need to spawn two threads
			// for reading from server, and writing to server every 5 seconds
			if (clientSocket != null && out != null && in != null) {
				
			}
			System.out.println("after if statement");
			Thread pm = new Thread(new ProcessManager2());
			pm.start();
			System.out.println("after starting child");
		}
		else {
			//    ******************* Master
			System.out.println("Master PM");
			//Scan for a user preferred port number; if none provided, go to default
			if (args.length < 1) {
				System.out.println("Usage: java ProcessManager <portNumber>\n"
			              + "Now using port number=" + port);
			} else {
				port = Integer.valueOf(args[0]).intValue();
			}
			
			//Spawn a master server thread
			MasterServer server = new MasterServer(port);
			Thread serverThread = new Thread(server);
			serverThread.start();
			Thread pm = new Thread(new ProcessManager2());
			pm.start();
		}
	}

	@Override
	public void run() {
		// Keep scanning for input from standard in for new processes
		
		ArrayList<String> cliArgs = new ArrayList<String>(); //cli arguments to processes
		
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
						for (Long k : allProcesses.keySet()) {
							ThreadProcess tp = allProcesses.get(k);
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
					// need to start a new process; parse arguments
					cliArgs.clear();
					while (sc2.hasNext()) {
						cliArgs.add(sc2.next());
					}
					System.out
						.println("Starting new process: name: "
							+ name + ", arguments: " + cliArgs.toString());

					// Now need to parse process and run it in a new thread
					
					Constructor ctor = null;
					Thread processThread = null;
					
					try {
						Class<?> processClass = Class.forName("processes.GrepProcess");
						System.out.println("Process class: " + processClass.toString());
						Class[] ctorArgs = new Class[1];
						ctorArgs[0] = String[].class;
						ctor = processClass.getConstructor(ctorArgs);
						
						String[] processArgs = new String[cliArgs.size()];
						
						for (int i = 0; i < cliArgs.size(); i++)
							processArgs[i] = cliArgs.get(i);
						
						MigratableProcess process = (MigratableProcess) ctor.newInstance((Object) processArgs);
						processThread = new Thread(process);
						ThreadProcess tp = new ThreadProcess(processThread, process);
						allProcesses.put(processThread.getId(), tp);  //Add to all processes collection
						processThread.start();
						
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						System.out.println("ERROR: Process " + name + " is not supported!");
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
						System.out.println("ERROR: Process " + name + " is a weird process!");
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("ERROR: trouble starting process: " + name);
					}
				}
			}
		}
	}
}