package slavemanagerstuff;

import interfaces.MigratableProcess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import processes.ThreadProcess;
import processmanagerstuff.HeaderPacket;
import processmanagerstuff.ProcessManager2;
import processmanagerstuff.ProcessManager3;

public class SlaveHelper implements Runnable {

	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;

	private int id = -1;

	public SlaveHelper(ObjectInputStream in, ObjectOutputStream out, int id) {
		System.out.println("Slave Helper Spawned");
		this.out = out;
		this.in = in;
		this.id = id;
	}

	
	// test Empty Cases
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			int iter = 0;
			String[] filePaths = null;
			while (true) {
				Object read = in.readObject();
				
				String command = (String) read;
				System.out.println(" Received Command " + command);
				if (command.equals("__START__")) {
					System.out.println("Got start ");
					String allFilePaths = new String();
					// Start Case : Suspend all Processes and serialize to File
					// and send back the File Paths
					for (int k : ProcessManager3.runningProcesses.keySet()) {
						MigratableProcess process = ProcessManager3.runningProcesses
								.get(k);
						process.suspend();
						ProcessManager3.runningProcesses.remove(k);
						iter++;
						// Writing Suspended Processes to Disk
						String filePath = "/tmp/" + iter + id
								  + k + ".dat";

						File processFile = new File(filePath);
						if (!processFile.exists()) {
							processFile.createNewFile();
						}

						// System.out.println("Writing the following : ");
						FileOutputStream f_out = new FileOutputStream(
								processFile, false);
						ObjectOutputStream oos = new ObjectOutputStream(f_out);
						oos.writeObject((Object) process);

						// Sending Back File Path
						filePath += "\t" + k;
						if (allFilePaths.length() == 0) {
							allFilePaths = filePath;
						} else {
							allFilePaths += "," + filePath;
						}
					}
					out.writeObject(allFilePaths);

				} else if (command.equals("__DONE__")) {
					System.out.println("Got Done ");
					// Done Case : Read process from file and spawn new Threads
					// for each Process
					for (String path : filePaths) {
						String[] filePathContents = path.split("\\t");
						path = filePathContents[0];
						int processNumber = Integer
								.parseInt(filePathContents[1]);
						FileInputStream f_in = new FileInputStream(path);
						ObjectInputStream oin = new ObjectInputStream(f_in);
						MigratableProcess process = (MigratableProcess) oin
								.readObject();

						Thread processThread = new Thread(process);
						ProcessManager3.runningProcesses.put(processNumber,
								process); // Add to all processes collection
						processThread.start();
					}

				} else {
					System.out.println("Got File Paths ");
					// File Path Case : Store File Paths
					filePaths = command.split(",");
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println("Spawned a New Thread for Connection");
		// try {
		// int numProcess = (Integer) in.readObject();
		// System.out.println(" Recieved Int which is Size " + numProcess);
		// HeaderPacket header = (HeaderPacket) in.readObject();
		//
		// System.out.println(" Recieved Header Packet " + " NumProcess " +
		// header.getNumProcess() + " FilePath "+ header.getFilePath() +
		// " client Id = " + header.getId());
		// FileInputStream fileIn = new FileInputStream(header.getFilePath());
		// ObjectInputStream oIn = new ObjectInputStream(fileIn);
		//
		// for (int i =0;i<header.getNumProcess();i++){
		// MigratableProcess process = (MigratableProcess) oIn.readObject();
		// //System.out.println("Received Process Back in Child Reader= " +
		// process.toString());
		//
		// Thread processThread = null;
		// processThread = new Thread(process);
		// ThreadProcess tp = new ThreadProcess(processThread, process);
		// ProcessManager2.allProcesses.put(processThread.getId(), tp); //Add to
		// all processes collection
		// System.out.println("Starting New Process Back");
		// processThread.start();
		// }
		// }

		// catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}
