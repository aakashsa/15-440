package nodework;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import communication.Message;
import communication.MessageType;
import communication.WorkerInfo;

public class WorkerScanner implements Runnable {

	private WorkerInfo master;
	
	public WorkerScanner(WorkerInfo master) {
		this.master = master;
	}
	
	/**
	 * Worker scanner opens the client socket to the master,
	 * and starts scanning from command line. It forwards all valid commands
	 * to master
	 */
	@Override
	public void run() {
		Socket clientSocket;
		try {
			clientSocket = new Socket(master.getHost(), master.getPort());
			OutputStream output = clientSocket.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(output);
			out.flush();
			
			// cli arguments to processes
			ArrayList<String> cliArgs = new ArrayList<String>();
			Scanner sc = new Scanner(System.in);

			while (sc.hasNextLine()) {

				Scanner sc2 = new Scanner(sc.nextLine());

				// Scan the first word for process name or a different command
				if (sc2.hasNext()) {
					String name = sc2.next().trim(); // trim any white spaces
					
					// listjobs Command
					if (name.equals("listjobs")) {
						if (sc2.hasNext()) {
							System.out.println("[ERROR] Invalid command: listjobs does not take any arguments!");
						}
						else {
							out.writeObject(new Message(MessageType.LISTJOBS));
						}
					} else if (name.equals("quit")) {
						if (sc2.hasNext()) {
							System.out.println("[ERROR] Invalid command: quit does not take any arguments!");
						} else {
							System.out.println("Quitting node...");
							System.exit(0);
						}
					} else if (name.equals("help")) {
						if (sc2.hasNext()) {
							System.out.println("[ERROR] Invalid command: help does not take any arguments!");
						} else {
							System.out.println("Map Reduce Facility");
							System.out.println("1. listjobs - Listing All Jobs");
							System.out.println("\tUsage: listjobs");
							System.out.println("2. quit - Quit this node");
							System.out.println("\tUsage: quit");
							System.out.println("3. killjob - Kills a job");
							System.out.println("\tUsage: killjob <job_name>");
							System.out.println("4. runjob - Runs a job with its configuration files and input file");
							System.out.println("\tUsage: runjob <inputFilePath> <jobConfigDir>");
						}
					} else if (name.equals("killjob")) {
						cliArgs.clear();
						while (sc2.hasNext()) {
							cliArgs.add(sc2.next().trim());
						}
						if (cliArgs.size()!=1) {
							System.out.println("Usage: killjob <job_name>");
						} else {
							out.writeObject(new Message(MessageType.KILLJOB, cliArgs.get(0)));
						}
					} else {
						if (!name.equals("runjob")){
							System.out.println("[ERROR] Command not supported");
						}
						// Parse runjob arguments
						else {
							cliArgs.clear();
							while (sc2.hasNext()) {
								cliArgs.add(sc2.next().trim());
							}
							
							if (cliArgs.size() != 2) {
								System.out.println("Usage: runjob <inputFilePath> <jobConfigDir>");
							} else {
								out.writeObject(new Message(MessageType.RUNJOB, cliArgs.get(0), cliArgs.get(1)));
							}
						}
					}
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
