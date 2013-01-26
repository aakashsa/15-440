import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import interfaces.MigratableProcess;

public class processManager {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.print("\n Reading Lines \n");

		ArrayList<String> myArr = new ArrayList<String>();
		ConcurrentHashMap<Long, ThreadProcess> mp = new ConcurrentHashMap();
		// ArrayList<ThreadProcess> allProcesses = new
		// ArrayList<ThreadProcess>();

		Scanner sc2 = new Scanner(System.in);

		while (sc2.hasNextLine()) {
			System.out.println("Re enter scanner Loop");
			myArr.clear();
			Scanner s2 = new Scanner(sc2.nextLine());
			boolean b;

			// Scan the first word for process name or a different command
			if (s2.hasNext()) {
				String name = s2.next();
				if (name.equals("ps")) {
					if (s2.hasNext()) {
						System.out
								.println("Invalid command: ps does not take any arguments!");
					} else {
						System.out.println("Display all processes");
					}
				} else if (name.equals("quit")) {
					if (s2.hasNext()) {
						System.out
								.println("Invalid commant: quit does not take any arguments!");
					} else {
						System.out.println("Quit prorgram");
						break;
					}
				} else {
					// need to start a new process; parse arguments
					while (b = s2.hasNext()) {
						myArr.add(s2.next());
					}
					System.out
							.println("Started new process with following information: name: "
									+ name + ", arguments: " + myArr.toString());

					Constructor strCtor = null;

					try {
						System.out.println(" Name= " + "'" + name + "'");
						Class<?> process = Class.forName(name);
						System.out.println(" Process = " + process.toString());
						Class[] ctorArgs1 = new Class[1];

						ctorArgs1[0] = String[].class;
						strCtor = process.getConstructor(ctorArgs1);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Thread t = null;
					System.out.println("Constructor Found " + strCtor);
					try {
						System.out.println("Spawning a New Thread \n");
						System.out.println(" Arguments in myArr "
								+ myArr.toArray().toString());

						String[] hello = new String[myArr.size()];

						for (int i = 0; i < myArr.size(); i++)
							hello[i] = myArr.get(i);

						MigratableProcess a = (MigratableProcess) strCtor
								.newInstance((Object) hello);
						t = new Thread(a);
						ThreadProcess tp = new ThreadProcess(t, a);
						t.start();
						mp.put(t.getId(), tp);

						Thread.sleep(10);
						System.out
								.println("After starting thread " + t.getId());
						System.out.println("Number of processes running: "
								+ mp.size());
						// a.suspend();
						for (Long k : mp.keySet()) {
							ThreadProcess tpr = mp.get(k);
							if (tpr.threadIsAlive()) {
								System.out.println("Process number: "
										+ tpr.getThread().getId()
										+ " is still running");
							} else {
								System.out.println("Process number: "
										+ tpr.getThread().getId() + " is dead");
								tpr.getThread().join();
								mp.remove(k);
							}
						}

						// processes.GrepProcess hey src/input.txt
						// src/output.txt
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	}
}
// System.out.println(" Calling this :" + args[0] + " :"+ args[1] + " :"+
// args[2] + " :");
// if (args[0].equals("-c")){
// System.out.println(" Starting Slave \n");
// // Slave Case
// Socket echoSocket = null;
// ObjectOutputStream out = null;
// ObjectInputStream in = null;
//
// try {
//
// echoSocket = new Socket("localhost", 8000);
// System.out.println(" Before Loop 1 \n");
// out = new ObjectOutputStream(echoSocket.getOutputStream());
// out.flush();
// System.out.println(" Before Loop 2 \n");
//
// } catch (UnknownHostException e) {
// System.err.println("Don't know about host: Aakashs-MacBook-Pro.local.");
// System.exit(1);
// } catch (IOException e) {
// System.err.println("Couldn't get I/O for "
// + "the connection to: Aakashs-MacBook-Pro.local.");
// System.exit(1);
// }
//
// BufferedReader stdIn = new BufferedReader(
// new InputStreamReader(System.in));
// String userInput;
//
// System.out.println(" Before Loop\n");
// try {
// while (!(userInput = stdIn.readLine()).equals("quit")) {
// System.out.println(" Writing String 1\n");
// out.writeObject(new String(userInput));
// // out.flush();
// System.out.println(" Writing String 2\n");
// }
//
//
// System.out.println("Closing Connection Hostt\n");
//
// out.close();
// in.close();
// stdIn.close();
// echoSocket.close();
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
//
// }
// else {
// System.out.println(" Starting master \n");
// //Master Case
// ServerSocket serverSocket = null;
// try {
// serverSocket = new ServerSocket(8000);
// }
// catch (IOException e) {
// System.out.println("Could not listen on port: 4444");
// System.exit(-1);
// }
//
// Socket clientSocket = null;
// try {
// clientSocket = serverSocket.accept();
// }
// catch (IOException e) {
// System.out.println("Accept failed: 4444");
// System.exit(-1);
// }
// PrintWriter out = null;
// try {
// out = new PrintWriter(clientSocket.getOutputStream(), true);
// } catch (IOException e1) {
// // TODO Auto-generated catch block
// e1.printStackTrace();
// }
// ObjectInputStream in = null;
// try {
// in = new ObjectInputStream(clientSocket.getInputStream());
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// String inputLine, outputLine = null;
//
// // initiate conversation with client
// try {
// try {
// while (!(inputLine = (String) in.readObject()).equals("Done")) {
// System.out.println(" Read Line "+ inputLine +" \n ");
// }
// } catch (ClassNotFoundException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
// try {
// System.out.println("Closing Connection n Mastert\n");
// out.close();
// in.close();
// clientSocket.close();
// serverSocket.close();
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
// }
//
//
//
// }
// }

/*
 * // TODO Auto-generated method stub try { Class c =
 * Class.forName("processes.GrepProcess"); System.out.println(" Class Name = "
 * +c.getName() + " \n");
 * 
 * Class[] ctorArgs1 = new Class[1];
 * 
 * ctorArgs1[0] = String[].class; Constructor strCtor = null; try { strCtor =
 * c.getConstructor(ctorArgs1); } catch (SecurityException e) { // TODO
 * Auto-generated catch block e.printStackTrace(); } catch
 * (NoSuchMethodException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); } System.out.println("Constructor found: " +
 * strCtor.toString() +"\n"); String [] hello = new String[3]; hello[0] =
 * "hewyy"; hello[1] = "hdhaj"; hello[2] =" yfabjdbsja"; try {
 * System.out.println(" New Instance = " + strCtor.newInstance((Object)
 * hello).toString() ); } catch (IllegalArgumentException e) { // TODO
 * Auto-generated catch block e.printStackTrace(); } catch
 * (InstantiationException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); } catch (IllegalAccessException e) { // TODO
 * Auto-generated catch block e.printStackTrace(); } catch
 * (InvocationTargetException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); }
 * 
 * } catch (ClassNotFoundException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); } }
 */