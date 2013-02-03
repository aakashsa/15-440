package filestreams;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream  implements Serializable {

	private static final long serialVersionUID = 3140469395351996961L;
	private transient RandomAccessFile fileObject;
	private String fileString; 
	private long pointer;

	public TransactionalFileOutputStream(String fileString, boolean b) {
		try {
			this.fileObject = new RandomAccessFile(fileString, "rw");
			this.fileString = fileString;
			pointer = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void write(int b){
		try {
			this.fileObject = new RandomAccessFile(fileString, "rw");
			fileObject.seek(pointer);
			byte lowByte = (byte)(b & 0xFF);
			fileObject.write(lowByte);
			pointer = fileObject.getFilePointer();
			fileObject.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(byte[] b) throws IOException {
		try {
			this.fileObject = new RandomAccessFile(fileString, "rw");
			fileObject.seek(pointer);
			fileObject.write(b);
			pointer = fileObject.getFilePointer();
			fileObject.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(byte[] b,int off,int len) throws IOException {
		try {
			this.fileObject = new RandomAccessFile(fileString, "rw");
			fileObject.seek(pointer);
			fileObject.write(b,off,len);
			pointer = fileObject.getFilePointer();
			fileObject.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
