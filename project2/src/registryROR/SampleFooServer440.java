package registryROR;

public class SampleFooServer440 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String name = "foo1";
		Foo fooSample = new FooImpl();
		Binder.bindObject(name, "registryROR.Foo", fooSample);
	}
}
