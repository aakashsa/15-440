package nodework;

import interfaces.InputFormat;
import interfaces.Mapper;
import interfaces.Reducer;
import interfaces.Task;
import interfaces.Writable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import communication.MapTask;
import communication.ReduceTask;
import communication.TaskType;

/**
 * Gets in the port it should listen on from args, and starts listening for task
 * requests on that port. When a request comes in, it checks if it's a map
 * request or a reduce request, and carries it out accordingly.
 */
public class Worker {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// Get port to listen on from command line
		if (args.length != 1) {
			System.out.println("Usage: Worker <port>");
			System.exit(-1);
		}
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("Port must be an integer");
			System.exit(-1);
		}
		if (port < 1024 || port > 49151) {
			System.out.println("Port number must be >= 1024 and <= 49151 (Registered port numbers range)");
			System.exit(-1);
		}

		// Setup server socket
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("[INFO] Worker started. Listening for task requests");
			// Start listening for requests on the current port
			while (true) {
				Socket workerSocket = server.accept();
				OutputStream output = workerSocket.getOutputStream();
				InputStream input = workerSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				out.flush();
				ObjectInputStream in = new ObjectInputStream(input);

				Task obj = (Task) in.readObject();

				// Check if task is Map
				if (obj.getTaskType() == TaskType.MAP) {
					// Spawning MapWorkerThread
					new Thread(new MapWorkerThread(obj, out)).start();
				}

				// Check if task is Reduce
				else if (obj.getTaskType() == TaskType.REDUCE) {
					// Spawning ReduceWorkerThread
					new Thread(new ReduceWorkerThread(obj, out)).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
