package processmanagerstuff;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A runnable object that runs on the server for a particular
 * client. It is responsible for communicating information
 * with the client.
 *
 */
public class ClientThread implements Runnable {

	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int id = -1;

	public ClientThread(int id, ObjectOutputStream out, ObjectInputStream in) {
		this.id = id;
		
		// Send the slave a new id
		try {
			this.out = out;
			out.flush();
			out.writeObject((Object) new Integer(id));
			out.flush();
			this.in = in;
		} catch (IOException e) {
			System.out.println("ERROR: Client thread couldn't " +
					"send id to slave (" + e.getLocalizedMessage() + ")");
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				// Read for all file paths from slave
				String receivedString = (String) in.readObject();
				String[] filePaths = receivedString.split(",");
				
				//Add each file path to a collection in the Load Balancer
				for (String path : filePaths) {
					if (path.length() > 0) {
						String[] pathContents = path.split("\\t");
						int processId = Integer.parseInt(pathContents[1]);
						LoadBalancer.processFilePaths.put(processId, path);
					}
				}
				// Send a message to Load balancer after sending all paths
				LoadBalancer.clientMessageStatus.put(this.id, "SENT");
			}
		} catch (IOException e) {
			System.out.println("Slave " + id + " disconnected!");
			MasterServer.clientOutputStreamList.remove(out);
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
		}
	}
}
