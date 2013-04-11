package master;

import interfaces.JobConfiguration;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import lib.Job;
import lib.Utils;
import test.JobSetupClass;
import test.JobSetupClass;


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
						System.out.println("[Error] Invalid command: listjobs does not take any arguments!");
						
					}
					else {
						// LIST ALL JOBS
						Set<Integer> jobIds = HadoopMaster.jobMap.keySet();
						System.out.println("[Info] Current Job List");
						for (int id : jobIds){
							System.out.println("Job_Id = " + id + " " + HadoopMaster.jobMap.get(id));
						}
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
						
						if (cliArgs.size() != 2) {
							System.out.println("Usage: runjob <inputFilePath> <jobConfigDir>");
						} else {
							// Initializing the Job Object
							HadoopMaster.counter++;
							String jobConfigDir = cliArgs.get(1);
							String inputFile = cliArgs.get(0);
							Job job = null;
							try {
								Class<?> jobConfClass = Class.forName(jobConfigDir + ".JobSetupClass");
								JobConfiguration jConf = (JobConfiguration) jobConfClass.newInstance();
								Job[] jobs = jConf.setup();
								job = jobs[0];
								// Performing Sanity Checks on the Job provided
								Utils.performJobSanityChecks(job);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
								System.out.println("[Error] JobSetupClass Class Not Found. Try Again");
								continue;
							} catch (InstantiationException e) {
								e.printStackTrace();
								System.out.println("[Error] JobSetupClass Class Not Instantiated. Try Again");
								continue;
							} catch (IllegalAccessException e) {
								e.printStackTrace();
								System.out.println("[Error] JobSetupClass Class Not Found. Try Again");
								continue;
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
								System.out.println("[Error] JobSetupClass has an Incorrect Argument. Try Again");
								continue;
							}
							HadoopMaster.jobMap.put(HadoopMaster.counter, job);
							new Thread(new JobThread(inputFile,job)).start();
						}
					}
				}
			}
		}
	}
}
