package registryROR;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public class SampleFooServer440 {

	/**
	 * @param args
	 */
	public static Foo fooSample;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ConcurrentHashMap<String, Object> remoteObjectsMap = new ConcurrentHashMap<String, Object>();
		
		int port = 4023; // registry prt: above 1024 so that we can run it.
		String name = "foo1";
		fooSample = new FooImpl();
		remoteObjectsMap.put(name, fooSample);
		
		// Spawn proxy
		System.out.println("Spawning Proxy Thread on Server");
		Thread t = new Thread(new ServerProxy(port, "localhost", remoteObjectsMap));
		t.start();

		// Rebind
		//Registry registry;
		try {
			//registry = LocateRegistry.getRegistry(port);
			// InetAddress addr = InetAddress.getLocalHost();
			// String hostname = addr.getHostName();
//			RemoteObjectRef(String ip, int port, int obj_key, String riname,
//					String objectName) 
			RemoteObjectRef ror = new RemoteObjectRef("localhost", port, 1,
					"registryROR.Foo", name);
			RMIRegistry440.rebind(name, ror);

			// registry.rebind(name, fooSample);
			System.out.println("Rebing Object");

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		System.out.println("HelloGiver bound and ready to give greetings");

	}

}
