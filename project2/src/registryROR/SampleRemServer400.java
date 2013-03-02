package registryROR;

/**
 * A sample server for testing purposes
 */
public class SampleRemServer400 {

	public static void main(String[] args) {
		String name1 = "rem1";
		RemoteBar rem1 = new RemoteBarImpl();
		
		String name2 = "rem2";
		RemoteBar rem2 = new RemoteBarImpl();
		
		Binder.bindObject(name1, "registryROR.RemoteBar", rem1);
		Binder.bindObject(name2, "registryROR.RemoteBar", rem2);
	}
}
