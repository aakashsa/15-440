package registryROR;

import java.lang.reflect.Proxy;

/**
 * This class is used to abstract the process of obtaining a local
 * instance of the remote object from the registry.
 */
public class LocalizeObject {

	/**
	 * This method takes in the name of the remote object, gets the
	 * ROR from registry, and returns a proxy for that object to the
	 * application
	 * @param name = object name in registry
	 * @throws Exception - Exception if lookup fails
	 * @return proxy instance if call successful, else null
	 */
	public static Object localize(String name) throws Exception {

		RemoteObjectRef r = null;
		r = (RemoteObjectRef) RMIRegistry440.lookup(name);
		
		// Create a new proxy for the remote object, and return it
		Object proxy = null;
		try {
			proxy = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
					new Class<?>[] { Class.forName(r.getInterfaceName1()) },
					new ProxyHandler(r));
		} catch (IllegalArgumentException e) {
			System.out.println("[ERROR] Couldn't create new proxy instance for " + name);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("[ERROR] Couldn't create new proxy instance for " + name);
			e.printStackTrace();
		}
		
		return proxy;
	}
}
