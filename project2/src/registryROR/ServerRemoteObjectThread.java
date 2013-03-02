package registryROR;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import marshal.MessageInvokeFunction;

public class ServerRemoteObjectThread implements Runnable {

	private Object implementation;
	private ServerSocket serverSocket;
	private long numBinded = 0;

	public ServerRemoteObjectThread(Object implementation,
			ServerSocket serverSocket) {
		// TODO Auto-generated constructor stub
		this.serverSocket = serverSocket;
		this.implementation = implementation;
	}

	@Override
	public void run() {
		Socket clientSocket = null;
		while (true) {
			// Accept connections and initialize client streams
			try {
				System.out.println("Accepting Connections in ServerProxy");
				clientSocket = serverSocket.accept();
				OutputStream output = clientSocket.getOutputStream();
				InputStream input = clientSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				ObjectInputStream in = new ObjectInputStream(input);
				System.out.println("Read Request");
				MessageInvokeFunction marshalm = (MessageInvokeFunction) in
						.readObject();
				System.out.println(" Server Proxy Marshal m "
						+ marshalm.toString());
				System.out.println(" Server Proxy Marshal m Func Name "
						+ marshalm.getFunctionName());
				System.out.println(" Server Proxy Marshal m  Objec Name "
						+ marshalm.getObjName());
				System.out.println(" Server Proxy Marshal m Ret Val"
						+ marshalm.getRetVal());
				System.out.println(" Server Proxy Marshal m Exception"
						+ marshalm.getExp());

				MessageInvokeFunction response;
				try {
					Object returning = implementation
							.getClass()
							.getDeclaredMethod(marshalm.getFunctionName(),
									marshalm.getTypes())
							.invoke(implementation, marshalm.getArgs());
					
					// Check if return type is remote or serializable
					if (returning != null) {
						if (!(Serializable.class.isAssignableFrom(returning.getClass())) &&
							!(Remote440.class.isAssignableFrom(returning.getClass()))) {
							throw new RemoteException440("[ERROR] Return object is neither remote nor serializable!");
						}
						
						// Check if return type is a remote object, if yes, then pass back proxy
						if ((Remote440.class.isAssignableFrom(returning.getClass())) &&
								!(Serializable.class.isAssignableFrom(returning.getClass()))) {
							String name = marshalm.getObjName() + "server" + numBinded;
							String interfaceName = "registryROR." + marshalm.getReturnType().getSimpleName();
							//System.out.println("[INFO]========= Binding " + name + ", " + interfaceName);
							Binder.bindObject(name, interfaceName, returning);
							returning = LocalizeObject.localize(name);
							numBinded++;
						}
					}
					
					System.out.println("[INFO] Returning message on proxy on server");
					response = new MessageInvokeFunction(
							marshalm.getFunctionName(), marshalm.getArgs(),
							marshalm.getTypes(), returning, null,
							marshalm.getObjectKey(), marshalm.getObjName(), marshalm.getReturnType());
				} catch (Exception e) {
					System.out.println("[ERROR] Exception on Server. Printing stack trace...");
					e.printStackTrace();

					response = new MessageInvokeFunction(
							marshalm.getFunctionName(), marshalm.getArgs(),
							marshalm.getTypes(), null, e,
							marshalm.getObjectKey(), marshalm.getObjName(), null);
				}
				System.out.println("Returning Message on Proxy on Server");
				out.writeObject(response);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}			
		}
	}
}
