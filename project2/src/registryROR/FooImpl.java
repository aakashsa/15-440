package registryROR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FooImpl implements Foo {

	private int counter = 0;

	@Override
	public int bar(int a) {
		// throw new RuntimeException();
		System.out.println("IN BAR 1!");
		return a + 10;
	}

	@Override
	public ArrayList<Integer> bar2(ArrayList<Integer> a) {

		ArrayList<Integer> b = new ArrayList<Integer>();
		for (int i = a.size() - 1; i >= 0; i--) {
			b.add(a.get(i));
		}
		return b;
		// throw new RuntimeException("Custom Exception");
		// System.out.println("IN BAR 2!");
		// return a+ 20;
	}

	@Override
	public Map<String, String> baz(int a, List<Double> b) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Hello", b.toString());
		return map;
	}

	@Override
	public void bars() {
		// TODO Auto-generated method stub
		System.out.println("YO BARZZZZ");
	}

	public String bar2(NonSerializable a) {
		// TODO Auto-generated method stub
		System.out.println("YO BARZZZZ");
		return a.toString();
	}

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

	@Override
	public int getCounter() {
		// TODO Auto-generated method stub
		return counter;
	}

	@Override
	public void increment() {
		// TODO Auto-generated method stub
		counter++;
	}

	@Override
	public void decrement() {
		// TODO Auto-generated method stub
		counter--;
	}
}
