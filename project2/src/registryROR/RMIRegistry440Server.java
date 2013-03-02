package registryROR;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The RMI Registry! This class is the actual registry. It
 * maintains a local object that keeps a map of all names
 * to their remote objects, and serves requests like rebind
 * and lookup
 */
public class RMIRegistry440Server {

	private static Integer port = 0;
	private static String hostname = null;
	private static ConcurrentHashMap<String, Remote440> remoteObjects;

	public static void main(String[] args) {

		// Get the port the registry must run on
		if (args.length != 1) {
			System.out.println("[ERROR] Usage java RMIRegistry440Server <port>");
			System.exit(0);
		}

		try {
			port = Integer.valueOf(args[0]).intValue();
			if (port < 1024 || port > 49151) {
				System.out.println("[ERROR] Port number must "
						+ "range from 1024 - 49151 (including)");
				System.exit(0);
			}
		} catch (Exception e) {
			System.out.println("[ERROR] Port number must be an integer!");
			System.exit(0);
		}

		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			System.out.println("[ERROR] RMIRegistry440Server couldn't get own hostname. Quitting...");
			System.exit(0);
		}

		System.out.println("[INFO] RMIRegistry440 running on port " + port
				+ " and host " + hostname);

		remoteObjects = new ConcurrentHashMap<String, Remote440>();

		ServerSocket server = null;
		Socket clientSocket = null;

		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[ERROR] Could not listen on port: " + port);
			System.exit(0);
		}
		
		ObjectOutputStream out = null;
		
		while (true) {
			try {
				clientSocket = server.accept();
				OutputStream output = clientSocket.getOutputStream();
				InputStream input = clientSocket.getInputStream();
				out = new ObjectOutputStream(output);
				ObjectInputStream in = new ObjectInputStream(input);

				
				// Get the request, and carry it out, then send back response
				RMIRegistryMessage message = (RMIRegistryMessage) in.readObject();
				if (message.isLookupMessage()) {
					Remote440 remoteObj = lookup(message.getName());
					out.writeObject(new RMIRegistryMessage(null, remoteObj, RMIRegistryMessage.Request.LOOKUP, null, null));
				} else if (message.isRebindMessage()) {
					rebind(message.getName(), message.getRemoteRef());
					out.writeObject(new RMIRegistryMessage(null, null, RMIRegistryMessage.Request.REBIND, null, null));
				} else if (message.isAllObjectsMessage()) {
					ArrayList<String> allObjects = getAllObjects();
					out.writeObject(new RMIRegistryMessage(null, null, RMIRegistryMessage.Request.ALLOBJECTS, null, allObjects));
				}
			} catch (RemoteException440 e) {
				System.out.println("[ERROR] Remote Exception in RMI Registry");
				try {
					out.writeObject(new RMIRegistryMessage(null, null, RMIRegistryMessage.Request.REBIND, e, null));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				System.out.println("[ERROR] IO Exception RMI Registry");
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e) {
				System.out.println("[ERROR] Class not found exception RMI Registry");
				e.printStackTrace();
			}
		}
	}

	/**
	 * The rebind function in the registry. It binds the remote object to the
	 * given name. If the name has an entry, then that is replaced with the new
	 * object
	 */
	private static void rebind(String name, Remote440 ror) throws RemoteException440 {
		System.out.println("[INFO] Binding object with name " + name);
		remoteObjects.put(name, ror);
	}

	/**
	 * The lookup function of the registry. It throws an exception if the name
	 * isn't bound to anything, otherwise it returns the object bound to the name
	 */
	private static Remote440 lookup(String name) throws RemoteException440 {
		if (!remoteObjects.containsKey(name)) {
			throw new RemoteException440("[ERROR] Name " + name
					+ " is not bound! (RMIRegistry440Server)");
		}
		return remoteObjects.get(name);
	}
	
	/**
	 * A function that returns a string of all objects in the registry
	 */
	private static ArrayList<String> getAllObjects() {
		ArrayList<String> result = new ArrayList<String>();
		
		for (Entry<String, Remote440> e : remoteObjects.entrySet()) {
			String entry = "{" + e.getKey() + "," + e.getValue().toString() + "}";
			result.add(entry);
		}
		return result;
	}
}
