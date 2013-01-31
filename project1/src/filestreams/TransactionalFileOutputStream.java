package filestreams;

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
			this.fileObject = new RandomAccessFile(fileString, "rw");
			this.fileString = fileString;
			pointer = 0;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void write(int b){
		//System.out.println(" Calling function 4 ");

		try {
			this.fileObject = new RandomAccessFile(fileString, "rw");
			fileObject.seek(pointer);
			byte lowByte = (byte)(b & 0xFF);
			fileObject.write(lowByte);
			pointer = fileObject.getFilePointer();
			fileObject.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public void write(byte[] b) throws IOException {
		//System.out.println(" Calling function 5 ");

		try {
			this.fileObject = new RandomAccessFile(fileString, "rw");
			fileObject.seek(pointer);
			fileObject.write(b);
			pointer = fileObject.getFilePointer();
			fileObject.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void write(byte[] b,int off,int len) throws IOException {
		//System.out.println(" Calling function 6 ");
		try {
			this.fileObject = new RandomAccessFile(fileString, "rw");
			fileObject.seek(pointer);
			fileObject.write(b,off,len);
			pointer = fileObject.getFilePointer();
			fileObject.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	

}
