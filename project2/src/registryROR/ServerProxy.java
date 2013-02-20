package registryROR;

import java.io.*;
import java.net.*;
import java.rmi.RemoteException;

import marshal.MessageInvokeFunction;

public class ServerProxy implements Runnable {

	private int port;
	private String localHost;

	public ServerProxy(int port, String localHost) {
		// TODO Auto-generated constructor stub
		this.port = port;
		this.localHost = localHost;
	}

	@Override
	public void run() {

		ServerSocket serverSocket = null;
		Socket clientSocket = null;

		try {
			// Create a new server socket
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("ERROR: Could not listen on port: " + port);
			System.exit(0);
		}

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
				MessageInvokeFunction marshal2;
				try {
//					System.out.println(" Function Invoked = "
//							+ SampleServer440.fooSample
//									.getClass()
//									.getDeclaredMethod(
//											marshalm.getFunctionName(),
//											marshalm.getTypes())
//									.invoke(SampleServer440.fooSample,
//											marshalm.getArgs()));

					// + FooImpl.getClass()
					// .getDeclaredMethod(marshal1.getFunctionName(),
					// marshal1.getTypes()).getReturnType());
					System.out.println("Invoking Function on Server");
					Object returning = SampleServer440.fooSample
							.getClass()
							.getDeclaredMethod(marshalm.getFunctionName(),
									marshalm.getTypes())
							.invoke(SampleServer440.fooSample,
									marshalm.getArgs());
					System.out.println("Returning Message on Proxy on Server");
					marshal2 = new MessageInvokeFunction(
							marshalm.getFunctionName(), marshalm.getArgs(),
							marshalm.getTypes(), returning, null,
							marshalm.getObjectKey(), marshalm.getObjName());
				} catch (Exception e) {
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
