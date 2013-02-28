package registryROR;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * This class is used by the server side implementations to
 * do the common process for a remote object, i.e. binding it
 * in the registry and starting a proxy thread for that object,
 */
public class Binder {

	public static void bindObject(String name, String interface_name, Object impl) {

		// Spawn proxy thread for remote object
		System.out.println("Spawning Proxy Thread on Server");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(0);
		} catch (IOException e) {
			System.out.println("[ERROR]: Couldn't start proxy for object: "+ name);
			e.printStackTrace();
			System.exit(0);
		}
		int port = serverSocket.getLocalPort();
		System.out.println(" Binder on port = " + serverSocket.getLocalPort());

		// Start proxy thread for object
		Thread t = new Thread(new ServerRemoteObjectThread(impl, serverSocket));
		t.start();

		try {
			// Rebind the remote object in registry
			InetAddress addr = InetAddress.getLocalHost();
			String hostname = addr.getHostName();
			RemoteObjectRef ror = new RemoteObjectRef(hostname, port, interface_name, name);
			RMIRegistry440.rebind(name, ror);
			System.out.println("Rebind Object");
		} catch (UnknownHostException e) {
			System.out.println("[ERROR]: Binder couldn't get self host");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			System.out.println("[ERROR]: Error in binding object to name in registry");
			e.printStackTrace();
		}
	}
}
