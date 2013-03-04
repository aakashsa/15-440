package rmi440.commoncode;

/**
 * The remote exception type. If there is a remote exception,
 * this exception is thrown
 */
public class RemoteException440 extends Exception {

	private static final long serialVersionUID = 1L;

	// Parameterless constructor
	public RemoteException440() {
		
	}
	
	// Constructor with a message
	public RemoteException440(String message) {
		super(message);
	}

}
