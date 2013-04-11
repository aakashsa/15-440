package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import master.HadoopMaster;
import master.JobThread;
import nodework.*;

/**
 * ServiceMapThread is the thread spawned by the JobThread for servicing a particular map task.
 * Sends a Chunk to the worker and waits for an ack.
 * Upon getting the Ack it updates the mapsDone Counter for the Job Thread.
 *
 */

public class ServiceMapThread implements Runnable {

	private String host;
	private int port;
	private int workerNumber;
	private Message msg;
	private JobThread jb;
	private JobThreadSharedFields sharedData;

	public ServiceMapThread(Message msg, int workerNumber, WorkerInfo wi,JobThread jb,JobThreadSharedFields sharedData) {
		this.host = wi.getHost();
		this.port = wi.getPort();
		this.workerNumber = workerNumber;
		this.msg = msg;
		this.jb = jb;
		this.sharedData = sharedData;
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
					synchronized (sharedData.getMapCounterLock()) {
						sharedData.incrementMapCounter();
						System.out.println(" Counter in " +  jb.job.getJobName() + " = " + sharedData.getMapCounter());
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
