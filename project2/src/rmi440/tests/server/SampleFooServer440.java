package rmi440.tests.server;

import rmi440.servercode.Binder;
import rmi440.tests.common.Foo;

/**
 * A sample server for testing purposes
 */
public class SampleFooServer440 {

	public static void main(String[] args) {
		String name1 = "foo1";
		Foo foo1 = new FooImpl();
		
		String name2 = "foo2";
		Foo foo2 = new FooImpl();
		
		Binder.bindObject(name1, "rmi440.tests.common.Foo", foo1);
		Binder.bindObject(name2, "rmi440.tests.common.Foo", foo2);
	}
}
