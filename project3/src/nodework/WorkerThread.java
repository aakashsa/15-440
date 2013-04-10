package nodework;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import communication.MapTask;
import communication.Message;
import communication.MessageType;
import communication.ReduceTask;

public class WorkerThread implements Runnable {

	private int port;
	
	public WorkerThread(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		// Setup server socket
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("[INFO] Worker thread started. Listening for task requests");
			// Start listening for requests on the current port
			while (true) {
				Socket workerSocket = server.accept();
				OutputStream output = workerSocket.getOutputStream();
				InputStream input = workerSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				out.flush();
				ObjectInputStream in = new ObjectInputStream(input);

				Message msg = (Message) in.readObject();

				// Map request
				if (msg.type == MessageType.START_MAP) {
					MapTask task = (MapTask) msg.task;
					new Thread(new WorkerFunctions(msg.type, task, out)).start();
				}
				// Reduce request
				else if (msg.type == MessageType.START_REDUCE) {
					ReduceTask task = (ReduceTask) msg.task;
					new Thread(new WorkerFunctions(msg.type, task, out)).start();
				}
				// Ping request
				else if (msg.type == MessageType.PING_REQUEST) {
					out.writeObject(new Message(MessageType.PING_REPLY));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
