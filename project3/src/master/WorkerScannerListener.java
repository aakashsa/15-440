package master;

import interfaces.JobConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map.Entry;

import lib.Job;
import lib.JobConfigurationLoader;
import lib.Utils;

import communication.Message;
import communication.MessageType;

/**
 * This class listens for job management requests from workers
 * in case the programmer or admin requested them on the worker 
 *
 */
public class WorkerScannerListener implements Runnable {

	private int port;
	private int numWorkers;
	
	/**
	 * Start listener on the given port
	 * @param port Port to listen on
	 * @param numWorkers Number of workers listening from
	 */
	public WorkerScannerListener(int port, int numWorkers) {
		this.port = port;
		this.numWorkers = numWorkers;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			
			while (true) {
				Socket workerSocket = server.accept();
				OutputStream output = workerSocket.getOutputStream();
				InputStream input = workerSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				out.flush();
				ObjectInputStream in = new ObjectInputStream(input);

				Message requestMsg = (Message) in.readObject();
				
				// List jobs message
				if (requestMsg.type == MessageType.LISTJOBS) {
					System.out.println("[INFO] Current Jobs:");
					for (Entry<Integer, Job> e : HadoopMaster.jobMap.entrySet()){
						System.out.println("  Job ID = " + e.getKey() + ", Job name = " + e.getValue().getJobName());
					}
				}
				
				// Kill job message
				else if (requestMsg.type == MessageType.KILLJOB) {
					String [] jobName = requestMsg.jobName.split("_");
					int id = Integer.parseInt(jobName[1]);
					System.out.println("Quitting Job " + requestMsg.jobName);
					JobThread killJob = HadoopMaster.jobThreadObjectMap.get(id);
					Thread killThread = HadoopMaster.jobThreadMap.get(id);
					killJob.jobCleanup();
					killThread.suspend();
					HadoopMaster.jobMap.remove(id);
				}
				
				// Run job
				else if (requestMsg.type == MessageType.RUNJOB) {
					// Initialize the Job Object
					String inputFile = requestMsg.inputFilePath;
					String jobConfigDir = requestMsg.configFilesDir;
					Job job = null;
					try {
						JobConfigurationLoader jcl = new JobConfigurationLoader();
						Class<?> jobConfClass = jcl.getClass(jobConfigDir, "JobSetupClass");
						JobConfiguration jConf = (JobConfiguration) jobConfClass.newInstance();
						job = jConf.setup();
						// Performing Sanity Checks on the Job provided
						Utils.performJobSanityChecks(job, numWorkers);
						HadoopMaster.jobCounter++;
						HadoopMaster.jobMap.put(HadoopMaster.jobCounter, job);
						JobThread newJob = new JobThread(inputFile,job);
						HadoopMaster.jobThreadObjectMap.put(HadoopMaster.jobCounter,newJob);
						Thread new_thread = new Thread(newJob);
						HadoopMaster.jobThreadMap.put(HadoopMaster.jobCounter, new_thread);
						new_thread.start();
					} catch (InstantiationException e) {
						System.out.println("[ERROR] Job configuration instantiation error");
						continue;
					} catch (IllegalAccessException e) {
						System.out.println("[ERROR] Job configuration instantiation error");
						continue;
					} catch (IllegalArgumentException e) {
						System.out.println("[ERROR] " + e.getMessage());
						continue;
					} catch (IOException e) {
						System.out.println("[ERROR] " + e.getMessage());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}	
}
