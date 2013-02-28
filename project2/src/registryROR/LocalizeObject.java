package registryROR;

import java.lang.reflect.Proxy;
import java.net.MalformedURLException;

/**
 * This class is used to abstract the process of obtaining a local
 * instance of the remote object from the registry.
 */
public class LocalizeObject {

	/**
	 * This method takes in the name of the remote object, gets the
	 * ROR from registry, and returns a proxy for that object to the
	 * application
	 */
	public static Object localize(String name) throws IllegalArgumentException,
			ClassNotFoundException {

		RemoteObjectRef r = null;
		try {
			r = (RemoteObjectRef) RMIRegistry440.lookup(name);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		// Create a new proxy for the remote object, and return it
		return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
				new Class<?>[] { Class.forName(r.getInterfaceName1()) },
				new ProxyHandler(r));
	}
}
