package registryROR;

public class RemoteObjectRef {
	String IP_adr;
	int Port;
	int Obj_Key;
	String Remote_Interface_Name;

	public RemoteObjectRef(String ip, int port, int obj_key, String riname) {
		IP_adr = ip;
		Port = port;
		Obj_Key = obj_key;
		Remote_Interface_Name = riname;
	}

	public String getInterfaceName1() {
		return Remote_Interface_Name;
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
