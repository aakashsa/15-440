package slavemanagerstuff;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

import processes.ThreadProcess;
import processmanagerstuff.HeaderPacket;
import processmanagerstuff.ProcessManager2;

public class ChildWriter implements Runnable {

	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;

	private int id = -1;
	
	public ChildWriter(ObjectInputStream in, ObjectOutputStream out, int id){
		System.out.println("Child Writer Spawned");
		this.out = out;
		this.id = id;
		this.in = in;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			int x = 0;
			
			while(true){
					//System.out.println("Writing Every 5 Seconds");
					Thread.sleep(10*1000);
					String filePath = "/tmp/myobject"+x+id+".data";
					x++;
					File childFile = new File(filePath);
					if(!childFile.exists()) {
					    childFile.createNewFile();
					} 
					
					//System.out.println("Writing the following : ");
					FileOutputStream f_out = new FileOutputStream(childFile,true);
					ObjectOutputStream oos = new ObjectOutputStream(f_out);

					//oos.writeObject((Object) new Integer(ProcessManager2.allProcesses.size()));
					int i = 0;
					for (Long k : ProcessManager2.allProcesses.keySet()) {
						ThreadProcess tp = ProcessManager2.allProcesses.get(k);
						System.out.println("Writing THIS ONE process number to Dis " + i + " " + tp.getProcess().toString());
						i++;
						tp.getProcess().suspend();
						oos.writeObject((Object)tp.getProcess());
						//System.out.println("Writen !! ");
						ProcessManager2.allProcesses.remove(k);
					}
					out.writeObject((Object) new HeaderPacket(id, i, filePath));
//					Thread ChildReader = new Thread(new ChildReader(in,id));
//					ChildReader.start();
//					oos.writeObject((Object)new Integer(-1));
//					
//					System.out.println(ProcessManager2.allProcesses.get(0).getProcess().toString());
//					out.writeObject((Object)ProcessManager2.allProcesses.get(0).getProcess());			
			
			}		
		}	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


