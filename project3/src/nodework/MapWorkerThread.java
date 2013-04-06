package nodework;

import interfaces.InputFormat;
import interfaces.Mapper;
import interfaces.Writable;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import communication.MapTask;

public class MapWorkerThread implements Runnable{
	private MapTask task = null;
	private ObjectOutputStream out;
	
	public MapWorkerThread(Object obj, ObjectOutputStream out){
		task = (MapTask) obj;
		this.out = out;
	}
	@Override
	public void run() {

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
				Partitioner.partitiondata(kv.getKey(), kv.getValue(), task.cp);
			}
			cx.clear();
		}
		System.out.println("[INFO] Finished map task.");
		try {
			out.writeObject(new Integer(RecordReader.read));
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
