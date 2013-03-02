package registryROR;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This is a wrapper around the
 * lookup and rebind methods of the registry. It
 * essentially takes in the necessary objects from
 * the caller, and communicates back and forth with
 * the registry to send and get results.
 */
public class RMIRegistry440 {

	private static ObjectInputStream in = null;
	private static ObjectOutputStream out = null;
	private static Socket clientSocket = null;

	// This value would need to be known before hand
	private static int registryPort = 4444;
	
	/**
	 * Lookup method - takes in the name of object to lookup,
	 * and throws exception if anything goes wrong
	 */
	public static Remote440 lookup(String name) throws Exception {
		RMIRegistryMessage msg = new RMIRegistryMessage(name, null, RMIRegistryMessage.Request.LOOKUP, null, null);
		RMIRegistryMessage retMsg = ioHelper(msg);
		return retMsg.getRemoteRef();
	}

	/**
	 * Rebind method - binds the given name to the given remote object.
	 * If there's an exception, it throws that exception.
	 */
	public static void rebind(String name, Remote440 remote) throws Exception {
		RMIRegistryMessage msg = new RMIRegistryMessage(name, remote, RMIRegistryMessage.Request.REBIND, null, null);
		ioHelper(msg);
	}
	
	/**
	 * All objects method
	 */
	public static ArrayList<String> getAllObjects() throws Exception {
		RMIRegistryMessage msg = new RMIRegistryMessage(null, null, RMIRegistryMessage.Request.ALLOBJECTS, null, null);
		RMIRegistryMessage retMsg = ioHelper(msg);
		ArrayList<String> ret = null;
		try {
			ret = (ArrayList<String>) retMsg.getRetVal();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * A helper method that sends the message to the RMI registry,
	 * gets the response, check for an exception (and throws if there was one
	 * on the registry), else returns the response.
	 */
	private synchronized static RMIRegistryMessage ioHelper(
			RMIRegistryMessage msg) throws Exception {
		clientSocket = new Socket("localhost", registryPort);
		OutputStream output = clientSocket.getOutputStream();
		InputStream input = clientSocket.getInputStream();
		out = new ObjectOutputStream(output);
		out.flush();
		in = new ObjectInputStream(input);

		out.writeObject(msg);
		RMIRegistryMessage retMsg = (RMIRegistryMessage) in.readObject();
		in.close();
		out.close();
		if (retMsg.getException() != null) {
			System.out.println("[INFO] Got back exception message from RMI registry");
			throw retMsg.getException();
		} else
			return retMsg;
	}

}
