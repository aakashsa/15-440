package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import lib.Constants;
import master.HadoopMaster;

public class ServiceThread implements Runnable {

	private String host;
	private int port;
	private int chunkNumber;
	private int workerNumber;
	private String fileName;

	public ServiceThread(int chunkNumber, int workerNumber, String fileName) {
		this.host = Constants.WORKER_HOSTS[workerNumber];
		this.port = Constants.WORKER_PORTS[workerNumber];
		this.chunkNumber = chunkNumber;
		this.workerNumber = workerNumber;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		// Opening a Socket and sending a request to map a chunk
		try {
			HadoopMaster.workerSocket[workerNumber] = new Socket(host, port);
			OutputStream output = HadoopMaster.workerSocket[workerNumber]
					.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(output);
			out.flush();
			out.writeObject(new MessageClass(chunkNumber, Constants.CHUNK_SIZE,
					Constants.RECORD_SIZE, fileName));
			InputStream input = HadoopMaster.workerSocket[workerNumber]
					.getInputStream();
			ObjectInputStream in = new ObjectInputStream(input);
			System.out.println("Got Back Ack = " + in.readObject());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
