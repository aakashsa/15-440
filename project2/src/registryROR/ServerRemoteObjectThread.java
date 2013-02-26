package registryROR;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import marshal.MessageInvokeFunction;

public class ServerRemoteObjectThread implements Runnable {

	private Object implementation;
	private ConcurrentHashMap<String, Object> remoteObjectsMap;
	private ServerSocket serverSocket;

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
				System.out.println(" Server Proxy Marshal m ObjectKey"
						+ marshalm.getObjectKey());
				System.out.println(" Server Proxy Marshal m  Objec Name "
						+ marshalm.getObjName());
				System.out.println(" Server Proxy Marshal m Ret Val"
						+ marshalm.getRetVal());
				System.out.println(" Server Proxy Marshal m Exception"
						+ marshalm.getExp());

				MessageInvokeFunction marshal2;
				try {
					// System.out.println("Invoking Function on Server");
					// System.out.println("Object = " +
					// implementation.toString());
					Object returning = implementation
							.getClass()
							.getDeclaredMethod(marshalm.getFunctionName(),
									marshalm.getTypes())
							.invoke(implementation, marshalm.getArgs());
					System.out.println("Returning Message on Proxy on Server");
					marshal2 = new MessageInvokeFunction(
							marshalm.getFunctionName(), marshalm.getArgs(),
							marshalm.getTypes(), returning, null,
							marshalm.getObjectKey(), marshalm.getObjName());
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Exception on Server !!");
					// e.printStackTrace();
					marshal2 = new MessageInvokeFunction(
							marshalm.getFunctionName(), marshalm.getArgs(),
							marshalm.getTypes(), null, e,
							marshalm.getObjectKey(), marshalm.getObjName());
					// System.out.println(marshal2.getExp().toString());
					// throw new RemoteException();
				}
				System.out.println("Returning Message on Proxy on Server");
				out.writeObject(marshal2);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
