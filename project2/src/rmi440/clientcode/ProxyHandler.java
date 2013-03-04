package rmi440.clientcode;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;

import rmi440.commoncode.MessageInvokeFunction;
import rmi440.commoncode.Remote440;
import rmi440.commoncode.RemoteException440;
import rmi440.commoncode.RemoteObjectRef;

/**
 * The invocation handler class that takes care of marhsalling method
 * invocations and sending them over to the remote server so the request
 * can be carried out.
 */
public class ProxyHandler implements InvocationHandler, Serializable {

	private static final long serialVersionUID = 1L;
	private RemoteObjectRef ror;

	public ProxyHandler(RemoteObjectRef r) {
		this.ror = r;
	}

	/**
	 * The main invoke method. This is called everything a method is invoked on
	 * and object that is bound to this proxy handler. It does the main chunk
	 * of the work of marshalling the request and unmarshalling the response.
	 */
	@Override
	public synchronized Object invoke(Object proxy, Method method, Object[] args)
			throws RemoteException440 {
		System.out.println("[INFO] Calling method " + method.getName());
		
		// Check if all arguments are remote or serializable, else throw exception.
		Type[] params = method.getGenericParameterTypes();
		for (int i = 0; i < params.length; i++) {
			if (!(Serializable.class.isAssignableFrom(args[i].getClass())) &&
					!(Remote440.class.isAssignableFrom(args[i].getClass()))) {
				throw new RemoteException440("[ERROR] An argument is neither remote nor serializable!");
			}
		}

		// Marshal request and send it over to server
		OutputStream output = null;
		InputStream input = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Socket clientSocket = null;

		try {
			// Connect to the ROR's proxy running on server side
			clientSocket = new Socket(ror.getIp(), ror.getPort());
			output = clientSocket.getOutputStream();
			input = clientSocket.getInputStream();
			out = new ObjectOutputStream(output);
			out.flush();
			in = new ObjectInputStream(input);
		} catch (UnknownHostException e) {
			System.out.println("[ERROR] Proxy handler couldn't connect to IP: " + ror.getIp() + ", port: " + ror.getPort());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[ERROR] Error in initializing streams to RORs server thread");
			e.printStackTrace();
		}
		
		// Create the invoke message object
		MessageInvokeFunction request = new MessageInvokeFunction(
				method.getName(), args, method.getParameterTypes(), null, null,
				ror.getObjectName(), method.getReturnType());
		MessageInvokeFunction reply = null;
		
		// Block on reading a response back from remote server
		try {
			out.writeObject(request);
			out.flush();
			reply = (MessageInvokeFunction) in.readObject();
		} catch (IOException e) {
			System.out.println("[ERROR] Error in reading/writing from RORs stream");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("[ERROR] Error in reading input from RORs server thread. Bad cast.");
			e.printStackTrace();
		}

		// Return exception if remote method threw an exception,
		// else return the return value
		if (reply.getExp() != null) {
			throw new RemoteException440(reply.getExp().getLocalizedMessage());
		} else {
			return reply.getRetVal();
		}
	}
}