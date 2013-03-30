package nodework;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import communication.ChunkObject;
import communication.MessageClass;

import lib.Constants;

public class WorkerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// TODO Auto-generated method stub
		ServerSocket server = null;
		int workernum = Integer.parseInt(args[0]);
		System.out.println("Worker number " + args[0]);
		try {
			server = new ServerSocket(Constants.WORKER_PORTS[workernum]);
			while (true) {
				Socket workerSocket = server.accept();
				OutputStream output = workerSocket.getOutputStream();
				InputStream input = workerSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				out.flush();
				ObjectInputStream in = new ObjectInputStream(input);
				ChunkObject readMessage = (ChunkObject) in.readObject();
				RecordReader.readChunk(readMessage);
				out.writeObject(new Integer(readMessage.getChunkNumber()));
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