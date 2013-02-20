package registryROR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Socket;
import java.rmi.RemoteException;

import marshal.MessageInvokeFunction;

public class ProxyHandler implements InvocationHandler {

	private RemoteObjectRef ror;

	public ProxyHandler(RemoteObjectRef r) {
		// TODO Auto-generated constructor stub
		this.ror = r;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Foo impl = new FooImpl();
		System.out.printf("Someone called method %s with arguments\n",
				method.getName());
		Type[] params = method.getGenericParameterTypes();
		for (int i = 0; i < params.length; i++) {
			System.out.printf("%d: %s = %s\n", i, params[i], args[i]);
		}
		System.out.println("Marshalling");
		OutputStream output = null;
		InputStream input = null;
		ObjectOutputStream out = null; // Slave output stream
		ObjectInputStream in = null; // Slave input stream
		Socket clientSocket = null; // Client socket

		clientSocket = new Socket("localhost", 4023);
		output = clientSocket.getOutputStream();
		input = clientSocket.getInputStream();
		out = new ObjectOutputStream(output);
		out.flush();
		in = new ObjectInputStream(input);

		MessageInvokeFunction marshal = new MessageInvokeFunction(
				method.getName(), args, method.getParameterTypes(), null, null,
				0, ror.getObjectName());
		out.writeObject(marshal);

		MessageInvokeFunction reply = (MessageInvokeFunction) in.readObject();

		System.out.printf("Returning %s\n\n", reply.getRetVal());
		return 10;
	}
}