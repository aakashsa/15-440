package registryROR;

import java.rmi.Remote;
import java.util.List;
import java.util.Map;

public interface Foo extends Remote{
	int bar(); 	
	void bars();
	Map<String, String> baz(int a, List<Double> b);
}