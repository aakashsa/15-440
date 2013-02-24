package registryROR;

import java.io.Serializable;

public class RemoteObjectRef  implements Serializable,Remote440{

	private static final long serialVersionUID = 1L;
	String IP_adr;
	int Port;
	int Obj_Key;
	String Remote_Interface_Name;
	String objName;

	public RemoteObjectRef(String ip, int port, int obj_key, String riname,String objectName) {
		IP_adr = ip;
		Port = port;
		Obj_Key = obj_key;
		Remote_Interface_Name = riname;
		objName = objectName;		
	}

	public String getInterfaceName1() {
		return Remote_Interface_Name;
	}
	
	public String getObjectName() {
		return objName;
	}
	
	public String getIp() {
		return IP_adr;
	}

	public int getObjKey() {
		return Obj_Key;
	}

	public int getPort() {
		return Port;
	}
}
