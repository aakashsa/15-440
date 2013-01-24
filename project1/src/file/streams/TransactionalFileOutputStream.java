package file.streams;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream  implements Serializable {

	private transient RandomAccessFile fileObject;
	private String fileString; 
	private long pointer;

	
	public TransactionalFileOutputStream(String fileString, boolean b) {
		try {
			this.fileObject = new RandomAccessFile(fileString, "w");
			this.fileString = fileString;
			pointer = 0;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		byte lowByte = (byte)(b & 0xFF);
		this.write(lowByte);
	}

	public void write(byte[] b) throws IOException {
		

	}

	public void write(byte[] b,int off,int len) throws IOException {
		

	}	
	

}
