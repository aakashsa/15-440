package registryROR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FooImpl implements Foo {

	@Override
	public int bar() {
		//throw new RuntimeException();
		System.out.println("IN BAR !");
		return 10;
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

}
