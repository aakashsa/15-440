package registryROR;

/**
 * A sample server for testing purposes
 */
public class SampleFooServer440 {

	public static void main(String[] args) {
		String name1 = "foo1";
		Foo foo1 = new FooImpl();
		
		String name2 = "foo2";
		Foo foo2 = new FooImpl();
		
		Binder.bindObject(name1, "registryROR.Foo", foo1);
		Binder.bindObject(name2, "registryROR.Foo", foo2);
	}
}
