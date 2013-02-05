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
			System.out.println("usage: ROT13 <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}

		inFile = new TransactionalFileInputStream(args[0]);
		outFile = new TransactionalFileOutputStream(args[1], false);
		
		arguments = new ArrayList<String>(Arrays.asList(args));
	}

	@Override
	public void run() {
		PrintStream out = new PrintStream(outFile);
		DataInputStream in = new DataInputStream(inFile);

		try {
			while (!suspending) {

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
			// End of File
		} catch (IOException e) {
			System.out.println("ERROR (" +this.getClass().getSimpleName()+ "): " + e.getLocalizedMessage());
		}

		suspending = false;
	}

	@Override
	public void suspend() {
		// TODO Auto-generated method stub
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
