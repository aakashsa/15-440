import java.rmi.*;
import java.rmi.server.*;

interface HelloInterface extends Remote {
	public String sayHello(String name) throws RemoteException;
}
