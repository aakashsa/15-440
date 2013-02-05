package slavemanagerstuff;

import interfaces.MigratableProcess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import processes.ThreadProcess;
import processmanagerstuff.ProcessManager;

/**
 * This class is responsible for communicating on the client side.
 * It sends data/messages to server and receives as well.
 *
 */
public class SlaveHelper implements Runnable {

	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;
	private int id = -1;

	public SlaveHelper(int id, Socket clientSocket, ObjectOutputStream out,
			ObjectInputStream in) {
		try {
			this.out = out;
			this.out.flush();
			this.in = in;
		} catch (IOException e) {
			System.out.println("ERROR: Slave helper couldn't flush " +
					"stream (" + e.getLocalizedMessage() + ")");
		}
		this.id = id;
	}

	@Override
	public void run() {
		try {
			int iter = 0;
			String[] filePaths = null;
			while (true) {
				// Read for messages from Load Balancer
				Object read = in.readObject();
				String command = (String) read;
				if (command != null) {
					if (command.equals("__START__")) {

						// Start Case : Suspend all Processes and serialize to
						// file and send back the File Paths
						
						String allFilePaths = new String();
						for (int k : ProcessManager.runningProcesses.keySet()) {

							ThreadProcess tp = 
									ProcessManager.runningProcesses.get(k);
							
							// If the process terminated, remove it
							if (!tp.threadIsAlive()) {
								System.out.println("Process \"" + tp.getProcess().toString() + "\" was terminated");
								tp.getThread().join();
								ProcessManager.runningProcesses.remove(k);
							} 
							else {
								// The process is running, so serialize it
								MigratableProcess process = tp.getProcess();
								process.suspend();
								ProcessManager.runningProcesses.remove(k);
								iter++;
								
								// Write suspended processes to disk
								String filePath = ProcessManager.fileDirectory
										+ iter + id + k + ".dat";

								File processFile = new File(filePath);
								if (!processFile.exists()) {
									processFile.createNewFile();
								}

								FileOutputStream f_out = 
										new FileOutputStream(processFile, false);
								ObjectOutputStream oos = 
										new ObjectOutputStream(f_out);
								oos.writeObject((Object) process);
								oos.flush();
								oos.close();

								// Append all file paths with comma
								filePath += "\t" + k;
								if (allFilePaths.length() == 0) {
									allFilePaths = filePath;
								} else {
									allFilePaths += "," + filePath;
								}
							}
						}
						// Send all file paths to load balancer
						out.writeObject(allFilePaths);
						out.flush();
					} 
					else if (command.equals("__DONE__")) {
						// Done Case : Read process from file and spawn new
						// threads for each process
						if (filePaths != null) {
							for (String path : filePaths) {
								if (path.length() > 0) {
									String[] filePathContents = path
											.split("\\t");
									path = filePathContents[0];
									int processNumber = 
											Integer.parseInt(filePathContents[1]);
									FileInputStream f_in = 
											new FileInputStream(path);
									ObjectInputStream oin = 
											new ObjectInputStream(f_in);
									MigratableProcess process = 
											(MigratableProcess) oin.readObject();
									oin.close();
									Thread processThread = new Thread(process);
									ThreadProcess tp = 
											new ThreadProcess(processThread, process);
									
									// Add to running processes collection
									ProcessManager.runningProcesses.put(processNumber, tp);
									processThread.start();
									
									// Delete file in the previous iteration
									File processFile = new File(path);
									processFile.delete();
								}
							}
							// Reset file paths
							filePaths = null;
						}
					} else {
						// File Path Case : Store File Paths
						if (command.length() > 0)
							filePaths = command.split(",");
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Master PM died. Quitting...");
			System.exit(0);
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: Slave helper had trouble in " +
					"reading (" + e.getLocalizedMessage() + ")");
		} catch (InterruptedException e) {
			System.out.println("ERROR: Slave helper - trouble in " +
					"killing thread (" + e.getLocalizedMessage() + ")");
		}
	}
}
