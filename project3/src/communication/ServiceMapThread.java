package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.sun.codemodel.internal.fmt.JBinaryFile;

import master.HadoopMaster;
import master.JobThread;

public class ServiceMapThread implements Runnable {

	private String host;
	private int port;
	private int workerNumber;
	private Message msg;
	private JobThread jb;

	public ServiceMapThread(Message msg, int workerNumber, WorkerInfo wi,JobThread jb) {
		this.host = wi.getHost();
		this.port = wi.getPort();
		this.workerNumber = workerNumber;
		this.msg = msg;
		this.jb = jb;
	}

	@Override
	public void run() {
		Socket mapSocket;
		// Opening a Socket and sending a request to map a chunk
		try {
			mapSocket = new Socket(host, port);
			OutputStream output = mapSocket.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(output);
			out.flush();
			MapTask task = (MapTask) msg.task;
			out.writeObject(msg);
			InputStream input = mapSocket.getInputStream();
			ObjectInputStream in = new ObjectInputStream(input);
			Message msg = (Message) in.readObject();

			if (msg.type == MessageType.DONE_MAP) {
				synchronized (HadoopMaster.QUEUE_LOCK) {
					HadoopMaster.freeWorkers.add(workerNumber);
					HadoopMaster.busyWorkerMap.remove(workerNumber);
					JobThread.chunkWorkerMap.remove(task.chunk);
					synchronized (jb.getMapCounterLock()) {
						jb.incrementMapCounter();
					}
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
