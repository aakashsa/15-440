package rmi440.tests.server;

import java.util.ArrayList;
import java.util.Random;

import rmi440.tests.common.RemoteBar;


/**
 * A sample remote object for testing purposes
 */
public class RemoteBarImpl implements RemoteBar {

	private int state = 0;

	/**
	 * A function that returns a random integer between 0 (inclusive)
	 * and the size of a given array (exclusive)
	 */
	public int getRandomSize(ArrayList<Integer> a) {
		if (a.size() == 0)
			throw new IllegalArgumentException("[ERROR] Size of array list is 0");
		Random i = new Random();
		return i.nextInt(a.size());
	}	
	
	/**
	 * A function that returns a new instance of this remote object
	 */
	public RemoteBar renew() {
		return new RemoteBarImpl();
	}
	
	/**
	 * A function that changes an inner state of this object
	 */
	public void changeState() {
		Random r = new Random();
		state = r.nextInt(50);
		if (state == 0) state = 25;
	}
	
	/**
	 * Getter for state of this object
	 */
	public int getState() {
		return state;
	}
	
}
