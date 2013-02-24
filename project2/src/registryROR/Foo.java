package registryROR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Foo extends Remote440 {
	int bar(int a);

	int barRem(RemoteBar a, int b);

	ArrayList<Integer> bar2(ArrayList<Integer> a);

	void bars();
	
	public String bar2(NonSerializable a);

	Map<String, String> baz(int a, List<Double> b);
}