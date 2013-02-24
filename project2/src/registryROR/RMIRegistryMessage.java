package registryROR;

import java.io.Serializable;

public class RMIRegistryMessage implements Serializable {
	
	private static final long serialVersionUID = -2490542686352505172L;
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
	
	public String getName() {
		return name;
	}
	
	public Remote440 getRemoteRef() {
		return remote;
	}
	
	/**
	 * returns true if the message is a lookup message
	 * Flag is true for lookup, and false for rebind
	 * @return
	 */
	public boolean isLookupMessage() {
		return bindOrLookup;
	}
	
	public Exception getException() {
		return e;
	}
}
