package nodework;

import fileio.ReduceRecordReader;
import fileio.ReduceRecordWriter;
import interfaces.Reducer;
import interfaces.Writable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import lib.Context;
import lib.KeyValue;
import lib.Utils;

import communication.Message;
import communication.MessageType;
import communication.ReduceTask;

/**
 * Class that contains doMap Function that carries out the Map Operation
 */
public class ReduceFunction {

	/**
	 * The function that carries out reduce operation
	 * 
	 * @param task
	 *            The reduce task
	 * @param out
	 *            Output stream to write acknowledgement to
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	public static void doReduce(ReduceTask task, ObjectOutputStream out) throws IOException {
		System.out.println("[INFO] Received reduce task on reducer "+ task.reducerNumber);
		System.out.println("[INFO] Starting sort on reducer "+ task.reducerNumber);
		// Sort the reducer input file
		InsertionSortRecords sorter = new InsertionSortRecords(task.reducerInputKeyClass, (int) task.mapperOutputSize, "\t",Utils.getReduceInputFileName(task.reducerInputFileNumber,task.jobName));
		sorter.sort();
		System.out.println("[INFO] Done sorting on reducer "+ task.reducerNumber);

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
			out.writeObject(new Message(MessageType.EXCEPTION, e2));
			return;
		} catch (InstantiationException e2) {
			out.writeObject(new Message(MessageType.EXCEPTION, e2));
			return;
		} catch (IllegalAccessException e2) {
			out.writeObject(new Message(MessageType.EXCEPTION, e2));
			return;
		}

		// Create new context
		Context<Writable<?>, Writable<?>> cx = new Context<Writable<?>, Writable<?>>();

		// Do reduce on input file
		Writable<?> prevKey = null;
		try {
			prevKey = (Writable<?>) task.reducerInputKeyClass.newInstance();
		} catch (InstantiationException e1) {
			out.writeObject(new Message(MessageType.EXCEPTION, e1));
			return;
		} catch (IllegalAccessException e1) {
			out.writeObject(new Message(MessageType.EXCEPTION, e1));
			return;
		}

		Iterator<Writable<?>> valueItr = null;
		ArrayList<Writable<?>> l = new ArrayList<Writable<?>>();
		KeyValue<Writable<?>, Writable<?>> kv = null;
		int i = 0;

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
					ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx
							.getAll();
					for (KeyValue<Writable<?>, Writable<?>> keyvalue : toWrite) {
						recordWriter.writeRecord(keyvalue, "\t");
					}

					// Clear iterators for previous key and start fresh for new
					// key
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
		System.out.println("[INFO] Finished reduce task on reducer "+ task.reducerNumber);
		out.writeObject(new Message(MessageType.DONE_REDUCE));

	}
}
