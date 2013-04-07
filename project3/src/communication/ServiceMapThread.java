package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import master.JobThread;

public class ServiceMapThread implements Runnable {

	private String host;
	private int port;
	private int workerNumber;
	private Message msg;

	public ServiceMapThread(Message msg, int workerNumber, WorkerInfo wi) {
		this.host = wi.getHost();
		this.port = wi.getPort();
		this.workerNumber = workerNumber;
		this.msg = msg;
	}

	@Override
	public void run() {
		// Opening a Socket and sending a request to map a chunk
		try {
			JobThread.workerSockets[workerNumber] = new Socket(host, port);
			OutputStream output = JobThread.workerSockets[workerNumber].getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(output);
			out.flush();
			MapTask task = (MapTask) msg.task;
			out.writeObject(msg);
			InputStream input = JobThread.workerSockets[workerNumber].getInputStream();
			ObjectInputStream in = new ObjectInputStream(input);
			Message msg = (Message) in.readObject();
			
			if (msg.type == MessageType.DONE_MAP) {
				synchronized (JobThread.OBJ_LOCK) {
					JobThread.freeWorkers.add(workerNumber);
					JobThread.busyWorkerMap.remove(workerNumber);
					JobThread.chunkWorkerMap.remove(task.chunk);
					JobThread.mapsDone++;
				}
			} else if (msg.type == MessageType.EXCEPTION) {
				msg.e.printStackTrace();
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
