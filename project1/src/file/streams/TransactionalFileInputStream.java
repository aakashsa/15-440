package file.streams;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class TransactionalFileInputStream   extends FileInputStream implements Serializable {

	private long pointer;
	
	public TransactionalFileInputStream(String string) throws FileNotFoundException {
		super(string);
		pointer = 0;
	}

	@Override
	public int read() throws IOException {
		super.skip(pointer);
		byte[] readByte = new byte[1]; 
		int returnValue = super.read(readByte,0, 1);
		if (returnValue!=-1)
			pointer+=returnValue;
		return readByte[0];
	}
	
	public int read(byte[] b,int off,int len) throws IOException {
		
		super.skip(pointer);
		int returnValue = super.read(b,off, len);
		if (returnValue!=-1)
			pointer+=returnValue;
		return returnValue;
	}
	
	 
	public int read(byte[] b) throws IOException {
		
		super.skip(pointer);
		int returnValue = super.read(b);
		if (returnValue!=-1)
			pointer+=returnValue;
		return returnValue;
	}
}
