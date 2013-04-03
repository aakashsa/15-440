package nodework;

import interfaces.InputFormat;
import interfaces.Mapper;
import interfaces.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import communication.ChunkObject;

import lib.ConstantsParser;

public class WorkerMain {

	public static void main(String[] args) {

		ServerSocket server = null;
		int workerNum = Integer.parseInt(args[0]);
		System.out.println("Worker number " + args[0]);
		try {
			ConstantsParser cp = new ConstantsParser();

			server = new ServerSocket(cp.getAllWorkers().get(workerNum).getPort());
			Class<?> fileInputFormatClass = cp.getInputFormat();
			
			while (true) {
				// Wait to get new instruction from master; initialize streams
				Socket workerSocket = server.accept();
				OutputStream output = workerSocket.getOutputStream();
				InputStream input = workerSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				out.flush();
				ObjectInputStream in = new ObjectInputStream(input);
				
				// Read in chunk message from master
				ChunkObject readMessage = (ChunkObject) in.readObject();
				
				// Use file input format to read records from file
				RecordReader recordReader = new RecordReader(fileInputFormatClass);
				Iterator<InputFormat<Writable<?>, Writable<?>>> itr = recordReader.readChunk(readMessage);
				
				// Initialize Mapper instance
				Class<?> mapperClass = cp.getMapperClass();
				@SuppressWarnings("unchecked")
				Mapper<Writable<?>, Writable<?>, Writable<?>, Writable<?>> mapper = 
						(Mapper<Writable<?>, Writable<?>, Writable<?>, Writable<?>>) mapperClass.newInstance();
				Context<Writable<?>, Writable<?>> cx = new Context<Writable<?>, Writable<?>>();
				if (cx==null)
					System.out.println("Problem EARLY !!!");
				
				while (itr.hasNext()) {
					InputFormat<Writable<?>, Writable<?>> iformat = itr.next();
					if (cx==null)
						System.out.println("Problem BEFORE !!!");
					if (iformat==null)
						System.out.println("Problem 2 !!!");
					if (mapper==null)
						System.out.println("Problem 3 !!!");
					cx = mapper.map(iformat.getKey(), iformat.getValue(), cx);
					if (cx==null)
						System.out.println("Problem !!!");
					ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
					// WRITE THIS SHIT TO FILE! for now, printing it out
					for (KeyValue<Writable<?>, Writable<?>> kv : toWrite) {
						System.out.println(kv.getKey().toString() + "\t" + kv.getValue().toString());
					}
					cx.clear();
				}
				System.out.println("At the End ");
				out.writeObject(new Integer(RecordReader.read));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}