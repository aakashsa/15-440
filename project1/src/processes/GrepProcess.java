package processes;

import interfaces.MigratableProcess;
import filestreams.TransactionalFileInputStream;
import filestreams.TransactionalFileOutputStream;


import java.io.PrintStream;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.util.ArrayList;
import java.util.Arrays;

public class GrepProcess implements MigratableProcess
{
	private TransactionalFileInputStream  inFile;
	private TransactionalFileOutputStream outFile;
	private String query;
	private String[] arguments;

	private volatile boolean suspending;

	private int i = 0;
	public GrepProcess(String args[]) throws Exception
	{
		if (args.length != 3) {
			System.out.println("usage: GrepProcess <queryString> <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}
		
		query = args[0];
		inFile = new TransactionalFileInputStream(args[1]);
		outFile = new TransactionalFileOutputStream(args[2], false);
		arguments = args;
	}

	public void run()
	{
		//System.out.println("Running Grep Processes with query \n" + query);
		PrintStream out = new PrintStream(outFile);
		DataInputStream in = new DataInputStream(inFile);
		//System.out.println("suspending var here is: " + suspending + "\n");
		//try {
			while (!suspending) {

				//String line = in.readLine();
				
				try {
					Thread.sleep(0*1000);
					//System.out.println("Grep still running. i: " + i + " " + query);
					i++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
//				try {
//					System.out.println("Sleeping");
//					Thread.sleep(5*1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				break;
//				if (line == null) break;
//				
//				if (line.contains(query)) {
//					out.println(line);
//				}
//				
//				// Make grep take longer so that we don't require extremely large files for interesting results
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// ignore it
//				}
//			}
//		} catch (EOFException e) {
//			//End of File
//		} catch (IOException e) {
//			System.out.println ("GrepProcess: Error: " + e);
		}


		suspending = false;
	}
	
	public void suspend()
	{
		//System.out.println("suspending var in beginning of suspend: " + suspending + "\n");
		suspending = true;
		//System.out.println("Suspending Grep\n");
		//System.out.println("suspending var after changing it: " + suspending + "\n");
		while (suspending);
	}
	
	public String toString() {
		String result = "GrepProcess";
		for (String arg : arguments) {
			result = result + " " + arg;
		}
		return result;
		
	}

}