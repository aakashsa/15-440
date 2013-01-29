package processmanagerstuff;

import interfaces.MigratableProcess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import processes.ThreadProcess;
import slavemanagerstuff.ChildWriter;

public class ClientThread implements Runnable {

	private ObjectOutputStream out ;
	private ObjectInputStream in ;
	private int id = -1;
	private String filePath = null;
	private int numProcess ;
	
	public ClientThread(Socket clientSocket) {
		System.out.println("Client Thread");
		try {
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			out.flush();
			out.writeObject((Object) new Integer(MasterServer.clientNumbers));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("accepted a connection from slave");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		//System.out.println("Spawned a New Thread for Connection");
		try {
			while(true){
				//				out.writeObject((Object) new String("src/myObject.data"));
				HeaderPacket header = (HeaderPacket) in.readObject();
				this.id = header.getId();
				this.filePath = header.getFilePath();
				this.numProcess  = header.getNumProcess();
				System.out.println("Client Number " + id  + " Wrote " + numProcess + " to file " + filePath);
				LoadBalancer.list.add(header);
				if (LoadBalancer.list.size() == MasterServer.clientNumbers)
					new Thread(new LoadBalancer(out,in)).start();

				
				
//			out.writeObject((Object) process);
//			ConcurrentHashMap<Long, ThreadProcess> allProcesses = (ConcurrentHashMap<Long, ThreadProcess>) in.readObject();
//			System.out.println("Received allProces Printing :");
//			for (Long k : allProcesses.keySet()) {
//				ThreadProcess tp = allProcesses.get(k);
//				System.out.println(tp.getProcess().toString());
//				
//			}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}
