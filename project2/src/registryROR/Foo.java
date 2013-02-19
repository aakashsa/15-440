package registryROR;

import java.util.List;
import java.util.Map;

public interface Foo extends Remote440{
	int bar();
	void bars();
	Map<String, String> baz(int a, List<Double> b);
}