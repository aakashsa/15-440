package master;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Command line scanner for commands to map reduce master.
 *
 */
public class Scan implements Runnable {

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
				
				// listjobs Command
				if (name.equals("listjobs")) {
					if (sc2.hasNext()) {
						System.out.println("Invalid command: listjobs does not take any arguments!");
					}
					else {
						// LIST ALL JOBS
					}
				} else if (name.equals("quit")) {
					if (sc2.hasNext()) {
						System.out.println("Invalid command: quit does not take any arguments!");
					} else {
						System.out.println("Quitting...");
						System.exit(0);
					}
				} else {
					if (!name.equals("runjob")){
						System.out.println("ERROR: Command not supported");
					}
					// Parse runjob arguments
					else {
						cliArgs.clear();
						while (sc2.hasNext()) {
							cliArgs.add(sc2.next().trim());
						}
						
						if (cliArgs.size() != 3) {
							System.out.println("Usage: runjob <inputFilePath> <configFilePath> <jobConfigDir>");
						} else {
							String jobConfigDir = cliArgs.get(2);
							String inputFile = cliArgs.get(0);
							String configFile = cliArgs.get(1);
							HadoopMaster.counter++;
							new Thread(new JobThread(inputFile, configFile, jobConfigDir)).start();
						}
					}
				}
			}
		}
	}
}
