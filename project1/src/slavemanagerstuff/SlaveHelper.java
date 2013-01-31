package slavemanagerstuff;

import interfaces.MigratableProcess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import processes.ThreadProcess;
import processmanagerstuff.ProcessManager3;

public class SlaveHelper implements Runnable {

	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;
	private int id = -1;

	public SlaveHelper(InputStream input, OutputStream output, int id,
			Socket clientSocket, ObjectOutputStream out, ObjectInputStream in) {
		System.out.println("Slave Helper Spawned");
		// this.out = out;
		// this.in = in;
		try {
			this.out = out;// new ObjectOutputStream(output);
			this.out.flush();
			System.out.println("before opening input stream in slave helper");
			this.in = in;// new ObjectInputStream(input);
			System.out.println("after opening input stream in slave helper");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.id = id;
	}

	@Override
	public void run() {
		try {
			int iter = 0;
			String[] filePaths = null;
			while (true) {
				System.out.println("Reading in Slave Helper");
				// Reads for messages from Load Balancer
				Object read = in.readObject();

				String command = (String) read;
				System.out.println(" Received Command " + command);
				if (command != null) {
					if (command.equals("__START__")) {
						System.out.println("Got start ");
						String allFilePaths = new String();
						// Start Case : Suspend all Processes and serialize to
						// File
						// and send back the File Paths
						System.out.println(" Length of RUnning Processes " + ProcessManager3.runningProcesses.keySet().size());
						for (int k : ProcessManager3.runningProcesses.keySet()) {
							
							ThreadProcess tp = ProcessManager3.runningProcesses
									.get(k);

							if (!tp.getThread().isAlive()) {
								System.out.println("THREAD DIED : PROCESS ENDED ");
								tp.getThread().join();
								ProcessManager3.runningProcesses.remove(k);
								System.out.println(" Removed + "+ k);
							} else {
								MigratableProcess process = tp.getProcess();
								process.suspend();
								ProcessManager3.runningProcesses.remove(k);
								iter++;
								// Writing Suspended Processes to Disk
								String filePath = "/tmp/" + iter + id + k
										+ ".dat";

								File processFile = new File(filePath);
								if (!processFile.exists()) {
									processFile.createNewFile();
								}

								// System.out.println("Writing the following : ");
								FileOutputStream f_out = new FileOutputStream(
										processFile, false);
								ObjectOutputStream oos = new ObjectOutputStream(
										f_out);
								oos.writeObject((Object) process);
								oos.flush();
								oos.close();
								// Sending Back File Path
								filePath += "\t" + k;
								if (allFilePaths.length() == 0) {
									allFilePaths = filePath;
								} else {
									allFilePaths += "," + filePath;
								}
							}
							System.out.println("Wrote to Disk, and all filepaths: "
									+ allFilePaths);
						}
						
						out.writeObject(allFilePaths);// allFilePaths);
						out.flush();
						System.out
								.println("Sent all filepaths to client thread.");

					} else if (command.equals("__DONE__")) {
						System.out.println("Got Done ");
						// Done Case : Read process from file and spawn new
						// Threads
						// for each Process
						if (filePaths != null) {
							for (String path : filePaths) {
								if (path.length() > 0) {
									String[] filePathContents = path
											.split("\\t");
									path = filePathContents[0];
									int processNumber = Integer
											.parseInt(filePathContents[1]);
									FileInputStream f_in = new FileInputStream(
											path);
									ObjectInputStream oin = new ObjectInputStream(
											f_in);
									MigratableProcess process = (MigratableProcess) oin
											.readObject();
									oin.close();
									Thread processThread = new Thread(process);
									ThreadProcess tp = new ThreadProcess(
											processThread, process);
									System.out.println("Adding PRocess to Running Process "+ processNumber);
									// Add to all processes collection
									ProcessManager3.runningProcesses.put(
											processNumber, tp);
									processThread.start();
								}
							}
						}

					} else {
						System.out.println("Got File Paths ");
						// File Path Case : Store File Paths
						if (command.length() > 0)
							filePaths = command.split(",");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
