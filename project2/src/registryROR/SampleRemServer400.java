package registryROR;

/**
 * A sample server for testing purposes
 */
public class SampleRemServer400 {

	public static void main(String[] args) {
		String name = "Rem";
		RemoteBar remSample = new RemoteBarImpl();
		Binder.bindObject(name, "registryROR.RemoteBar", remSample);
	}
}
