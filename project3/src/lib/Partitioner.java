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

/**
 * Class to do the partitioning of mapper output
 */
public class Partitioner {

	/**
	 * A function that takes output from each map worker and
	 * hashes each key in those worker files to a particular reducer file
	 * @param numReducers Number of reducers requested by job
	 * @param jobName Name of job running partitioning
	 */
	public static void partitionMapOutputData(int numReducers, String jobName) {
		
		// Initialize reducer input file writers
		PrintWriter[] reducerWriters = new PrintWriter[numReducers];
		
		for (int i = 0; i < numReducers; i++) {
			OutputStream file;
			try {
				file = new FileOutputStream(Utils.getReduceInputFileName(i, jobName), true);
				reducerWriters[i] = new PrintWriter(file, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		// Write to reducer input files
		File workerFilesDir = new File(Utils.getWorkerOutputFilesDirName(jobName));
		File[] workerFiles = workerFilesDir.listFiles();
		
		for (File f : workerFiles) {
			BufferedReader br;
			try {
				FileInputStream fis = new FileInputStream(f);
				br = new BufferedReader(new InputStreamReader(fis));
				
				String line;
				while ((line = br.readLine()) != null) {
					String[] lineContents = line.split("\\t");
					
					// Hash key to find out which reducer this key should be sent to
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
