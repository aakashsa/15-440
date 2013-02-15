package nameServer;

import registryROR.*;

public interface NameServer // extends YourRemote
{
	public RemoteObjectRef match(String name);

	public NameServer add(String s, RemoteObjectRef r, NameServer n);

	public NameServer next();

}
