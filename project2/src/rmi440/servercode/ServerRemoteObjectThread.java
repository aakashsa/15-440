package rmi440.servercode;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import rmi440.commoncode.LocalizeObject;
import rmi440.commoncode.Remote440;
import rmi440.commoncode.RemoteException440;
import rmi440.commoncode.MessageInvokeFunction;

/**
 * This class represents the thread for each remote object. Its
 * constructor takes in the remote object this thred is running
 * for, reads in a method invocation request from the application,
 * calls the method on the remote object, and then passes the response
 * back to the application.
 */
public class ServerRemoteObjectThread implements Runnable {

	private Object implementation;
	private ServerSocket serverSocket;
	private long numBinded = 0;

	public ServerRemoteObjectThread(Object implementation,
			ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		this.implementation = implementation;
	}

	@Override
	public void run() {
		Socket clientSocket = null;
		while (true) {
			// Accept connections and initialize client streams
			try {
				clientSocket = serverSocket.accept();
				OutputStream output = clientSocket.getOutputStream();
				InputStream input = clientSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				ObjectInputStream in = new ObjectInputStream(input);

				// Read in request
				MessageInvokeFunction request = (MessageInvokeFunction) in.readObject();

				// Create response and send it
				MessageInvokeFunction response;
				try {
					Object returning = implementation
							.getClass()
							.getDeclaredMethod(request.getFunctionName(), request.getTypes())
							.invoke(implementation, request.getArgs());
					
					// Check if return type is remote or serializable
					if (returning != null) {
						if (!(Serializable.class.isAssignableFrom(returning.getClass())) &&
							!(Remote440.class.isAssignableFrom(returning.getClass()))) {
							throw new RemoteException440("[ERROR] Return object is neither remote nor serializable!");
						}
						
						// Check if return type is a remote object, if yes, then pass back proxy
						if ((Remote440.class.isAssignableFrom(returning.getClass())) &&
								!(Serializable.class.isAssignableFrom(returning.getClass()))) {
							// The object to return is a new instance, so add it in the registry
							String name = request.getObjName() + "server" + numBinded;
							String interfaceName = "rmi440.tests.common." + request.getReturnType().getSimpleName() ;
							System.out.println("[INFO] Binding " + name + ", " + interfaceName);
							Binder.bindObject(name, interfaceName, returning);
							returning = LocalizeObject.localize(name);
							numBinded++;
						}
					}
					
					response = new MessageInvokeFunction(
							request.getFunctionName(), request.getArgs(),
							request.getTypes(), returning, null,
							request.getObjName(), request.getReturnType());
				} catch (Exception e) {
					// remote object's method threw an exception, catch it and pass it back
					System.out.println("[ERROR] Exception on Server. Printing stack trace...");
					e.printStackTrace();

					response = new MessageInvokeFunction(
							request.getFunctionName(), request.getArgs(),
							request.getTypes(), null, e,
							request.getObjName(), null);
				}
				out.writeObject(response);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
