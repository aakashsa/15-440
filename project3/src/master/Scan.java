package master;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Command line scanner for commands to process manager.
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
				
				// ps Command
				if (name.equals("listjobs")) {
					if (sc2.hasNext()) {
						System.out.println("Invalid command: " +
								"ps does not take any arguments!");
					}
					else {
				
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
					if (!name.equals("RUNJOB")){
						System.out.println("Invalid commant: quit does " +
								"not take any arguments!");
					}
					// Parsing Job  Command
					else {
						String jobConfClassName = sc2.next();;
						System.out.println(" Job Conf Class = " + jobConfClassName);
						String inputFile = sc2.next();
						System.out.println(" File Name = " + inputFile);						
						System.out.println(" Lets start the Job !");
						new Thread(new JobThread(jobConfClassName, inputFile)).start();
					}
				}
			}
		}
	}
}
