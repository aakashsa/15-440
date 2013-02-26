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
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Still need to send back exceptions like remote exception for methods like
 * rebind
 * 
 * @author nikhiltibrewal
 * 
 */
public class RMIRegistry440Server {

	private static Integer port = 0;
	private static String hostname = null;
	private static ConcurrentHashMap<String, Remote440> remoteObjects;

	public static void main(String[] args) {

		// Get the port the registry must run on
		if (args.length != 1) {
			System.out
					.println("[ERROR]: Usage java RMIRegistry440Server <port>");
			System.exit(0);
		}

		try {
			port = Integer.valueOf(args[0]).intValue();
			if (port < 1024 || port > 49151) {
				System.out.println("[ERROR]: Port number must "
						+ "range from 1024 - 49151 (including)");
				System.exit(0);
			}
		} catch (Exception e) {
			System.out.println("[ERROR]: Port number must be an integer!");
			System.exit(0);
		}

		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			System.out
					.println("[ERROR]: RMIRegistry440Server couldn't get own hostname. Quitting...");
			System.exit(0);
		}

		System.out.println("[INFO]: RMIRegistry440 running on port " + port
				+ " and host " + hostname);

		remoteObjects = new ConcurrentHashMap<String, Remote440>();

		ServerSocket server = null;
		Socket clientSocket = null;

		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("[ERROR]: Could not listen on port: " + port);
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

				RMIRegistryMessage message = (RMIRegistryMessage) in
						.readObject();
				if (message.isLookupMessage()) {
					Remote440 remoteObj = lookup(message.getName());
					out.writeObject(new RMIRegistryMessage(null, remoteObj,
							true, null));
				} else {
					rebind(message.getName(), message.getRemoteRef());
					out.writeObject(new RMIRegistryMessage(null, null, false,
							null));
				}
			} catch (RemoteException e) {
				System.out.println("Remote Exception E 93");
				try {
					out.writeObject(new RMIRegistryMessage(null, null, false, e));
				} catch (IOException e1) {
					System.out.println("IO Exception E 97");
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException e) {
				System.out.println("IO Exception E 102");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("Class NOT FOUND Exception E 105");
				e.printStackTrace();
			}
		}
	}

	private static void rebind(String name, Remote440 ror) throws RemoteException {
//		throw new RemoteException();
		if (remoteObjects.contains(name)) {
			System.out.println("[INFO]: Rebinding object with name " + name);
		}
		System.out.println("[INFO]: BINDINGbinding object with name " + name);
		remoteObjects.put(name, ror);
		for (String a : remoteObjects.keySet()) {
			System.out.println(" KEYS + " + a);
		}
	}

	private static Remote440 lookup(String name) throws RemoteException {
		for (String a : remoteObjects.keySet()) {
			System.out.println(" KEYS + " + a);
		}
		if (!remoteObjects.containsKey(name)) {
			throw new RemoteException("[ERROR]: Name " + name
					+ " is not bound! (RMIRegistry440Server)");
		}
		return remoteObjects.get(name);
	}
}
