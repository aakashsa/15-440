package processmanagerstuff;

import java.io.Serializable;

public class HeaderPacket implements Serializable {

//	out.writeObject((Object) new Integer(id));
//	out.writeObject((Object) new Integer(i)); 
//	out.writeObject((Object) new String(filePath));
	private int id;
	private int numProcess;
	private String filePath;
	
	public HeaderPacket(int id,int numProcess, String filePath){
		this.id = id;
		this.numProcess = numProcess;
		this.filePath = filePath;
	}
	
	public int getId(){
		return this.id;
	}
	
	public int getNumProcess(){
		return this.numProcess;
	}
	
	public String getFilePath(){
		return this.filePath;
	}

}
