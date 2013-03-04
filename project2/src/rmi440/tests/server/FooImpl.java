package rmi440.tests.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rmi440.tests.common.Foo;
import rmi440.tests.common.NonSerializable;
import rmi440.tests.common.RemoteBar;


/**
 * A sample remote object for testing purposes
 */
public class FooImpl implements Foo {

	private int counter = 0;
	private ArrayList<Integer> ints = new ArrayList<Integer>();

	/**
	 * A function that reverses a list and returns it back
	 */
	public ArrayList<Integer> reverseList(ArrayList<Integer> a) {

		ArrayList<Integer> b = new ArrayList<Integer>();
		for (int i = a.size() - 1; i >= 0; i--) {
			b.add(a.get(i));
		}
		return b;
	}

	/**
	 * A function that adds a value to a map and returns the map
	 */
	public Map<String, String> addStringifiedListToMap(List<Double> b) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Hello", b.toString());
		return map;
	}

	/**
	 * A function that prints out something
	 */
	public void printOut() {
		System.out.println("Output from printOut function");
	}

	/**
	 * A function that is given a non serializable object
	 */
	public String notSerializableArgument(NonSerializable a) {
		return "Should've thrown an exception";
	}

	/**
	 * A function that returns a new instance of the same remote object type
	 * as in argument
	 */
	public RemoteBar renewRemoteArgument(RemoteBar rem) {
		return rem.renew();
	}
	
	/**
	 * A function that modifies a given list by using behavior of a remote argument
	 */
	public ArrayList<Integer> modifyByRemoteObj(ArrayList<Integer> a, RemoteBar b) {
		int neededSize = b.getRandomSize(a);
		ints.clear();
		for (int i = 0; i < neededSize; i++) {
			ints.add(a.get(i));
		}
		return ints;
	}
	
	/**
	 * A function that returns the same instance of a remote object as in argument
	 */
	public RemoteBar returnRemoteArgument(RemoteBar r) {
		return r;
	}
	
	/**
	 * A function that returns a not serializable object
	 */
	public NonSerializable returnObjectNotSerializable() {
		return new NonSerializable();
	}
	
	/**
	 * Functions that do some counter operations to alter inner state of object
	 */
	public int getCounter() {
		return counter;
	}

	public void increment() {
		counter++;
	}

	public void decrement() {
		counter--;
	}
}
