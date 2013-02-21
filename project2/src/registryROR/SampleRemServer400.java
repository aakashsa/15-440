package registryROR;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SampleRemServer400 {

	/**
	 * @param args
	 */
	public static RemoteBar RemSample;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int port = 4026; // registry prt: above 1024 so that we can run it.
		String name = "Rem";
		RemSample = new RemoteBarImpl();

		// Spawn proxy
		System.out.println("Spawning Proxy Thread on Server");
		Thread t = new Thread(new ServerProxy(RemSample, port, "localhost"));
		t.start();

		// Rebind
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(port);
			// InetAddress addr = InetAddress.getLocalHost();
			// String hostname = addr.getHostName();

			RemoteObjectRef ror = new RemoteObjectRef("localhost", port, 1,
					"registryROR.RemoteBar", "Rem");
			Naming.rebind(name, ror);

			// registry.rebind(name, fooSample);
			System.out.println("Rebing Object");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("HelloGiver bound and ready to give greetings");

	}

}
