package registryROR;

import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Foo extends Remote{
	int bar(int a);
	int barRem(RemoteBar a,int b);
	
	ArrayList<Integer> bar2(ArrayList<Integer> a); 	
	void bars();
	Map<String, String> baz(int a, List<Double> b);
}