package processmanagerstuff;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A runnable object that runs on the server for a particular
 * client. It is responsible for communicating information
 * with the client.
 * @author nikhiltibrewal
 *
 */
public class ClientThread implements Runnable {

	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int id = -1;

	public ClientThread(int id, ObjectOutputStream out, ObjectInputStream in) {
		this.id = id;
		
		try {
			System.out.println("Sending ID to slave... (Client thread)");
			this.out = out;
			out.flush();
			out.writeObject((Object) new Integer(id));
			out.flush();
			this.in = in;
			System.out.println("Client thread sent id....");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				// Block read for allFilePaths from slave
				String receivedString = (String) in.readObject();
				System.out.println(" Receieved Filepath string from Slave : "
						+ receivedString + " (client thread)");

				String[] filePaths = receivedString.split(",");
				for (String path : filePaths) {
					if (path.length() > 0) {
						String[] pathContents = path.split("\\t");
						int processId = Integer.parseInt(pathContents[1]);
						LoadBalancer.processFilePaths.put(processId, path);
					}
				}
				LoadBalancer.clientMessageStatus.put(this.id, "SENT");
			}
		} catch (IOException e) {
			e.printStackTrace();
			
			System.out.println("Removing Stream... (client thread)" );
			MasterServer.clientOutputStreamList.remove(out);
			System.out.println("Removed Stream (client thread)" );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
