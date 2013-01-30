package processmanagerstuff;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread implements Runnable {

	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int id = -1;

	public ClientThread(Socket clientSocket,int id) {
		this.id = id;
		System.out.println("Client Thread");
		try {

			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			out.flush();
			out.writeObject((Object) new Integer(id));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("accepted a connection from slave");
	}

	public int getClientId() {
		return id;
	}

	@Override
	public void run() {

		try {
			while (true) {
				String receivedString = (String) in.readObject();
				System.out.println(" Receieved Filepath string from Slave : " + receivedString);
				
				String[] filePaths = receivedString.split(",");
				System.out.println("Client Thread File Paths Code");
				for (String path: filePaths){
					String[] pathContents = path.split("\\t");
					int processId = Integer.parseInt(pathContents[1]);
					LoadBalancer.processFilePaths.put(processId, path);
				}
				LoadBalancer.clientMessageStatus.put(this.id, "SENT");

			}
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			 e.printStackTrace();
		}
	}
}
