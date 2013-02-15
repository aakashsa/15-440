package registryROR;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyDemo {
	public static void main(String[] args) {
		Foo fooProxy = (Foo) Proxy.newProxyInstance(
				ClassLoader.getSystemClassLoader(),
				new Class<?>[] {Foo.class},
				new ProxyHandler(new FooImpl()));

		fooProxy.bar();
		List<Double> list = new ArrayList<Double>();
		list.add(2.0);
		fooProxy.baz(10, list);
	}

	private static class ProxyHandler implements InvocationHandler {
		private final Foo impl;

		public ProxyHandler(Foo impl) {
			this.impl = impl;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			System.out.printf("Someone called method %s with arguments\n",
					method.getName());
			Type[] params = method.getGenericParameterTypes();
			for (int i = 0; i < params.length; i++) {
				System.out.printf("%d: %s = %s\n", i, params[i], args[i]);
			}

			Object returning = method.invoke(impl, args);
			System.out.printf("Returning %s\n\n", returning);
			return returning;
		}
	}

	private interface Foo {
		int bar();
		Map<String, String> baz(int a, List<Double> b);
	}

	private static class FooImpl implements Foo {

		@Override
		public int bar() {
			return 10;
		}

		@Override
		public Map<String, String> baz(int a, List<Double> b) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("Hello", b.toString());
			return map;
		}

	}
}
