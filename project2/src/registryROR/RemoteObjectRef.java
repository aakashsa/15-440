package registryROR;

import java.io.Serializable;

/**
 * This class encapsulates information about a remote object
 * and is stored in the registry. When an application requests
 * a remote object, they get the remote object reference for that
 * remote object
 */
public class RemoteObjectRef implements Serializable, Remote440 {
	
	private static final long serialVersionUID = 1L;
	
	// Store host, port, interface name, and object name (the name
	// the object is bound to in registry)
	String IP_adr;
	int Port;
	String Remote_Interface_Name;
	String objName;

	public RemoteObjectRef(String ip, int port, String riname, String objectName) {
		this.IP_adr = ip;
		this.Port = port;
		this.Remote_Interface_Name = riname;
		this.objName = objectName;
	}

	// Getters for all of the fields
	
	public String getInterfaceName1() {
		return Remote_Interface_Name;
	}

	public String getObjectName() {
		return objName;
	}

	public String getIp() {
		return IP_adr;
	}

	public int getPort() {
		return Port;
	}
	
	@Override
	public String toString() {
		return "<" + Remote_Interface_Name + "," + IP_adr + "," + Port + ">";
	}
}
