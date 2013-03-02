package registryROR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A sample remote object for testing purposes
 */
public class FooImpl implements Foo {

	private int counter = 0;
	private ArrayList<Integer> ints = new ArrayList<Integer>();

	@Override
	public int bar(int a) {
		System.out.println("IN BAR 1!");
		return a + 10;
	}

	/**
	 * A function that reverses a list and returns it back
	 */
	@Override
	public ArrayList<Integer> bar2(ArrayList<Integer> a) {

		ArrayList<Integer> b = new ArrayList<Integer>();
		for (int i = a.size() - 1; i >= 0; i--) {
			b.add(a.get(i));
		}
		return b;
	}

	/**
	 * A function that adds a value to a map and returns the map
	 */
	@Override
	public Map<String, String> baz(int a, List<Double> b) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Hello", b.toString());
		return map;
	}

	/**
	 * A function that prints out something
	 */
	@Override
	public void bars() {
		System.out.println("YO BARZZZZ");
	}

	/**
	 * A function that is given a non serializable object
	 */
	public String bar2(NonSerializable a) {
		System.out.println("YO BARZZZZ 2");
		return a.toString();
	}

	/**
	 * A function that based on current value prints it out, or
	 * modifies the value
	 */
	@Override
	public int barRem(int b) {
		if (b == 0) {
			System.out.println("Return Now");
			return -10;
		} else {
			System.out.println("In Foo b = " + b);
			return this.barRem(b - 1);
		}
	}

	public Foo renewRemoteArgument(RemoteBar rem) {
		return new FooImpl();
	}
	
	public ArrayList<Integer> modifyByRemoteObj(ArrayList<Integer> a, RemoteBar b) {
		int neededSize = b.getRandomSize(a);
		ints.clear();
		for (int i = 0; i < neededSize; i++) {
			ints.add(a.get(i));
		}
		return ints;
	}
	
	public RemoteBar returnRemoteArgument(RemoteBar r) {
		return r;
	}
	
	public NonSerializable returnObjectNotSerializable() {
		return new NonSerializable();
	}
	
	@Override
	public int getCounter() {
		return counter;
	}

	@Override
	public void increment() {
		counter++;
	}

	@Override
	public void decrement() {
		counter--;
	}
}
