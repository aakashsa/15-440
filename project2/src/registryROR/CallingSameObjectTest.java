package registryROR;

public class CallingSameObjectTest {

	public static void main(String[] args) {
		System.out.println("\n\n[TEST] Testing calling same object at the same time...");
		
		Thread t1 = new Thread(new Tester(1));
		Thread t2 = new Thread(new Tester(2));
		t1.start();
		t2.start();
		
		System.out.println("[TEST RESULT] The print statements on server side should not be interleaving.");
	}

	static class Tester implements Runnable {

		private int number = 0;
		public Tester(int number) {
			this.number = number;
		}
		
		@Override
		public void run() {
			Foo fooProxy = null;
			Object obj = null;
			
			try {
				obj = LocalizeObject.localize("foo1");
				fooProxy = (Foo) obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			fooProxy.increment();
			System.out.println("[TEST RESULT " + number + "] " + fooProxy.getCounter());
			fooProxy.increment();
			System.out.println("[TEST RESULT " + number + "] " + fooProxy.getCounter());
			fooProxy.increment();
			System.out.println("[TEST RESULT " + number + "] " + fooProxy.getCounter());
		}
	}
}
