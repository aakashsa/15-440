package registryROR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.rmi.RemoteException;

import marshal.MessageInvokeFunction;

public class ProxyHandler implements InvocationHandler {

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

		MessageInvokeFunction marshal = new MessageInvokeFunction(
				method.getName(), args, method.getParameterTypes(), null, null,
				0);
		System.out.println("Serializing");
		String filePath = "Marshaltest" + ".dat";

		File processFile = new File(filePath);
		if (!processFile.exists()) {
			processFile.createNewFile();
		}

		FileOutputStream f_out = new FileOutputStream(filePath, false);
		ObjectOutputStream oos = new ObjectOutputStream(f_out);
		oos.writeObject((Object) marshal);
		oos.flush();
		oos.close();

		FileInputStream f_in = new FileInputStream(filePath);
		ObjectInputStream oin = new ObjectInputStream(f_in);
		MessageInvokeFunction marshal1 = (MessageInvokeFunction) oin
				.readObject();
		oin.close();

		System.out.println("UnSerializing");
		System.out.println("Unmarshalling");

		Object returning = null;
		try {
			System.out.println("Return type "
					+ impl.getClass()
							.getDeclaredMethod(marshal1.getFunctionName(),
									marshal1.getTypes()).getReturnType());

			returning = impl
					.getClass()
					.getDeclaredMethod(marshal1.getFunctionName(),
							marshal1.getTypes())
					.invoke(impl, marshal1.getArgs());

		} catch (Exception e) {
			// e.printStackTrace();
			MessageInvokeFunction marshal2 = new MessageInvokeFunction(
					marshal1.getFunctionName(), marshal1.getArgs(),
					marshal1.getTypes(), null, e, 0);
			System.out.println(marshal2.getExp().toString());
			throw new RemoteException();
		}
		System.out.printf("Returning %s\n\n", returning);
		return 10;
	}
}