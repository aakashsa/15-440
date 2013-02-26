package registryROR;

public class SampleRemServer400 {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		String name = "Rem";
		RemoteBar remSample = new RemoteBarImpl();
		Binder.bindObject(name, "registryROR.RemoteBar", remSample);
	}
}
