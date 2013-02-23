package registryROR;

import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class LocalizeObject {

	
	
	public static Object localize(String name) throws IllegalArgumentException,
			ClassNotFoundException {
		RemoteObjectRef r = null;
		try {
			r = (RemoteObjectRef) RMIRegistry440.lookup(name);
			//r = (RemoteObjectRef) Naming.lookup(name);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
				new Class<?>[] { Class.forName(r.getInterfaceName1()) },
				new ProxyHandler(r));
	}
}
