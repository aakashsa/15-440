package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import nodework.JobThreadSharedFields;

import master.HadoopMaster;

/**
 * ServiceReduceThread is the thread spawned by the JobThread for servicing a particular Reduce task.
 * Sends a reduce command to a worker
 * Upon getting the Ack it updates the reduceDone Map
 */
public class ServiceReduceThread implements Runnable {

	private Message msg;
	private WorkerInfo wi;
	private JobThreadSharedFields sharedData;

	public ServiceReduceThread(WorkerInfo wi, Message msg,JobThreadSharedFields sharedData) {
		this.msg = msg;
		this.wi = wi;
		this.sharedData = sharedData;
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
			Message msg1 = (Message) in.readObject();
			if (msg1.type == MessageType.DONE_REDUCE) {
				System.out.println("[INFO] Done Reduce by worker " + wi.getWorkerNum());
				// Reduce Done Add the Ack for it 
				sharedData.getReduceDoneMap().add(msg1.type);
				synchronized (HadoopMaster.QUEUE_LOCK) {
					// Free the Worker
					HadoopMaster.freeWorkers.add(wi.getWorkerNum());
				}
				// Remove the worker from Reduce Map As its free now
				sharedData.getReduceWorkerMap().remove(msg.task);
			} else if (msg1.type == MessageType.EXCEPTION) {
				msg1.e.printStackTrace();
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
