package processes;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;

import filestreams.TransactionalFileInputStream;
import filestreams.TransactionalFileOutputStream;
import interfaces.MigratableProcess;

//processes.ROT13 rot13input.txt output.txt 

/**
 * Simple Process to do the ROT13 substitution cipher of the text of a file.
 * @author aakashsa
 */
public class ROT13 implements MigratableProcess {
	private static final long serialVersionUID = 1L;
	private TransactionalFileInputStream inFile;
	private TransactionalFileOutputStream outFile;

	private volatile boolean suspending;

	public ROT13(String args[]) throws Exception {
		if (args.length != 2) {
			System.out.println("usage: ROT13 <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}

		inFile = new TransactionalFileInputStream(args[0]);
		outFile = new TransactionalFileOutputStream(args[1], false);
	}

	@Override
	public void run() {
		PrintStream out = new PrintStream(outFile);
		DataInputStream in = new DataInputStream(inFile);

		try {
			while (!suspending) {

				String line = in.readLine();
				if (line == null){
					System.out.println("Finished ROT13");
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
			System.out.println("GrepProcess: Error: " + e);
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

}
