package lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class Partitioner {

	/**
	 * A function that takes output from each map worker and
	 * hashes each key in those worker files to a particular reducer file
	 * @param cp
	 * @param jobName
	 */
	public static void partitionMapOutputData(ConstantsParser cp, String jobName) {
		
		// Initialize reducer input file writers
		int numReducers = (int) cp.getNumReducers();
		PrintWriter[] reducerWriters = new PrintWriter[numReducers];
		
		for (int i = 0; i < numReducers; i++) {
			OutputStream file;
			try {
				file = new FileOutputStream(Utils.getReduceInputFileName(i, jobName), true);
				PrintWriter out = new PrintWriter(file, true);
				reducerWriters[i] = out;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		// Write to reducer input files
		File workerFilesDir = new File(Utils.getWorkerOutputFilesDirName(jobName));
		File[] workerFiles = workerFilesDir.listFiles();
		
		for (File f : workerFiles) {
			try {
				FileInputStream fis = new FileInputStream(f);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				
				String line;
				while ((line = br.readLine()) != null) {
					String[] lineContents = line.split("\\t");
					
					int reducerNumber = (lineContents[0].hashCode() % numReducers);
					if (reducerNumber < 0) {
						reducerNumber += numReducers;
					}
					
					PrintWriter out = reducerWriters[reducerNumber];
					out.println(line);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Close reducer input file writers
		for (int i = 0; i < numReducers; i++) {
			reducerWriters[i].close();
		}
	}
}
