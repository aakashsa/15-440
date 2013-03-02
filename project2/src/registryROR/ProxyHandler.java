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
			throws RemoteException440 {
		System.out.println("[INFO] Calling method " + method.getName());
		
		// Check if all arguments are remote or serializable, else throw exception.
		Type[] params = method.getGenericParameterTypes();
		for (int i = 0; i < params.length; i++) {
			if (!(Serializable.class.isAssignableFrom(args[i].getClass())) &&
					!(Remote440.class.isAssignableFrom(args[i].getClass()))) {
				throw new RemoteException440("[ERROR] Object is neither remote nor serializable!");
			}
		}

		// Marshal request and send it over to server
		System.out.println("[INFO] Marshalling request from ror object " + ror.getObjectName() + "...");
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Create the invoke message object
		MessageInvokeFunction request = new MessageInvokeFunction(
				method.getName(), args, method.getParameterTypes(), null, null,
				0, ror.getObjectName(), method.getReturnType());
		MessageInvokeFunction reply = null;

		System.out.println(" Request Func Name  - " + request.getFunctionName());
		System.out.println(" Request Objec Name - " + request.getObjName());
		System.out.println(" Request Ret Val    - " + request.getRetVal());
		System.out.println(" Request Exception  - " + request.getExp());
		
		// Block on reading a response back from remote server
		try {
			out.writeObject(request);
			reply = (MessageInvokeFunction) in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (reply == null) {
			System.out.println("[ERROR] Reply is null!");
		}
		
		System.out.println(" Reply Func Name  - " + reply.getFunctionName());
		System.out.println(" Reply Objec Name - " + reply.getObjName());
		System.out.println(" Reply Ret Val    - " + reply.getRetVal());
		System.out.println(" Reply Exception  - " + reply.getExp());
				
		// Return exception if remote method threw an exception,
		// else return the return value
		if (reply.getExp() != null) {
			System.out.println("[ERROR] Exception = " + reply.getExp().toString());
			throw new RemoteException440(reply.getExp().getLocalizedMessage());
		} else {
			System.out.printf("[INFO] Returning %s\n\n", reply.getRetVal());
			return reply.getRetVal();
		}
	}
}