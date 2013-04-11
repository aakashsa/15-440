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



/**
 * ServiceReduceThread is the thread spawned by the JobThread for servicing a particular Reduce task.
 * Sends a reduce command to a worker
 * Upon getting the Ack it updates the reduceDone Map
 */
public class ServiceReduceThread implements Runnable {

	private Message msg;
	private WorkerInfo wi;
	
	public ServiceReduceThread(WorkerInfo wi, Message msg) {
		this.msg = msg;
		this.wi = wi;
	}
	
	@Override
	public void run() {
		Socket reduceSocket;
		try {
			reduceSocket = new Socket(wi.getHost(), wi.getPort());
			OutputStream output = reduceSocket.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(output);
			out.flush();
			out.writeObject(msg);
			InputStream input = reduceSocket.getInputStream();
			ObjectInputStream in = new ObjectInputStream(input);
			Message msg = (Message) in.readObject();
			if (msg.type == MessageType.DONE_REDUCE) {
				System.out.println("[INFO] Done Reduce by worker " + wi.getWorkerNum());
				JobThread.reduceDoneMessages.add(msg.type);
				synchronized (HadoopMaster.QUEUE_LOCK) {
					HadoopMaster.freeWorkers.add(wi.getWorkerNum());
					HadoopMaster.busyWorkerMap.remove(wi.getWorkerNum());
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
