package processes;

import interfaces.MigratableProcess;
import file.streams.TransactionalFileInputStream;
import file.streams.TransactionalFileOutputStream;


import java.io.PrintStream;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.Thread;
import java.lang.InterruptedException;

public class GrepProcess implements MigratableProcess
{
	private TransactionalFileInputStream  inFile;
	private TransactionalFileOutputStream outFile;
	private String query;

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
	}

	public void run()
	{
		System.out.println("Running Grep Processes \n");
		PrintStream out = new PrintStream(outFile);
		DataInputStream in = new DataInputStream(inFile);

		//try {
			while (!suspending) {

				//String line = in.readLine();
				System.out.println("Line :" + i + " \n");
				i++;
				
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
		suspending = true;
		System.out.println("Hi From Suspend\n");
		while (suspending);
	}

}