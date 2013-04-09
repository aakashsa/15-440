package nodework;

import interfaces.InputFormat;
import interfaces.Mapper;
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

import lib.Utils;

import communication.MapTask;
import communication.Message;
import communication.MessageType;
import communication.ReduceTask;

public class WorkerFunctions {

	@SuppressWarnings("unchecked")
	public static void doMap(MapTask task, ObjectOutputStream out) {
		System.out.println("[INFO] Received map task. Performing map...");
		
		// Use file input format to read records from file
		RecordReader recordReader = new RecordReader(task.fileInputFormat);
		Iterator<InputFormat<Writable<?>, Writable<?>>> itr = recordReader.readChunk(task.chunk);
		
		// Initialize Mapper instance
		Mapper<Writable<?>, Writable<?>, Writable<?>, Writable<?>> mapper = null;
		try {
			mapper = (Mapper<Writable<?>, Writable<?>, Writable<?>, Writable<?>>) task.mapperClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		Context<Writable<?>, Writable<?>> cx = new Context<Writable<?>, Writable<?>>();
		
		while (itr.hasNext()) {
			InputFormat<?, ?> iformat = itr.next();
			cx = mapper.map(iformat.getKey(), iformat.getValue(),
					cx);
			if (cx == null) {
				throw new RuntimeException("Mapper returned Null Context");
			}
			ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
			for (KeyValue<Writable<?>, Writable<?>> kv : toWrite) {
				Partitioner.partitionData(kv.getKey(), kv.getValue(), task.cp, task.jobName);
			}
			cx.clear();
		}
		System.out.println("[INFO] Finished map task.");
		try {
			out.writeObject(new Message(MessageType.DONE_MAP));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void doReduce(ReduceTask task, ObjectOutputStream out) {
		System.out.println("[INFO] Received reduce task on reducer " + task.reducerNumber + ". Performing reduce on reducer...");
		
		File reducerFolder = new File(Utils.getReducerFolderName(task.reducerNumber, task.jobName));
		File[] listOfFiles = reducerFolder.listFiles();
		
		if (listOfFiles == null) {
			System.out.println("[INFO] Reducer folder has no files");
			try {
				out.writeObject(new Message(MessageType.DONE_REDUCE));
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			
			OutputStream outputFile = null;
			PrintWriter outWriter = null;
			try {
				outputFile = new FileOutputStream(Utils.getReduceOutputFileName(task.reducerNumber, task.outputDir), true);
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			}
			outWriter = new PrintWriter(outputFile, true);
			
			// Call reduce on each key file
			for (File file : listOfFiles) {
				if (file.isFile()) {
					String fileName = file.getName();
					
					// Get key instance
					String keyName = Utils.getKeyNameFromFilename(fileName);
					
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
						FileInputStream fis = new FileInputStream(Utils.getKeyFileAbsoluteLocation(task.reducerNumber, task.jobName, fileName));
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
					// Write results of reduce to file
					ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
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
				System.out.println("[INFO] Finished reduce task.");
				out.writeObject(new Message(MessageType.DONE_REDUCE));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
