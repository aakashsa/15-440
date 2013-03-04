package rmi440.tests.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rmi440.commoncode.Remote440;

/**
 * A sample remote interface for testing purposes
 */
public interface Foo extends Remote440 {

	int getCounter();
	
	void increment();
	
	void decrement();
		
	Map<String, String> addStringifiedListToMap(List<Double> b);
	
	String notSerializableArgument(NonSerializable a);
	
	ArrayList<Integer> reverseList(ArrayList<Integer> a);
	
	ArrayList<Integer> modifyByRemoteObj(ArrayList<Integer> a, RemoteBar b);
	
	RemoteBar renewRemoteArgument(RemoteBar rem);
	
	RemoteBar returnRemoteArgument(RemoteBar r);
	
	NonSerializable returnObjectNotSerializable();
}
