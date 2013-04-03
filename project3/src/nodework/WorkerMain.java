package nodework;

import interfaces.Mapper;

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
import mapper.NaiveMapperIntString;
import mapper.NaiveMapperStringString;

public class WorkerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ServerSocket server = null;
		int workerNum = Integer.parseInt(args[0]);
		System.out.println("Worker number " + args[0]);
		ConstantsParser cp = new ConstantsParser();
		try {
			server = new ServerSocket(cp.getAllWorkers().get(workerNum)
					.getPort());
			while (true) {
				Socket workerSocket = server.accept();
				OutputStream output = workerSocket.getOutputStream();
				InputStream input = workerSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				out.flush();
				ObjectInputStream in = new ObjectInputStream(input);
				ChunkObject readMessage = (ChunkObject) in.readObject();
				Iterator<String> itr = RecordReader.readChunk(readMessage);
				Mapper mapper = null;
				if (cp.getInputFormat().equals("TEXTINPUTFORMAT")) {
					mapper = new NaiveMapperIntString();
					int i = 0;
					while (itr.hasNext()) {
						String element = itr.next();
						// mapper.map(i, element);
						i++;
					}
				} else { // input format is KEYVALUEINPUTFORMAT
					mapper = new NaiveMapperStringString();
					while (itr.hasNext()) {
						String element = itr.next();
						String[] keyValue = element.split("\t");
						Class[] methodParameters = new Class[] { String.class,
								String.class };
						try {
							mapper.getClass().getDeclaredMethod("map",
									methodParameters);
							// mapper.map(keyValue[0], keyValue[1]);
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				out.writeObject(new Integer(RecordReader.read));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}