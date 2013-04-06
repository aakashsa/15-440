package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import master.HadoopMaster;

public class ServiceMapThread implements Runnable {

	private String host;
	private int port;
	private int workerNumber;
	private MapTask task;

	public ServiceMapThread(MapTask task, int workerNumber, WorkerInfo wi) {
		this.host = wi.getHost();
		this.port = wi.getPort();
		this.workerNumber = workerNumber;
		this.task = task;
	}

	@Override
	public void run() {
		// Opening a Socket and sending a request to map a chunk
		try {
			System.out.println("Port number = " + port);
			HadoopMaster.workerSockets[workerNumber] = new Socket(host, port);
			OutputStream output = HadoopMaster.workerSockets[workerNumber]
					.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(output);
			out.flush();
			out.writeObject(task);
			InputStream input = HadoopMaster.workerSockets[workerNumber]
					.getInputStream();
			ObjectInputStream in = new ObjectInputStream(input);
			int read = (Integer) in.readObject();
			synchronized (HadoopMaster.OBJ_LOCK) {
				HadoopMaster.fileSizeRead += read;
				System.out.println("new File Size  = " + HadoopMaster.fileSizeRead);
				HadoopMaster.freeWorkers.add(workerNumber);
				HadoopMaster.busyWorkerMap.remove(workerNumber);
				HadoopMaster.chunkWorkerMap.remove(task.chunk);
				HadoopMaster.mapsDone++;
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