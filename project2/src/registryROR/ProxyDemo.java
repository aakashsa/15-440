package registryROR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marshal.MessageInvokeFunction;

public class ProxyDemo {
	public static void main(String[] args) {
		RemoteObjectRef r = new RemoteObjectRef("127.17", 1234, 3,
				"registryROR.Foo");
		Foo fooProxy = null;
		Class<?>[] a = FooImpl.class.getInterfaces();
		for (int i = 0; i < a.length; i++)
			System.out.println("Interface " + i + " " + a[i] + " \n");

		if (Remote440.class.isAssignableFrom(Foo.class)) {
			System.out.println(" Extends Remote BRO !");
		}
		try {
			fooProxy = (Foo) LocalizeObject.localize(r);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fooProxy.bars();
		// fooProxy.bar();
		List<Double> list = new ArrayList<Double>();
		list.add(2.0);
		// fooProxy.baz(10, list);
	}

}
