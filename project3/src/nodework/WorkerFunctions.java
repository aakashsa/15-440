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

import lib.InsertionSortRecords;
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
		
		// Open output file
		OutputStream outputFile = null;
		PrintWriter outWriter = null;
		try {
			outputFile = new FileOutputStream(Utils.getWorkerOutputFileName(task.wi.getWorkerNum(), task.jobName), true);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		outWriter = new PrintWriter(outputFile, true);
		
		// Map each line; write it to worker output file
		while (itr.hasNext()) {
			InputFormat<?, ?> iformat = itr.next();
			cx = mapper.map(iformat.getKey(), iformat.getValue(), cx);
			if (cx == null) {
				try {
					out.writeObject(new Message(MessageType.EXCEPTION, new RuntimeException("Mapper returned Null Context")));
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
			
			for (KeyValue<Writable<?>, Writable<?>> kv : toWrite) {
				// If key\tvalue string is shorter than mapper output record size - 1, pad it with tab characters
				String mapped = kv.getKey() + "\t" + kv.getValue();
				if (mapped.length() < (task.cp.getMapperOutputSize() - 1)) {
					long charsToPad = task.cp.getMapperOutputSize() - 1 - mapped.length(); 
					for (int i = 0; i < charsToPad; i++) {
						mapped = mapped + "~";
					}
				} else if (mapped.length() > (task.cp.getMapperOutputSize() - 1)) {
					try {
						out.writeObject(new Message(MessageType.EXCEPTION, new IllegalArgumentException("Mapper output concatenation of key and value is bigger than mapper output record size")));
						return;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				outWriter.println(mapped);
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
	
	@SuppressWarnings({ "unchecked" })
	public static void doReduce(ReduceTask task, ObjectOutputStream out) {
		System.out.println("[INFO] Received reduce task on reducer " + task.reducerNumber + ". Performing reduce on reducer...");
		
		// Get the reducer input file
		File inputFile = new File(Utils.getReduceInputFileName(task.reducerNumber, task.jobName));
		
		if (!inputFile.exists()) {
			System.out.println("[INFO] No Reducer input file found (" + inputFile.getName() + ")");
			try {
				out.writeObject(new Message(MessageType.DONE_REDUCE));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("[INFO] Starting sort on reducer " + task.reducerNumber + "...");
		// Sort the reducer input file
		InsertionSortRecords sorter = new InsertionSortRecords(task.reducerInputKeyClass, (int) task.mapperOutputSize, inputFile.getPath());
		sorter.sort();
		System.out.println("[INFO] Done sorting.");
		
		// Renew the pointer to the input file, just in case
		inputFile = new File(Utils.getReduceInputFileName(task.reducerNumber, task.jobName));
		
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
		OutputStream outputFile = null;
		PrintWriter outWriter = null;
		try {
			outputFile = new FileOutputStream(Utils.getReduceOutputFileName(task.reducerNumber, task.outputDir), true);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		outWriter = new PrintWriter(outputFile, true);
		
		// Do reduce on input file
		Writable<?> keyInstance = null;
		Writable<?> valueInstance = null;
		Writable<?> prevKey = null;
		try {
			keyInstance = (Writable<?>) task.reducerInputKeyClass.newInstance();
			valueInstance = (Writable<?>) task.reducerInputValueClass.newInstance();
			prevKey = (Writable<?>) task.reducerInputKeyClass.newInstance();;
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		Iterator<Writable<?>> valueItr = null;
		ArrayList<Writable<?>> l = new ArrayList<Writable<?>>();
		
		FileInputStream fis;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(inputFile);
			br = new BufferedReader(new InputStreamReader(fis));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		String line;
		int i = 0;
		try {
			while ((line = br.readLine()) != null) {
				String[] lineContents = line.split("\\t");
				String key = lineContents[0];
				String value = null;
				if (line.endsWith("~")){
					value = (lineContents[1].split("~"))[0];
				}
				else
					value = lineContents[1];
								
				if (i != 0) {
					keyInstance = keyInstance.parseFromString(key);			
					// If current key is same as before, accumulate value and update previous key
					if (prevKey.compareTo(keyInstance.getValue()) == 0) {
						valueInstance = valueInstance.parseFromString(value);
						l.add(valueInstance);
					} 
					// Came across a different key. Reduce previous key
					else {
						// Skype the First Prev = NULL key
							valueItr = l.iterator();
							reducer.reduce(prevKey, valueItr, cx);
							// Write results of reduce to file
							ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
							for (KeyValue<Writable<?>, Writable<?>> kv : toWrite) {
								outWriter.println(kv.getKey() + "\t" + kv.getValue());
								outWriter.flush();
							}
							
							// Clear iterators for previous key and start fresh for new key
							cx.clear();
							l.clear();
							prevKey = prevKey.parseFromString(key);
							valueInstance = valueInstance.parseFromString(value);
							l.add(valueInstance);	
					}
				} else {
					i=1;
					keyInstance = keyInstance.parseFromString(key);
					prevKey = prevKey.parseFromString(key);
					valueInstance = valueInstance.parseFromString(value);
					l.add(valueInstance);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Do the last edge case reduce and write to file
		valueItr = l.iterator();
		reducer.reduce(prevKey, valueItr, cx);
		
		ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
		for (KeyValue<Writable<?>, Writable<?>> kv : toWrite) {
			outWriter.println(kv.getKey() + "\t" + kv.getValue());
		}
		cx.clear();
		l.clear();
		
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
