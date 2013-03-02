package registryROR;

import java.util.ArrayList;
import java.util.Random;

/**
 * A sample remote object for testing purposes
 */
public class RemoteBarImpl implements RemoteBar {

	private int state = 0;
	
	@Override
	public int getbar() {
		System.out.println("[INFO] Called called getbar in RemoteBar object");
		return 10;
	}

	@Override
	public int barRem(int b) {
		return 0;
	}
	
	public int getRandomSize(ArrayList<Integer> a) {
		if (a.size() == 0)
			throw new IllegalArgumentException("[ERROR] Size of array list is 0");
		Random i = new Random();
		return i.nextInt(a.size());
	}	
	
	public RemoteBar renew() {
		return new RemoteBarImpl();
	}
	
	public void changeState() {
		Random r = new Random();
		state = r.nextInt(50);
		if (state == 0) state = 25;
	}
	
	public int getState() {
		return state;
	}
	
}
