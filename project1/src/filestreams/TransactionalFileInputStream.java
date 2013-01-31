package filestreams;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

// Extends InputStream but encloses a FileInputStream
// object that gets reinitialized every time
public class TransactionalFileInputStream   extends InputStream implements Serializable {

	private long pointer;
	private transient FileInputStream fileStream;
	private String filePath;
	
	public TransactionalFileInputStream(String string) throws FileNotFoundException {
		this.filePath = string;
		this.fileStream = new FileInputStream(string);
		pointer = 0;
	}

	@Override
	public int read() throws IOException {
		//System.out.println(" Calling function 1 ");
		this.fileStream = new FileInputStream(filePath);
		
		fileStream.skip(pointer);
		byte[] readByte = new byte[1]; 
		int returnValue = fileStream.read(readByte,0, 1);
		if (returnValue!=-1)
			pointer+=returnValue;
		else 
			return -1;
		fileStream.close();
		return readByte[0];
	}
	
	public int read(byte[] b,int off,int len) throws IOException {
		//System.out.println(" Calling function 2 ");

		this.fileStream = new FileInputStream(filePath);
		fileStream.skip(pointer);
		int returnValue = fileStream.read(b,off, len);
		if (returnValue!=-1)
			pointer+=returnValue;
		fileStream.close();
		return returnValue;
	}
	
	 
	public int read(byte[] b) throws IOException {
		//System.out.println(" Calling function 3 ");

		this.fileStream = new FileInputStream(filePath);
		fileStream.skip(pointer);
		int returnValue = fileStream.read(b);
		if (returnValue!=-1)
			pointer+=returnValue;
		fileStream.close();
		return returnValue;
	}
}
