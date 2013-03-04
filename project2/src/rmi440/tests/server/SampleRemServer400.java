package rmi440.tests.server;

import rmi440.servercode.Binder;
import rmi440.tests.common.RemoteBar;

/**
 * A sample server for testing purposes
 */
public class SampleRemServer400 {

	public static void main(String[] args) {
		String name1 = "rem1";
		RemoteBar rem1 = new RemoteBarImpl();
		
		String name2 = "rem2";
		RemoteBar rem2 = new RemoteBarImpl();
		
		Binder.bindObject(name1, "rmi440.tests.common.RemoteBar", rem1);
		Binder.bindObject(name2, "rmi440.tests.common.RemoteBar", rem2);
	}
}
