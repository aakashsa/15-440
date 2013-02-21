package registryROR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import marshal.MessageInvokeFunction;

public class ProxyHandler implements InvocationHandler, Serializable {

	private RemoteObjectRef ror;

	public ProxyHandler(RemoteObjectRef r) {
		// TODO Auto-generated constructor stub
		this.ror = r;
	}

	@Override
	public synchronized Object invoke(Object proxy, Method method, Object[] args)
			throws RemoteException {
		Foo impl = new FooImpl();
		System.out.printf("Someone called method %s with arguments\n",
				method.getName());
		Type[] params = method.getGenericParameterTypes();
//		for (int i = 0; i < params.length; i++) {
//			System.out.printf("%d: %s = %s\n", i, params[i], args[i]);
//		}

		System.out.println("Marshalling");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (reply.getExp() != null) {
			System.out
					.println("Exception = " + reply.getExp().toString() + " ");
			throw new RemoteException();
		} else {
			System.out.printf("Returning %s\n\n", reply.getRetVal());
			return reply.getRetVal();
		}
	}
}