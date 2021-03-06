package processes;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import filestreams.TransactionalFileInputStream;
import filestreams.TransactionalFileOutputStream;
import interfaces.MigratableProcess;

/**
 * Simple Process to do the ROT13 substitution cipher of the text of a file.
 */
public class ROT13Process implements MigratableProcess {
	
	private static final long serialVersionUID = 1L;
	private TransactionalFileInputStream inFile;
	private TransactionalFileOutputStream outFile;
	
	// Argument List
	private ArrayList<String> arguments = null;

	private volatile boolean suspending;

	public ROT13Process(String args[]) throws Exception {
		if (args.length != 2) {
			System.out.println("usage: ROT13Process <inputFile> <outputFile>");
			throw new Exception("ERROR: Invalid Arguments");
		}
		
		// Open the input and output files
		inFile = new TransactionalFileInputStream(args[0]);
		outFile = new TransactionalFileOutputStream(args[1], false);
		
		// Store arguments passed in
		arguments = new ArrayList<String>(Arrays.asList(args));
	}

	@Override
	public void run() {
		// The ROT13 algorithm
		PrintStream out = new PrintStream(outFile);
		DataInputStream in = new DataInputStream(inFile);

		try {
			while (!suspending) {
				Thread.sleep(4*1000);
				String line = in.readLine();
				if (line == null){
					break;
				}
				for (char c : line.toCharArray()) {
					if (c >= 'a' && c <= 'm')
						c += 13;
					else if (c >= 'A' && c <= 'M')
						c += 13;
					else if (c >= 'n' && c <= 'z')
						c -= 13;
					else if (c >= 'N' && c <= 'Z')
						c -= 13;
					out.print(c);
				}
				out.println();				
			}
		} catch (EOFException e) {
			System.out.println("ERROR (" +this.getClass().getSimpleName()+ "): " + e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println("ERROR (" +this.getClass().getSimpleName()+ "): " + e.getLocalizedMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		suspending = false;
	}

	@Override
	public void suspend() {
		suspending = true;
		while (suspending)
			;
	}
	
	public String toString() {
		String result = this.getClass().getSimpleName();
		for (String arg : arguments) {
			result = result + " " + arg;
		}
		return result;
	}

}
