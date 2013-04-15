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
/**
 * Main Clas begins the Execution of the Worker Class
 */
public class WorkerThread implements Runnable {

	private int port;

	/**
	 * Get the port to start on, and save it
	 * @param port Port to listen on
	 */
	public WorkerThread(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		// Setup server socket
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("[INFO] Worker thread started. Listening for task requests on " + port);
			// Start listening for requests on the current port
			while (true) {
				Socket workerSocket = server.accept();
				OutputStream output = workerSocket.getOutputStream();
				InputStream input = workerSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				out.flush();
				ObjectInputStream in = new ObjectInputStream(input);

				Message requestMsg = (Message) in.readObject();

				// Map request
				if (requestMsg.type == MessageType.START_MAP) {
					MapTask task = (MapTask) requestMsg.task;
					new Thread(new WorkerFunctions(requestMsg.type, task, out)).start();
				}
				// Reduce request
				else if (requestMsg.type == MessageType.START_REDUCE) {
					ReduceTask task = (ReduceTask) requestMsg.task;
					new Thread(new WorkerFunctions(requestMsg.type, task, out)).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
