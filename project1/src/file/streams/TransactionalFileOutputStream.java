package file.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream  implements Serializable {

	public TransactionalFileOutputStream(String string, boolean b) {
		
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		byte lowByte = (byte)(b & 0xFF);
		this.write(lowByte);
	}


		
	

}
