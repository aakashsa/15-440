package registryROR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FooImpl implements Foo {

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
		return  a.toString();
	}

	@Override
	public int barRem(RemoteBar a, int b) {
		// TODO Auto-generated method stub
		System.out.println(" barRem in Foo Impl");
		return 100 + a.getbar() + b;
	}

}
