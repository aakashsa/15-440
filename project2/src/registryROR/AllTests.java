package registryROR;

import java.util.ArrayList;

public class AllTests {
		
	public static void testNameNotBound() {
		System.out.println("\n\n[TEST] Testing exception for object name not bound...");
		Foo fooProxy = null;
		Object obj = null;
		try {
			obj = LocalizeObject.localize("foo3");
			fooProxy = (Foo) obj;
			fooProxy.increment();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("[TEST RESULT] Exception above" + "\n\n");
		
	}
	
	public static void testForArgumentNotSerializable() {
		System.out.println("\n\n[TEST] Testing exception for not serializable arguments...");
		Foo fooProxy = null;
		Object obj = null;
		try {
			obj = LocalizeObject.localize("foo1");
			fooProxy = (Foo) obj;
			fooProxy.bar2(new NonSerializable());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("[TEST RESULT] Exception above" + "\n\n");
	}
	
	public static void testForReturnTypeNotSerializable() {
		System.out.println("\n\n[TEST] Testing exception for not serializable return types...");
		Foo fooProxy = null;
		Object obj = null;
		try {
			obj = LocalizeObject.localize("foo1");
			fooProxy = (Foo) obj;
			fooProxy.returnObjectNotSerializable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("[TEST RESULT] Exception above" + "\n\n");
	}
	
	public static void testForRemoteArgument() {
		System.out.println("\n\n[TEST] Testing remote objects as arguments...");
		Foo fooProxy = null;
		RemoteBar rbarProxy = null;
		Object obj = null;
		
		try {
			obj = LocalizeObject.localize("foo1");
			fooProxy = (Foo) obj;
			obj = LocalizeObject.localize("rem1");
			rbarProxy = (RemoteBar) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ArrayList<Integer> ints = new ArrayList<Integer>();
		ints.add(1);
		ints.add(2);
		ints.add(4);
		ints.add(8);
		//ints.clear();
		System.out.println("[TEST RESULT] " + fooProxy.modifyByRemoteObj(ints, rbarProxy) + "\n\n");
	}
	
	public static void testForModifyingRemoteObjectField() {
		System.out.println("\n\n[TEST] Testing modifying remote object's fields...");
		Foo fooProxy = null;
		Object obj = null;
		
		try {
			obj = LocalizeObject.localize("foo1");
			fooProxy = (Foo) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		fooProxy.increment();
		fooProxy.increment();
		fooProxy.increment();
		System.out.println("[TEST RESULT] 3 ==  " + fooProxy.getCounter());
		fooProxy.decrement();
		fooProxy.decrement();
		System.out.println("[TEST RESULT] 1 ==  " + fooProxy.getCounter());
		fooProxy.increment();
		System.out.println("[TEST RESULT] 2 ==  " + fooProxy.getCounter() + "\n\n");
	}
	
	public static void testForRenewingArgumentRemoteObject() {
		System.out.println("\n\n[TEST] Testing renewing remote object passed as argument...");
		Foo fooProxy = null;
		Object obj = null;
		RemoteBar rbarProxy = null;

		try {
			obj = LocalizeObject.localize("foo1");
			fooProxy = (Foo) obj;
			obj = LocalizeObject.localize("rem1");
			rbarProxy = (RemoteBar) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		rbarProxy.changeState();
		int remState = rbarProxy.getState();
		System.out.println("[TEST RESULT] <randomNum> == " + remState);
		
		Foo fooProxy2 = fooProxy.renewRemoteArgument(rbarProxy);
		int newState = rbarProxy.getState();
		System.out.println("[TEST RESULT] 0 == " + newState + "\n\n");	
	}
	
	public static void testForReturnArgumentRemoteObject() {
		System.out.println("\n\n[TEST] Testing renewing remote object passed as argument...");
		Foo fooProxy = null;
		Object obj = null;
		RemoteBar rbarProxy = null;
		
		try {
			obj = LocalizeObject.localize("foo1");
			fooProxy = (Foo) obj;
			obj = LocalizeObject.localize("rem1");
			rbarProxy = (RemoteBar) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		RemoteBar rbarProxy2 = fooProxy.returnRemoteArgument(rbarProxy);
		System.out.println("[TEST RESULT] " + rbarProxy2.getState() + " == " + rbarProxy.getState() + "\n\n");
	}
	
	public static void testGetAllRMIRegistryObjects() {
		System.out.println("\n\n[TEST] Testing get all RMI Registry objects...");
		try {
			System.out.println("[TEST RESULT] " + RMIRegistry440.getAllObjects() + "\n\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		// Run a bunch of tests on the RMI library
//		testNameNotBound();
//		testForArgumentNotSerializable();
//		testForReturnTypeNotSerializable();
//		testForRemoteArgument();
//		testForModifyingRemoteObjectField();
//		testGetAllRMIRegistryObjects();
		testForRenewingArgumentRemoteObject(); // NOT WORKING CURRENTLY
//		testForReturnArgumentRemoteObject(); // NOT WORKING CURRENTLY
	}
}
