package registryROR;

import java.lang.reflect.Proxy;

public class LocalizeObject {

	public static Object localize(RemoteObjectRef r) throws IllegalArgumentException,
			ClassNotFoundException {
		return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
				new Class<?>[] { Class.forName(r.getInterfaceName1()) },
				new ProxyHandler(r));
	}
}
