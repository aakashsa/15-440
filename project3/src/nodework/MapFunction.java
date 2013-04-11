package nodework;

import fileio.MapRecordReader;
import fileio.MapRecordWriter;
import interfaces.InputFormat;
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
	 */
	@SuppressWarnings("unchecked")
	public static void doMap(MapTask task, ObjectOutputStream out) {
		System.out.println("[INFO] Received map task on " + task.wi.getWorkerNum());
		
		// Use file input format to read records from file
		MapRecordReader recordReader = new MapRecordReader(task.fileInputFormat);
		Iterator<InputFormat<Writable<?>, Writable<?>>> itr = null;
		// Initialize Mapper instance
		Mapper<Writable<?>, Writable<?>, Writable<?>, Writable<?>> mapper = null;
		try {
			itr = recordReader.readChunk(task.chunk);
			mapper = (Mapper<Writable<?>, Writable<?>, Writable<?>, Writable<?>>) task.mapperClass.newInstance();
		} catch (InstantiationException e2) {
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		Context<Writable<?>, Writable<?>> cx = new Context<Writable<?>, Writable<?>>();
		
		// Initialize record writer
		MapRecordWriter recordWriter = null;
		try {
			recordWriter = new MapRecordWriter(task);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// Call the mapper init function
		mapper.init();
		
		// Map each line; write it to worker output file
		while (itr.hasNext()) {
			InputFormat<?, ?> iformat = itr.next();
			mapper.map(iformat.getKey(), iformat.getValue(), cx);
			ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
			
			for (KeyValue<Writable<?>, Writable<?>> kv : toWrite) {
				try {
					recordWriter.writeRecord(kv, "\t", "~");
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
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
}
