package nodework;

/**
 * Gets in the port it should listen on from args, and starts listening for task
 * requests on that port. When a request comes in, it checks if it's a map
 * request or a reduce request, and carries it out accordingly.
 */
public class Worker {

	public static void main(String[] args) {
		// Get ports to listen on from command line
		if (args.length != 1) {
			System.out.println("Usage: Worker <port1>,<port2>, ... ,<port n>");
			System.exit(-1);
		}
		String[] portStrings = null;
		try {
			portStrings = args[0].split(",");
		} catch (Exception e) {
			System.out.println("Usage: Worker <port1>,<port2>, ... ,<port n>");
			System.exit(-1);
		}
		int[] ports = new int[portStrings.length];
		
		int port;
		for (int i = 0; i < portStrings.length; i++) {
			port = 0;
			try {
				port = Integer.parseInt(portStrings[i]);
			} catch (Exception e) {
				System.out.println("Ports must be an integer. Actual: " + portStrings[i]);
				System.exit(-1);
			}
			if (port < 1024 || port > 49151) {
				System.out.println("Port number must be >= 1024 and <= 49151 (Registered port numbers range)");
				System.exit(-1);
			}
			ports[i] = port;
		}
		
		// Spawn a thread to listen for requests on each port
		for (int p : ports) {
			new Thread(new WorkerThread(p)).start();
		}
	}
}
