package processmanagerstuff;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Iterator;
import processes.ThreadProcess;

public class LoadBalancer implements Runnable {

	public static int acked = 0;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public static List<HeaderPacket> list =  Collections.synchronizedList(new ArrayList<HeaderPacket>());
	
	public LoadBalancer(ObjectOutputStream out, ObjectInputStream in) {
		this.out = out;
		this.in = in;
	}

	@Override
	public void run() {
		System.out.println("Load Balancing Bro");
		 int totalLoad = 0;
		 int ideal_load = 0; 

		 try {
			 synchronized(list) {
			 	//out.writeObject(list.size());
			 	for (HeaderPacket item : list){
				 	totalLoad+=item.getNumProcess();
				 	out.writeObject((Object) item);
			 	}
			 	list.clear();
		 	}
		 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 }
	// TODO Auto-generated method stub

	}

}
