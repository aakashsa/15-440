package nodework;

import fileio.MapRecordReader;
import fileio.MapRecordWriter;
import fileio.ReduceRecordReader;
import fileio.ReduceRecordWriter;
import interfaces.InputFormat;
import interfaces.Mapper;
import interfaces.Reducer;
import interfaces.Task;
import interfaces.Writable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import lib.Context;
import lib.KeyValue;
import lib.Utils;

import communication.MapTask;
import communication.Message;
import communication.MessageType;
import communication.ReduceTask;

public class WorkerFunctions implements Runnable {

	private MessageType type;
	private Task task;
	private ObjectOutputStream out;
	
	public WorkerFunctions(MessageType type, Task task, ObjectOutputStream out) {
		this.type = type;
		this.task = task;
		this.out = out;
	}
	
	@Override
	public void run() {
		if (this.type == MessageType.START_MAP) {
			doMap((MapTask) this.task, this.out);
		} else if (this.type == MessageType.START_REDUCE) {
			doReduce((ReduceTask) this.task, this.out);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void doMap(MapTask task, ObjectOutputStream out) {
		System.out.println("[INFO] Received map task on " + task.wi.getWorkerNum());
		
		// Use file input format to read records from file
		MapRecordReader recordReader = new MapRecordReader(task.fileInputFormat);
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
		
		// Initialize record writer
		MapRecordWriter recordWriter = null;
		try {
			recordWriter = new MapRecordWriter(task);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// Map each line; write it to worker output file
		while (itr.hasNext()) {
			InputFormat<?, ?> iformat = itr.next();
			mapper.map(iformat.getKey(), iformat.getValue(), cx);
			ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
			
			for (KeyValue<Writable<?>, Writable<?>> kv : toWrite) {
				recordWriter.writeRecord(kv, "\t", "~");
			}
			cx.clear();
		}
		System.out.println("[INFO] Finished map task on mapper " + task.wi.getWorkerNum());
		try {
			out.writeObject(new Message(MessageType.DONE_MAP));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void doReduce(ReduceTask task, ObjectOutputStream out) {
		System.out.println("[INFO] Received reduce task on reducer " + task.reducerNumber);
				
		System.out.println("[INFO] Starting sort on reducer " + task.reducerNumber);
		// Sort the reducer input file
		InsertionSortRecords sorter = new InsertionSortRecords(task.reducerInputKeyClass, (int) task.mapperOutputSize, "\t", Utils.getReduceInputFileName(task.reducerNumber, task.jobName));
		sorter.sort();
		System.out.println("[INFO] Done sorting on reducer " + task.reducerNumber);
		
		// Initialize record reader and writer
		ReduceRecordReader recordReader = null;
		ReduceRecordWriter recordWriter = null;
		// Initialize Reducer instance
		Reducer<Writable<?>, Writable<?>, Writable<?>, Writable<?>> reducer = null;
		try {
			recordReader = new ReduceRecordReader(task);
			recordWriter = new ReduceRecordWriter(task);
			reducer = (Reducer<Writable<?>, Writable<?>, Writable<?>, Writable<?>>) task.reducerClass.newInstance();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			e2.printStackTrace();
		}
		
		// Create new context
		Context<Writable<?>, Writable<?>> cx = new Context<Writable<?>, Writable<?>>();
		
		// Do reduce on input file
		Writable<?> prevKey = null;
		try {
			prevKey = (Writable<?>) task.reducerInputKeyClass.newInstance();;
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		Iterator<Writable<?>> valueItr = null;
		ArrayList<Writable<?>> l = new ArrayList<Writable<?>>();
		
		KeyValue<Writable<?>, Writable<?>> kv = null;
		
		int i = 0;
		try {
			while ((kv = recordReader.readRecord("\t", "~")) != null) {
				if (i != 0) {
					// If current key is same as prevKey; if yes accumulate value
					if (prevKey.compareTo(kv.getKey().getValue()) == 0) {
						l.add(kv.getValue());
					} 
					// Came across a different key. Reduce previous key
					else {
						// reduce prevKey
						valueItr = l.iterator();
						reducer.reduce(prevKey, valueItr, cx);
							
						// Write results of reduce to file
						ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
						for (KeyValue<Writable<?>, Writable<?>> keyvalue : toWrite) {
							recordWriter.writeRecord(keyvalue, "\t");
						}
						
						// Clear iterators for previous key and start fresh for new key
						cx.clear();
						l.clear();
						prevKey = prevKey.parseFromString((String) kv.getKey().getValue());
						l.add(kv.getValue());	
					}
				} else {
					i = 1;
					prevKey = prevKey.parseFromString((String) kv.getKey().getValue());
					l.add(kv.getValue());
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Do the last edge case reduce and write to file
		valueItr = l.iterator();
		reducer.reduce(prevKey, valueItr, cx);
		
		ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
		for (KeyValue<Writable<?>, Writable<?>> keyvalue : toWrite) {
			recordWriter.writeRecord(keyvalue, "\t");
		}
		cx.clear();
		l.clear();
		
		// Done reducing all keys. Close writers
		try {
			System.out.println("[INFO] Finished reduce task on reducer " + task.reducerNumber);
			out.writeObject(new Message(MessageType.DONE_REDUCE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
