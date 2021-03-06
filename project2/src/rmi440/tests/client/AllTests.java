package rmi440.tests.client;

import java.util.ArrayList;

import rmi440.commoncode.LocalizeObject;
import rmi440.commoncode.RMIRegistry440;
import rmi440.tests.common.Foo;
import rmi440.tests.common.NonSerializable;
import rmi440.tests.common.RemoteBar;

/**
 * This class contains all tests for the library
 */
public class AllTests {
		
	/**
	 * Test for an exception when the name is not bound in registry
	 */
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
	
	/**
	 * Test to check if all arguments are serializable
	 */
	public static void testForArgumentNotSerializable() {
		System.out.println("\n\n[TEST] Testing exception for not serializable arguments...");
		Foo fooProxy = null;
		Object obj = null;
		try {
			obj = LocalizeObject.localize("foo1");
			fooProxy = (Foo) obj;
			fooProxy.notSerializableArgument(new NonSerializable());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("[TEST RESULT] Exception above" + "\n\n");
	}
	
	/**
	 * Test for a normal method call that returns a reversed array list
	 */
	public static void testForReverseList() {
		System.out.println("\n\n[TEST] Testing reverse list...");
		Foo fooProxy = null;
		Object obj = null;
		try {
			obj = LocalizeObject.localize("foo1");
			fooProxy = (Foo) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<Integer> original = new ArrayList<Integer>();
		original.add(1);
		original.add(2);
		original.add(3);
		
		System.out.println("[TEST RESULT] " + fooProxy.reverseList(original));
	}
	
	/**
	 * A normal method that tests adding something to a more complicated data
	 * structure like a map
	 */
	public static void testAddToMap() {
		System.out.println("\n\n[TEST] Testing add to map...");
		Foo fooProxy = null;
		Object obj = null;
		try {
			obj = LocalizeObject.localize("foo1");
			fooProxy = (Foo) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ArrayList<Double> d = new ArrayList<Double>();
		d.add(1.224523523);
		d.add(0.0);
		
		System.out.println("[TEST RESULT] " + fooProxy.addStringifiedListToMap(d));
	}
	
	/**
	 * Test for cases when the return object is not serializable.
	 */
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
	
	
	
	/**
	 * Test for passing a remote object, that modifies something, and return
	 * a result that is not remote
	 */
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

		System.out.println("[TEST RESULT] " + fooProxy.modifyByRemoteObj(ints, rbarProxy) + "\n\n");
	}
	
	/**
	 * Test for modifying a remote objects field and checking if it does that
	 */
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
	
	/**
	 * Test to check if the get all objects function is working
	 */
	public static void testGetAllRMIRegistryObjects() {
		System.out.println("\n\n[TEST] Testing get all RMI Registry objects...");
		try {
			System.out.println("[TEST RESULT] " + RMIRegistry440.getAllObjects() + "\n\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test for returning a new remote object that was not bound by the server
	 * initially
	 */
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
		
		RemoteBar rbarProxy2 = fooProxy.renewRemoteArgument(rbarProxy);
		int newState = rbarProxy2.getState();
		System.out.println("[TEST RESULT] 0 == " + newState + "\n\n");	
	}
	
	/**
	 * Test for returning the same, possibly modified, instance of remote object
	 * as passed in as argument
	 */
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
	
	public static void main(String[] args) {
		
		// Run a bunch of tests on the RMI library
		testNameNotBound();
		testForArgumentNotSerializable();
		testForReturnTypeNotSerializable();
		testForReverseList(); // Throws not serializable exception on server side
		testAddToMap();       // Throws not serializable exception on server side
		testForRemoteArgument();
		testForModifyingRemoteObjectField();
		testGetAllRMIRegistryObjects();
		testForRenewingArgumentRemoteObject();
		testForReturnArgumentRemoteObject();
	}
}
