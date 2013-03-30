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
	private int workerNumber;
	private ChunkObject chunk;

	public ServiceThread(ChunkObject chunk, int workerNumber) {
		this.host = Constants.WORKER_HOSTS[workerNumber];
		this.port = Constants.WORKER_PORTS[workerNumber];
		this.workerNumber = workerNumber;
		this.chunk = chunk;
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
			out.writeObject(chunk);
			InputStream input = HadoopMaster.workerSocket[workerNumber]
					.getInputStream();
			ObjectInputStream in = new ObjectInputStream(input);
			System.out.println("Got Back Ack for chunk number = "
					+ in.readObject());
			synchronized (HadoopMaster.OBJ_LOCK) {
				HadoopMaster.freeWorkers.add(workerNumber);
				HadoopMaster.busyWorkerMap.remove(workerNumber);
				HadoopMaster.chunkWorkerMap.remove(chunk);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
