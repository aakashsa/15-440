package processmanagerstuff;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalancer implements Runnable {

	private ArrayList<Socket> clientSocketList = new ArrayList<Socket>();

	public static ConcurrentHashMap<Integer, String> processFilePaths = new ConcurrentHashMap<Integer, String>();
	public static ConcurrentHashMap<Integer, String> clientMessageStatus = new ConcurrentHashMap<Integer, String>();
	private HashMap<Integer, String> loadBalancedFilePaths = new HashMap<Integer, String>();
	
	public LoadBalancer(ArrayList<Socket> clientSocketList) {
		this.clientSocketList = clientSocketList;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(5000);
				for (Socket clientSocket : clientSocketList) {
					sendStartLoadBalanceSignal(clientSocket);
				}
				while (true && clientSocketList.size() != 0 ) {
					if (clientMessageStatus.size() == clientSocketList.size()) {
						System.out.println("Load Balancing Bro");
						balanceLoad();
						for (int i = 0; i < clientSocketList.size(); i++) {
							Socket clientSocket = clientSocketList.get(i);
							ObjectOutputStream out = new ObjectOutputStream(
									clientSocket.getOutputStream());
							out.writeObject(loadBalancedFilePaths.get(i));
							sendStopLoadBalanceSignal(clientSocket);
						}
						clientMessageStatus.clear();
						loadBalancedFilePaths.clear();
						processFilePaths.clear();
						break;
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Uses the existing processFilePaths to fill up the loadBalancedFilePaths hash map
	 */
	private void balanceLoad() {
		int numClients = clientSocketList.size();
		int i = 0;
		
		for (int processId : processFilePaths.keySet()) {
			String filePath = processFilePaths.get(processId);
			String currentPath = loadBalancedFilePaths.get(i % numClients);
			String finalPath = currentPath.length() == 0 ? filePath : currentPath + "," + filePath;
			loadBalancedFilePaths.put(i % numClients, finalPath);
			i++;
		}
		
		for (int processId : ProcessManager3.allProcesses.keySet()) {
			String filePath = ProcessManager3.allProcesses.get(processId) + "\t" + processId;
			String currentPath = loadBalancedFilePaths.get(i % numClients);
			String finalPath = currentPath.length() == 0 ? filePath : currentPath + "," + filePath;
			loadBalancedFilePaths.put(i % numClients, finalPath);
			i++;
			ProcessManager3.allProcesses.remove(processId);
		}
	}
	
	/**
	 * A function that sends a start signal to it's client.
	 * 
	 * @return
	 */
	public boolean sendStartLoadBalanceSignal(Socket clientSocket) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					clientSocket.getOutputStream());
			out.writeObject((Object) new String("__START__"));
			System.out.println(" Sent Start");
			return true;
		} catch (IOException e) {
			System.out
					.println("ERROR: Couldn't send START load balance signal to client");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * A function that sends a done signal to it's client.
	 * 
	 * @return
	 */
	public boolean sendStopLoadBalanceSignal(Socket clientSocket) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					clientSocket.getOutputStream());
			out.writeObject((Object) new String("__DONE__"));
			System.out.println(" Sending Done");
			return true;
		} catch (IOException e) {
			System.out
					.println("ERROR: Couldn't send DONE load balance signal to client");
			e.printStackTrace();
		}
		return false;
	}
}
