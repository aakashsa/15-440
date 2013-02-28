package registryROR;

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

import java.rmi.RemoteException;

import marshal.MessageInvokeFunction;

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
			throws RemoteException {
		System.out.printf("Someone called method %s with arguments\n", method.getName());
		
		// Check if all arguments are remote or serializable, else throw exception.
		Type[] params = method.getGenericParameterTypes();
		for (int i = 0; i < params.length; i++) {
			if ((Serializable.class.isAssignableFrom(args[i].getClass()))) {
				System.out.println("Serializing Object");
			} else if ((Remote440.class.isAssignableFrom(args[i].getClass()))) {
				System.out.println("Remote Object");
			}
			else
				throw new RemoteException("[ERROR]: Object is neither remote nor serializable!");
		}

		// Marshal request and send it over to server
		System.out.println("[INFO] Marshalling...");
		OutputStream output = null;
		InputStream input = null;
		ObjectOutputStream out = null; // Slave output stream
		ObjectInputStream in = null; // Slave input stream
		Socket clientSocket = null; // Client socket

		try {
			clientSocket = new Socket(ror.getIp(), ror.getPort());
			output = clientSocket.getOutputStream();
			input = clientSocket.getInputStream();
			out = new ObjectOutputStream(output);
			out.flush();
			in = new ObjectInputStream(input);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Ror.getObject Name in Proxy handler = "
				+ ror.getObjectName());
		// (String funName, Object[] args, Class[] types,
		// Object returnVal, Exception exp, int objectKey,String objName)
		MessageInvokeFunction marshal = new MessageInvokeFunction(
				method.getName(), args, method.getParameterTypes(), null, null,
				0, ror.getObjectName());
		MessageInvokeFunction reply = null;

		System.out.println(" Marshal  " + marshal.toString());
		System.out.println(" Marshal  Func Name " + marshal.getFunctionName());
		System.out.println(" Marshal  ObjectKey" + marshal.getObjectKey());
		System.out.println(" Marshal  Objec Name " + marshal.getObjName());
		System.out.println(" Marshal  Ret Val" + marshal.getRetVal());
		System.out.println(" Marshal  Exception" + marshal.getExp());

		try {
			out.writeObject(marshal);
			reply = (MessageInvokeFunction) in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(" Reply  " + reply.toString());
		System.out.println(" Reply  Func Name " + reply.getFunctionName());
		System.out.println(" Reply  ObjectKey" + reply.getObjectKey());
		System.out.println(" Reply  Objec Name " + reply.getObjName());
		System.out.println(" Reply  Ret Val" + reply.getRetVal());
		System.out.println(" Reply  Exception" + reply.getExp());
		if (reply.getExp() != null) {
			System.out.println("Exception = " + reply.getExp().toString() + " ");
			throw new RemoteException();
		} else {
			System.out.printf("Returning %s\n\n", reply.getRetVal());
			return reply.getRetVal();
		}
	}
}