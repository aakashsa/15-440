package nodework;

import interfaces.Reducer;
import interfaces.Writable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import communication.ReduceTask;

public class ReduceWorkerThread implements Runnable {
	private ReduceTask task = null;
	private ObjectOutputStream out;

	public ReduceWorkerThread(Object obj, ObjectOutputStream out) {
		task = (ReduceTask) obj;
		this.out = out;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("[INFO] Received reduce task. Performing reduce...");
		
		File reducerFolder = new File("partition/reducer_" + task.reducerNumber);
		if (reducerFolder != null) {
			File[] listOfFiles = reducerFolder.listFiles();
			
			if (listOfFiles == null) {
				System.out.println("Reducer folder has no files");
			} else {
			
				// Initialize Reducer instance
				Reducer<Writable<?>, Writable<?>, Writable<?>, Writable<?>> reducer = null;
				try {
					reducer = (Reducer<Writable<?>, Writable<?>, Writable<?>, Writable<?>>) task.reducerClass.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				
				// Create new context
				Context<Writable<?>, Writable<?>> cx = new Context<Writable<?>, Writable<?>>();
				
				// Open output file for this reducer
				File outputDir = new File(task.outputDir);
				
				// If the directory does not exist, create it
				if (!outputDir.exists()) { 
					System.out.println("Creating directory: " + outputDir);
					outputDir.mkdir();
				}
				
				OutputStream outputFile = null;
				PrintWriter outWriter = null;
				try {
					outputFile = new FileOutputStream(task.outputDir + "part_" + task.reducerNumber + ".txt", true);
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				outWriter = new PrintWriter(outputFile, true);
				
				// Call reduce on each key file
				for (File file : listOfFiles) {
					if (file.isFile()) {
						String fileName = file.getName();
						
						// Get key instance
						String[] contents = fileName.split("_");
						String[] keyarray = contents[1].split("\\.");
						String keyName = keyarray[0];
						
						Writable<?> key = null;
						Writable<?> value = null;
						try {
							 key = (Writable<?>) task.reducerInputKeyClass.newInstance();
							 value = (Writable<?>) task.reducerInputValueClass.newInstance();
						} catch (InstantiationException e1) {
							e1.printStackTrace();
						} catch (IllegalAccessException e1) {
							e1.printStackTrace();
						}
						key = key.parseFromString(keyName);
						
						// Go through the key file and collect values in iterator
						Iterator<Writable<?>> valueItr = null;
						ArrayList<Writable<?>> l = new ArrayList<Writable<?>>();
						
						try {
							FileInputStream fis = new FileInputStream("partition/reducer_" + task.reducerNumber + "/" + fileName);
							BufferedReader inputS = new BufferedReader(new InputStreamReader(fis));
							
							String line;
							while ((line = inputS.readLine()) != null) {
								value = value.parseFromString(line);
								l.add(value);
							}
							
							// Call reduce with key and value iterator
							valueItr = l.iterator();
							reducer.reduce(key, valueItr, cx);
							
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println("[INFO] About to write to file...");
						// Write results of reduce to file
						ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
						System.out.println("[INFO] toWrite.length: " + toWrite.size());
						for (KeyValue<Writable<?>, Writable<?>> kv : toWrite) {
							outWriter.println(kv.getKey() + "\t" + kv.getValue());
						}
						cx.clear();
					}
				}
				
				// Done reducing all keys. Close writers
				try {
					outWriter.close();
					outputFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("[INFO] Finished reduce task.");
			}
		}
			
	}
}