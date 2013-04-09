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
				// If key\tvalue string is shorter than mapper output record size, pad it with tab characters
				String mapped = kv.getKey() + "\t" + kv.getValue();
				if (mapped.length() < task.cp.getMapperOutputSize()) {
					long charsToPad = task.cp.getMapperOutputSize() - mapped.length(); 
					for (int i = 0; i < charsToPad; i++) {
						mapped = mapped + "\t";
					}
				} else if (mapped.length() > task.cp.getMapperOutputSize()) {
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
			out.writeObject(new Message(MessageType.DONE_REDUCE));
		}
		
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
		Writable<?> keyInstance = (Writable<?>) task.reducerInputKeyClass.newInstance();
		Writable<?> valueInstance = (Writable<?>) task.reducerInputValueClass.newInstance();
		Iterator<Writable<?>> valueItr = null;
		ArrayList<Writable<?>> l = new ArrayList<Writable<?>>();
		
		FileInputStream fis = new FileInputStream(inputFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line;
		Writable<?> prevKey;
		
		while ((line = br.readLine()) != null) {
			String[] lineContents = line.split("\\t");
			String key = lineContents[0];
			String value = lineContents[1];
			
			if (prevKey != null) {
				keyInstance = keyInstance.parseFromString(key);
				
				// If current key is same as before, accumulate value and update previous key
				if (prevKey.compareTo(keyInstance) == 0) {
					valueInstance = valueInstance.parseFromString(value);
					l.add(valueInstance);
					prevKey = keyInstance;
				} 
				// Came across a different key. Reduce previous key
				else {
					valueItr = l.iterator();
					reducer.reduce(prevKey, valueItr, cx);
					
					// Write results of reduce to file
					ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
					for (KeyValue<Writable<?>, Writable<?>> kv : toWrite) {
						outWriter.println(kv.getKey() + "\t" + kv.getValue());
					}
					
					// Clear iterators for previous key and start fresh for new key
					cx.clear();
					l.clear();
					prevKey = keyInstance;
					valueInstance = valueInstance.parseFromString(value);
					l.add(valueInstance);
				}
			} else {
				prevKey = keyInstance.parseFromString(key);
				valueInstance = valueInstance.parseFromString(value);
				l.add(valueInstance);
			}
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
