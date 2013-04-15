package nodework;

import fileio.MapRecordReader;
import fileio.MapRecordWriter;
import interfaces.Mapper;
import interfaces.Writable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import lib.Context;
import lib.KeyValue;
import lib.Utils;

import communication.MapTask;
import communication.Message;
import communication.MessageType;

/**
 * Class that contains doMap Function that carries out the Map Operation
 */
public class MapFunction {

	/**
	 * The function that carries out map operation
	 * @param task The map task
	 * @param out Output stream to write acknowledgement to
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public static void doMap(MapTask task, ObjectOutputStream out) throws IOException {
		System.out.println(Utils.logInfo(task.jobName, "Received map task on " + task.wi.getWorkerNum()));
		
		// Use file input format to read records from file
		MapRecordReader recordReader = new MapRecordReader(task.fileInputFormat, task.jobName);
		Iterator<KeyValue<Writable<?>, Writable<?>>> itr = null;
		// Initialize Mapper instance
		Mapper<Writable<?>, Writable<?>, Writable<?>, Writable<?>> mapper = null;
		try {
			itr = recordReader.readChunk(task.chunk);
			mapper = (Mapper<Writable<?>, Writable<?>, Writable<?>, Writable<?>>) task.mapperClass.newInstance();
		} catch (InstantiationException e2) {
			out.writeObject(new Message(MessageType.EXCEPTION,e2));
			out.flush();
			return;
		} catch (IllegalAccessException e2) {
			out.writeObject(new Message(MessageType.EXCEPTION,e2));
			out.flush();
			return;
		}
		
		Context<Writable<?>, Writable<?>> cx = new Context<Writable<?>, Writable<?>>();
		
		// Initialize record writer
		MapRecordWriter recordWriter = null;
		try {
			recordWriter = new MapRecordWriter(task);
		} catch (FileNotFoundException e1) {
			out.writeObject(new Message(MessageType.EXCEPTION,e1));
			out.flush();
			return;
		}
		
		// Call the mapper init function
		mapper.init();
		
		// Map each line; write it to worker output file
		while (itr.hasNext()) {
			KeyValue<Writable<?>, Writable<?>> kv = itr.next();
			try {
				mapper.map(kv.getKey(), kv.getValue(), cx);
			} catch (Exception e) {
				out.writeObject(new Message(MessageType.EXCEPTION, e));
				out.flush();
				return;
			}
			ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
			
			for (KeyValue<Writable<?>, Writable<?>> kv1 : toWrite) {
				try {
					recordWriter.writeRecord(kv1, "\t", "~");
				} catch (IllegalArgumentException e) {
					out.writeObject(new Message(MessageType.EXCEPTION,e));
					out.flush();
					return;
				} catch (UnsupportedEncodingException e) {
					out.writeObject(new Message(MessageType.EXCEPTION,e));
					out.flush();
					return;
				}
			}
			cx.clear();
		}
		System.out.println(Utils.logInfo(task.jobName, "Finished map task on mapper " + task.wi.getWorkerNum()));
		out.writeObject(new Message(MessageType.DONE_MAP));
		out.flush();
	}
}
