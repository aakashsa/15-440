package registryROR;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SampleFooServer440 {

	/**
	 * @param args
	 */
	public static Foo fooSample;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int port = 4023; // registry prt: above 1024 so that we can run it.
		String name = "foo1";
		fooSample = new FooImpl();

		// Spawn proxy
		System.out.println("Spawning Proxy Thread on Server");
		Thread t = new Thread(new ServerProxy(fooSample, port, "localhost"));
		t.start();

		// Rebind
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(port);
			// InetAddress addr = InetAddress.getLocalHost();
			// String hostname = addr.getHostName();

			RemoteObjectRef ror = new RemoteObjectRef("localhost", port, 1,
					"registryROR.Foo", name);
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
