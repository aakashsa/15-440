package registryROR;

/**
 * A sample remote object for testing purposes
 */
public class RemoteBarImpl implements RemoteBar {

	@Override
	public int getbar() {
		System.out.println("[INFO] Called called getbar in RemoteBar object");
		return 10;
	}

	@Override
	public int barRem(int b) {
		return 0;
	}
	
}
