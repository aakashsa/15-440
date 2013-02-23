package registryROR;

import java.io.IOException;
import java.net.UnknownHostException;

public class ProxyDemo {
	public static void main(String[] args) {
		Foo fooProxy = null;
		RemoteBar rbarProxy = null;
		try {
			fooProxy = (Foo) LocalizeObject.localize("foo1");
			rbarProxy = (RemoteBar) LocalizeObject.localize("Rem");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("Return 2 =  " + fooProxy.bar(5));
		System.out.println("Return 1 =  " + fooProxy.barRem(rbarProxy, 17));

		NonSerializable z = new NonSerializable();
		//System.out.println("Return 1 =  " + fooProxy.bar2(z));
		// ArrayList<Integer> z = new ArrayList<Integer>();
		// z.add(1);
		// z.add(2);
		// z.add(3);
		// z.add(4);
		// z.add(5);

		// fooProxy.bar();
		// List<Double> list = new ArrayList<Double>();
		// list.add(2.0);
		// fooProxy.baz(10, list);
	}

}
