package processmanagerstuff;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalancer implements Runnable {

	//Stores output streams of all active clients
	private ArrayList<ObjectOutputStream> clientOutputStreamList = new ArrayList<ObjectOutputStream>();

	//Stores all filePaths of all processes running on all the clients (maps processId to filePath)
	public static ConcurrentHashMap<Integer, String> processFilePaths = new ConcurrentHashMap<Integer, String>();
	
	//Stores the confirmation messages as received from each client thread (maps clientId to the message)
	public static ConcurrentHashMap<Integer, String> clientMessageStatus = new ConcurrentHashMap<Integer, String>();
	
	//Stores all filePaths each client must deal with after load balancing (size = numSlaves)
	private HashMap<Integer, String> loadBalancedFilePaths = new HashMap<Integer, String>();
	
	public LoadBalancer(ArrayList<ObjectOutputStream> outputStreamList) {
		this.clientOutputStreamList = outputStreamList;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(5000);
				System.out.println("size of output stream list: " + clientOutputStreamList.size() + " (load balancer)");
				for (ObjectOutputStream out : clientOutputStreamList) {
					sendStartLoadBalanceSignal(out);
				}
				while (true && clientOutputStreamList.size() != 0 ) {
					if (clientMessageStatus.size() == clientOutputStreamList.size()) {
						System.out.println("Load Balancing Bro");
						balanceLoad();
						for (int i = 0; i < clientOutputStreamList.size(); i++) {
							ObjectOutputStream out = clientOutputStreamList.get(i);
							out.flush();
							out.writeObject(loadBalancedFilePaths.get(i));
							out.flush();
							sendStopLoadBalanceSignal(out);
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
		
		int numClients = clientOutputStreamList.size();
		int i = 0;
		System.out.println(" Length of Output Stream List in Balance Load: " + numClients);
		
		for (String filePath : processFilePaths.values()) {
			String currentPath = loadBalancedFilePaths.get(i % numClients);
			String finalPath = currentPath == null || currentPath.length() == 0 ? filePath : currentPath + "," + filePath;
			loadBalancedFilePaths.put(i % numClients, finalPath);
			i++;
		}
		System.out.println(" Length of All Processes \n" + ProcessManager3.allProcesses.size());
		for (int processId : ProcessManager3.allProcesses.keySet()) {
			String filePath = ProcessManager3.allProcesses.get(processId) + "\t" + processId;
			String currentPath = loadBalancedFilePaths.get(i % numClients);
			String finalPath = currentPath == null || currentPath.length() == 0 ? filePath : currentPath + "," + filePath;
			loadBalancedFilePaths.put(i % numClients, finalPath);
			i++;
			ProcessManager3.allProcesses.remove(processId);
		}
	}
	
	/**
	 * A function that sends a start signal to it's client.
	 * @return Sent successfully or not
	 */
	public boolean sendStartLoadBalanceSignal(ObjectOutputStream out) {
		try {
			out.flush();
			out.writeObject((Object) new String("__START__"));
			out.flush();
			System.out.println(" Sent Start (load balancer)");
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
	 * @return Sent successfully or not
	 */
	public boolean sendStopLoadBalanceSignal(ObjectOutputStream out) {
		try {
			out.flush();
			out.writeObject((Object) new String("__DONE__"));
			out.flush();
			System.out.println(" Sending Done (load balancer)");
			return true;
		} catch (IOException e) {
			System.out
					.println("ERROR: Couldn't send DONE load balance signal to client");
			e.printStackTrace();
		}
		return false;
	}
}
