package rmi440.commoncode;

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
	private Request req;
	private Exception e;
	private Object retVal;
	
	public enum Request {REBIND, LOOKUP, ALLOBJECTS};
	
	public RMIRegistryMessage(String name, Remote440 r, Request req, Exception e, Object retVal) {
		this.name = name;
		this.remote = r;
		this.req = req;
		this.e = e;
		this.retVal = retVal;
	}

	public boolean isLookupMessage() {
		return req.equals(Request.LOOKUP);
	}
	
	public boolean isRebindMessage() {
		return req.equals(Request.REBIND);
	}
	
	public boolean isAllObjectsMessage() {
		return req.equals(Request.ALLOBJECTS);
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
	
	public Object getRetVal() {
		return retVal;
	}
}
