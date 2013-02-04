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
import processmanagerstuff.ProcessManager3;

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
				// Read for messages from Load Balancer
				Object read = in.readObject();
				String command = (String) read;
				if (command != null) {
					if (command.equals("__START__")) {

						String allFilePaths = new String();
						// Start Case : Suspend all Processes and serialize to
						// file and send back the File Paths

						for (int k : ProcessManager3.runningProcesses.keySet()) {

							ThreadProcess tp = ProcessManager3.runningProcesses
									.get(k);

							if (!tp.getThread().isAlive()) {
								System.out
										.println("THREAD DIED : PROCESS ENDED ");
								tp.getThread().join();
								ProcessManager3.runningProcesses.remove(k);
								System.out.println(" Removed + " + k);
							} else {
								MigratableProcess process = tp.getProcess();
								process.suspend();
								ProcessManager3.runningProcesses.remove(k);
								iter++;
								// Writing Suspended Processes to Disk
								String filePath = ProcessManager3.fileDirectory
										+ iter + id + k + ".dat";

								File processFile = new File(filePath);
								if (!processFile.exists()) {
									processFile.createNewFile();
								}

								// Write process to file
								FileOutputStream f_out = new FileOutputStream(
										processFile, false);
								ObjectOutputStream oos = new ObjectOutputStream(
										f_out);
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
					} else if (command.equals("__DONE__")) {
						// Done Case : Read process from file and spawn new
						// threads for each process
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
									// Add to running processes collection
									ProcessManager3.runningProcesses.put(
											processNumber, tp);
									processThread.start();
								}
							}
							// Reset file paths
							filePaths = null;
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
			System.out.println("Master PM died. Quitting...");
			System.exit(-1);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
