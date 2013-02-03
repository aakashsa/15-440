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

//processes.ZipProcess input.txt output.zip

public class ZipProcess implements MigratableProcess, Serializable {

	private ArrayList<String> arguments = null;

	private TransactionalFileInputStream in;
	private TransactionalFileOutputStream fos;
	private String fileName;

	private volatile boolean suspending;

	public ZipProcess(String args[]) {
		arguments = new ArrayList<String>(Arrays.asList(args));

		File processFile = new File(args[1]);
		if (!processFile.exists()) {
			try {
				processFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			fos = new TransactionalFileOutputStream(args[1], false);
			in = new TransactionalFileInputStream(args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];
		System.out.println(" Running ZipProcess");
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(fos);
			ZipEntry ze = new ZipEntry(arguments.get(0));
			zos.putNextEntry(ze);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// TODO Auto-generated method stub
		try {

			while (!suspending) {
				Thread.sleep(4*1000);
				int len;
				if ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				} 
				else {
					System.out.println("Finished ZIP");
					zos.closeEntry();					 
		    		zos.close();
					break;					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		suspending = false;
		System.out.println("Process \"" + this.toString() + "\" was terminated");
	}

	@Override
	public void suspend() {
		// suspending + "\n");
		suspending = true;
		System.out.println("Suspending Zip\n");
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
