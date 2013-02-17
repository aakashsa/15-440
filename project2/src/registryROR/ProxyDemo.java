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
		Foo fooProxy = (Foo) Proxy.newProxyInstance(
				ClassLoader.getSystemClassLoader(),
				new Class<?>[] { Foo.class }, new ProxyHandler(new FooImpl()));

		fooProxy.bar();
		List<Double> list = new ArrayList<Double>();
		list.add(2.0);
		//fooProxy.baz(10, list);
	}

	
	}

	private interface Foo {
		int bar();

		Map<String, String> baz(int a, List<Double> b);
	}

	private static class FooImpl implements Foo {

		@Override
		public int bar() {
			throw new RuntimeException();
			// return 10;
		}

		@Override
		public Map<String, String> baz(int a, List<Double> b) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("Hello", b.toString());
			return map;
		}

	}
}
