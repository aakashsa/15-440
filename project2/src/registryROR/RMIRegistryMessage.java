package registryROR;

import java.io.Serializable;

/**
 * A class that encapsulates the elements of a message to the RMI
 * registry on a remote server.
 */
public class RMIRegistryMessage implements Serializable {
	
	private static final long serialVersionUID = -2490542686352505172L;
	
	// Contains the name of object, remote object to bind,
	// whether the operation is bind or lookup, and an exception
	private String name;
	private Remote440 remote;
	private boolean bindOrLookup;
	private Exception e;
	
	public RMIRegistryMessage(String name, Remote440 r, Boolean flag, Exception e) {
		this.name = name;
		this.remote = r;
		this.bindOrLookup = flag;
		this.e = e;
	}

	/**
	 * returns true if the message is a lookup message
	 * Flag is true for lookup, and false for rebind
	 */
	public boolean isLookupMessage() {
		return bindOrLookup;
	}
	
	// Getters for attributes
	
	public String getName() {
		return name;
	}
	
	public Remote440 getRemoteRef() {
		return remote;
	}
		
	public Exception getException() {
		return e;
	}
}
