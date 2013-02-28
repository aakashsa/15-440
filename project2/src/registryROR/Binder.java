package registryROR;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.rmi.RemoteException;

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
			e.printStackTrace();
		}
		int port = serverSocket.getLocalPort();
		System.out.println(" Port = " + serverSocket.getLocalPort());

		Thread t = new Thread(new ServerRemoteObjectThread(impl, serverSocket));
		t.start();

		// Rebind
		// Registry registry;
		try {
			// registry = LocateRegistry.getRegistry(port);
			InetAddress addr = InetAddress.getLocalHost();
			String hostname = addr.getHostName();
			// System.out.println(" Hostname = " + hostname);
			RemoteObjectRef ror = new RemoteObjectRef(hostname, port,
					interface_name, name);
			RMIRegistry440.rebind(name, ror);
			// registry.rebind(name, fooSample);
			System.out.println("Rebing Object");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("HelloGiver bound and ready to give greetings");
	}
}
