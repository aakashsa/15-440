package master;

import interfaces.JobConfiguration;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Scanner;

import lib.Job;
import lib.Utils;

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
						System.out.println("[ERROR] Invalid command: listjobs does not take any arguments!");
					}
					else {
						// List all jobs
						System.out.println("[INFO] Current Jobs:");
						for (Entry<Integer, Job> e : HadoopMaster.jobMap.entrySet()){
							System.out.println("  Job ID = " + e.getKey() + ", Job name = " + e.getValue().getJobName());
						}
					}
				} else if (name.equals("quit")) {
					if (sc2.hasNext()) {
						System.out.println("[ERROR] Invalid command: quit does not take any arguments!");
					} else {
						System.out.println("Quitting node...");
						System.exit(0);
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
							// Initialize the Job Object
							HadoopMaster.jobCounter++;
							String inputFile = cliArgs.get(0);
							String jobConfigDir = cliArgs.get(1);
							Job job = null;
							try {
								Class<?> jobConfClass = Class.forName(jobConfigDir + ".JobSetupClass");
								JobConfiguration jConf = (JobConfiguration) jobConfClass.newInstance();
								job = jConf.setup();
								// Performing Sanity Checks on the Job provided
								Utils.performJobSanityChecks(job);
								HadoopMaster.jobMap.put(HadoopMaster.jobCounter, job);
								new Thread(new JobThread(inputFile,job)).start();
							} catch (ClassNotFoundException e) {
								System.out.println("[ERROR] JobSetupClass class not found. Make sure the jobConfigDir argument is correct.");
								continue;
							} catch (InstantiationException e) {
								System.out.println("[ERROR] Job configuration instantiation error");
								continue;
							} catch (IllegalAccessException e) {
								System.out.println("[ERROR] Job configuration instantiation error");
								continue;
							} catch (IllegalArgumentException e) {
								System.out.println("[ERROR] " + e.getMessage());
								continue;
							}
						}
					}
				}
			}
		}
	}
}
