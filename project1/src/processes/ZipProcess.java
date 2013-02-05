package processes;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import filestreams.TransactionalFileInputStream;
import filestreams.TransactionalFileOutputStream;

import interfaces.MigratableProcess;

/**
 * A simple Process That zips input file to an output zip file.
 */
public class ZipProcess implements MigratableProcess, Serializable {

	private static final long serialVersionUID = 1L;
	
	// Argument List
	private ArrayList<String> arguments = null;
	
	// File Streams
	private TransactionalFileInputStream in;
	private TransactionalFileOutputStream fos;
	
	// To Suspend Process
	private volatile boolean suspending;

	public ZipProcess(String args[]) throws Exception {
		if (args.length != 2) {
			System.out.println("usage: processes.ZipProcess <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}
		
		arguments = new ArrayList<String>(Arrays.asList(args));

		File processFile = new File(args[1]);
		if (!processFile.exists()) {
			try {
				processFile.createNewFile();
			} catch (IOException e) {
				System.out.println("ERROR : File Create Exception");
				e.printStackTrace();
			}
		}
		try {
			fos = new TransactionalFileOutputStream(args[1], false);
			in = new TransactionalFileInputStream(args[0]);
		} catch (IOException e) {
			System.out.println("ERROR : File IO Exception");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(fos);
			ZipEntry ze = new ZipEntry(arguments.get(0));
			zos.putNextEntry(ze);
		} catch (IOException e1) {
			System.out.println("ERROR : File IO Exception");
			e1.printStackTrace();
		}

		try {

			while (!suspending) {
				Thread.sleep(4*1000);
				int len;
				if ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				} 
				else {
					zos.closeEntry();					 
		    		zos.close();
					break;					
				}
			}
		} catch (IOException e) {
			System.out.println("ERROR : File IO Exception");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("ERROR : Interupt Exception");
			e.printStackTrace();
		}
		suspending = false;
	}

	@Override
	public void suspend() {
		// suspending;
		suspending = true;
		while (suspending);
	}

	public String toString() {
		String result = this.getClass().getSimpleName();
		for (String arg : arguments) {
			result = result + " " + arg;
		}
		return result;
	}
}
