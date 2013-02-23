package registryROR;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Our RMI Registry class
 * @author nikhiltibrewal
 *
 */
public class RMIRegistry440 {
	
	private static ObjectInputStream in = null;
	private static ObjectOutputStream out = null;
	private Socket clientSocket = null;
	
	public RMIRegistry440(int port, String hostname) throws UnknownHostException, IOException {
		//open connections to registry
		clientSocket = new Socket(hostname, port);
		OutputStream output = clientSocket.getOutputStream();
		InputStream input = clientSocket.getInputStream();
		out = new ObjectOutputStream(output);
		out.flush();
		in = new ObjectInputStream(input);
	}
	
	public static Remote440 lookup(String name) throws Exception {
		RMIRegistryMessage msg = new RMIRegistryMessage(name, null, true, null);
		RMIRegistryMessage retMsg = ioHelper(msg);
		return retMsg.getRemoteRef();
	}
	
	public static void rebind(String name, Remote440 remote) throws Exception {
		RMIRegistryMessage msg = new RMIRegistryMessage(name, remote, false, null);
		RMIRegistryMessage retMsg = ioHelper(msg);
	}
	
	private synchronized static RMIRegistryMessage ioHelper(RMIRegistryMessage msg) throws Exception {
		out.writeObject(msg);
		RMIRegistryMessage retMsg = (RMIRegistryMessage) in.readObject();
		if (retMsg.getException() != null) {
			throw retMsg.getException();
		}
		else return retMsg;
	}
	
}
