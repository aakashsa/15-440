package processmanagerstuff;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible for load balancing. It sends
 * a signal to all slaves telling them to send all
 * running process on them to the load balancer. Then it does
 * load balancing and sends the slaves new processes back.
 */
public class LoadBalancer implements Runnable {

	// Stores output streams of all active clients
	private ArrayList<ObjectOutputStream> clientOutputStreamList = 
			new ArrayList<ObjectOutputStream>();

	// Stores all filePaths of all processes running on all the clients (maps
	// processId to filePath)
	public static ConcurrentHashMap<Integer, String> processFilePaths = 
			new ConcurrentHashMap<Integer, String>();

	// Stores the confirmation messages as received from each client thread
	// (maps clientId to the message)
	public static ConcurrentHashMap<Integer, String> clientMessageStatus = 
			new ConcurrentHashMap<Integer, String>();

	// Stores all filePaths each client must deal with after load balancing
	// (size = numSlaves)
	private HashMap<Integer, String> loadBalancedFilePaths = 
			new HashMap<Integer, String>();

	public LoadBalancer(ArrayList<ObjectOutputStream> outputStreamList) {
		this.clientOutputStreamList = outputStreamList;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(5*1000);
				
				// Start signal for slaves to write the processes 
				// to disk and send back file paths
				for (ObjectOutputStream out : clientOutputStreamList) {
					sendStartLoadBalanceSignal(out);
				}
				
				while (true && clientOutputStreamList.size() != 0) {
					// Check if all slaves have sent file paths
					if (clientMessageStatus.size() == 
							clientOutputStreamList.size()) {
						// Do load balancing 
						balanceLoad();
						for (int i = 0; i < clientOutputStreamList.size(); i++) {
							ObjectOutputStream out = 
									clientOutputStreamList.get(i);
							out.flush();
							// Write the file paths to slave over socket
							out.writeObject(loadBalancedFilePaths.get(i));
							out.flush();
							// Send done signal to slaves
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
			System.out.println("ERROR: Load balancer couldn't sleep " +
					"(" + e.getLocalizedMessage() + ")");
		} catch (IOException e) {
			System.out.println("ERROR: Load balancer had trouble in writing " +
					"file paths to slave. (" + e.getLocalizedMessage() + ")");
		}
	}

	/**
	 * Uses the existing processFilePaths to fill up the loadBalancedFilePaths
	 * hash map. Then we just need to send out file paths here to each client
	 */
	private void balanceLoad() {

		int numClients = clientOutputStreamList.size();
		int i = 0;
		
		// First distribute processes returned back from slaves
		for (String filePath : processFilePaths.values()) {
			String currPath = loadBalancedFilePaths.get(i % numClients);
			String finalPath = currPath == null || currPath.length() == 0 ?
					filePath : currPath + "," + filePath;
			loadBalancedFilePaths.put(i % numClients, finalPath);
			i++;
		}
		
		// Distribute new processes stored on Master PM
		for (int processId : ProcessManager.allProcesses.keySet()) {
			String filePath = ProcessManager.allProcesses.get(processId)
					+ "\t" + processId;
			String currPath = loadBalancedFilePaths.get(i % numClients);
			String finalPath = currPath == null || currPath.length() == 0 ? 
					filePath : currPath + "," + filePath;
			loadBalancedFilePaths.put(i % numClients, finalPath);
			i++;
			ProcessManager.allProcesses.remove(processId);
		}
	}

	/**
	 * A function that sends a start signal to the slave having
	 * the output stream in parameter
	 * 
	 * @return Sent successfully or not
	 */
	public boolean sendStartLoadBalanceSignal(ObjectOutputStream out) {
		try {
			out.flush();
			out.writeObject((Object) new String("__START__"));
			out.flush();
			return true;
		} catch (IOException e) {
			System.out.println("ERROR: Couldn't send START load balance " +
					"signal to client (" + e.getLocalizedMessage() + ")");
		}
		return false;
	}

	/**
	 * A function that sends a done signal the slave having
	 * the output stream in parameter
	 * @return Sent successfully or not
	 */
	public boolean sendStopLoadBalanceSignal(ObjectOutputStream out) {
		try {
			out.flush();
			out.writeObject((Object) new String("__DONE__"));
			out.flush();
			return true;
		} catch (IOException e) {
			System.out.println("ERROR: Couldn't send DONE load balance " +
					"signal to client (" + e.getLocalizedMessage() + ")");
		}
		return false;
	}
}
