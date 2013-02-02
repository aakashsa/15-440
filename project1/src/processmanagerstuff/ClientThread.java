package processmanagerstuff;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class ClientThread implements Runnable {

	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int id = -1;

	public ClientThread(OutputStream output, InputStream input, int id, ObjectOutputStream out, ObjectInputStream in) {
		this.id = id;
		
		System.out.println("Client Thread");
		try {
			System.out.println("sending ID to slave...");
			this.out = out;//new ObjectOutputStream(output);
			out.flush();
			out.writeObject((Object) new Integer(id));
			out.flush();
			this.in = in;//new ObjectInputStream(input);
			System.out.println("client thread sent id....");
			//out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Client thread sent id to Slave");
	}

	public int getClientId() {
		return id;
	}

	@Override
	public void run() {

		try {
			while (true) {
				// Block read for allFilePaths from slave
				String receivedString = (String) in.readObject();
				System.out.println(" Receieved Filepath string from Slave : "
						+ receivedString);

				String[] filePaths = receivedString.split(",");
				System.out.println("Client Thread File Paths Code, filePaths received: ");
				Arrays.toString(filePaths);
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
			
			System.out.println(" Removing Stream " );
			MasterServer.clientOutputStreamList.remove(out);
			System.out.println(" Removed Stream " );
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void printArray(String[] arr) {
		for (String s : arr) {
			System.out.println(s);
		}
	}
}
