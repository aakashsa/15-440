package processmanagerstuff;

import interfaces.MigratableProcess;
import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Scanner;
import processes.ThreadProcess;

/**
 * Command line scanner for commands to process manager.
 *
 */
public class Scan implements Runnable {
	private boolean isSlave;

	public Scan(boolean isSlave) {
		this.isSlave = isSlave;
	}

	@Override
	public void run() {

		// cli arguments to processes
		ArrayList<String> cliArgs = new ArrayList<String>();
		Scanner sc = new Scanner(System.in);

		while (sc.hasNextLine()) {

			Scanner sc2 = new Scanner(sc.nextLine());

			// Scan the first word for process name or a different command
			if (sc2.hasNext()) {
				String name = sc2.next().trim(); // trim any white spaces
				
				// ps Command
				if (name.equals("ps")) {
					if (sc2.hasNext()) {
						System.out.println("Invalid command: " +
								"ps does not take any arguments!");
					}
					else {
						if (!isSlave) {
							System.out.println("No running processes on " +
									"Master");
						} else {
							if (ProcessManager3.runningProcesses.size() == 0)
								System.out.println("No running processes");
							for (ThreadProcess tp : ProcessManager3.runningProcesses.values()) {
								System.out.println(tp.getProcess().toString());
							}
						}
					}
				} else if (name.equals("quit")) {
					if (sc2.hasNext()) {
						System.out.println("Invalid commant: quit does " +
								"not take any arguments!");
					} else {
						System.out.println("Quitting...");
						System.exit(0);
					}
				} else {
					// Parsing Process Command
					if (!isSlave) {
						// Parse arguments to a new process
						cliArgs.clear();
						while (sc2.hasNext()) {
							cliArgs.add(sc2.next().trim());
						}

						// Parse process and run it in a new thread
						Constructor<?> ctor = null;
						try {
							String processName = name.contains("processes.") ? name : "processes." + name;
							Class<?> processClass = Class.forName(processName);
							Class<?>[] ctorArgs = new Class[1];
							ctorArgs[0] = String[].class;
							ctor = processClass.getConstructor(ctorArgs);

							String[] processArgs = new String[cliArgs.size()];

							for (int i = 0; i < cliArgs.size(); i++)
								processArgs[i] = cliArgs.get(i);
							
							// Instantiating a Migratable process	
							MigratableProcess process = (MigratableProcess) ctor
									.newInstance((Object) processArgs);

							// Write process to unique file on disk
							String filePath = ProcessManager3.fileDirectory
									+ ProcessManager3.numProcesses + ".dat";

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

							// Add new process to all processes collection
							ProcessManager3.allProcesses.put(
									ProcessManager3.numProcesses, filePath);
							ProcessManager3.numProcesses++;
						} catch (ClassNotFoundException e) {
							System.out.println("ERROR: Process " + name
									+ " is not supported!");
						} catch (NoSuchMethodException e) {
							System.out
									.println("ERROR: Couldn't parse process: "
											+ name);
						} catch (Exception e) {
							String err = e.getCause() == null ? "" : 
								" - " + e.getCause().getLocalizedMessage();
							System.out.println("ERROR: Trouble starting process: "
												+ name + err);
						}
					} else {
						System.out.println("ERROR: Command not supported " +
								"by slave PM");
					}
				}
			}
		}
	}
}
