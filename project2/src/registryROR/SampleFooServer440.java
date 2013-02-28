package registryROR;

/**
 * A sample server for testing purposes
 */
public class SampleFooServer440 {

	public static void main(String[] args) {
		String name = "foo1";
		Foo fooSample = new FooImpl();
		Binder.bindObject(name, "registryROR.Foo", fooSample);
	}
}
