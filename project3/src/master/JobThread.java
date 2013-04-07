package master;

import java.io.File;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lib.ConstantsParser;
import lib.Job;
import lib.Utils;
import test.JobConfiguration;

import communication.ChunkObject;
import communication.MapTask;
import communication.Message;
import communication.MessageType;
import communication.ReduceTask;
import communication.ServiceMapThread;
import communication.ServiceReduceThread;
import communication.WorkerInfo;

public class JobThread implements Runnable {

	public static Socket[] workerSockets;

	public static ConcurrentLinkedQueue<ChunkObject> chunkQueue;
	public static ConcurrentLinkedQueue<Integer> freeWorkers;
	public static ConcurrentHashMap<ChunkObject, Integer> chunkWorkerMap;
	public static ConcurrentHashMap<Integer, ChunkObject> busyWorkerMap;
	public static ConcurrentLinkedQueue<MessageType> reduceDoneMessages;

	public static final Object OBJ_LOCK = new Object();
	public static int fileSizeRead = 0;
	public static int mapsDone = 0;

	private String jobConfigDir;
	private String inputFile;
	private String configFile;
	
	public JobThread(String inputFile, String configFile, String jobConfigDir){
		this.inputFile = inputFile;	
		this.jobConfigDir = jobConfigDir;
		this.configFile = configFile;
	}
	
	@Override
	public void run() {

		// Initialize status data structures
		chunkQueue = new ConcurrentLinkedQueue<ChunkObject>();
		freeWorkers = new ConcurrentLinkedQueue<Integer>();
		chunkWorkerMap = new ConcurrentHashMap<ChunkObject, Integer>();
		busyWorkerMap = new ConcurrentHashMap<Integer, ChunkObject>();


		// Parse the JSON config file
		ConstantsParser cp = new ConstantsParser(configFile);
		long recordSize = cp.getRecordSize();
		long chunkSize = cp.getChunkSize();
		HashMap<Integer, WorkerInfo> allWorkers = cp.getAllWorkers();
		int numWorkers = allWorkers.size();

		// Get jobs and do sanity checks
		Job job = null;
		try {
			Class<?> jobConfClass = Class.forName(jobConfigDir + ".JobConfiguration");
			JobConfiguration jConf = (JobConfiguration) jobConfClass.newInstance();
			Job[] jobs = jConf.setup();
			job = jobs[0];
			Utils.performJobSanityChecks(job);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.exit(0);
		}

		System.out.println("[INFO] Starting job " + job.getJobName());
		
		// Get input file name and size
		System.out.println("File Path = " + inputFile);
		File f = new File(inputFile);
		int fileSize = (int) f.length();
		System.out.println("File Size = " + fileSize);

		long round = (fileSize % recordSize);
		long numRecordsInFile = (fileSize / recordSize);
		if (round != 0)
			numRecordsInFile++;
		long numRecordsPerChunk = chunkSize / recordSize;

		System.out.println(" num of Records in File = " + numRecordsInFile);
		System.out.println(" num of Records per Chunk = " + numRecordsPerChunk);

		long numChunks = fileSize / (numRecordsPerChunk * recordSize);
		round = fileSize % (numRecordsPerChunk * recordSize);

		if (round != 0)
			numChunks++;
		System.out.println(" num of Chunks = " + numChunks);

		// Spawn threads for telling workers to map appropriate chunks
		// First add all workers to free workers queue
		workerSockets = new Socket[(int) numWorkers];
		for (int i = 0; i < numWorkers; i++) {
			freeWorkers.add(i);
		}

		// Add all chunks to chunk queue, and assign to null workers initially
		for (int i = 0; i < numChunks; i++) {
			ChunkObject chunKey = new ChunkObject(i, i * numRecordsPerChunk,
					numRecordsPerChunk, (int) recordSize, inputFile);
			chunkQueue.add(chunKey);
			chunkWorkerMap.put(chunKey, -1);
		}

		// Map setup. Check if the partition folder exists
		File theDir = new File(Utils.getPartitionDirName(job.getJobName()));
		if (!theDir.exists()) {
			System.out.println("[INFO] Creating directory: " + Utils.getPartitionDirName(job.getJobName()));
			theDir.mkdir();
		}
		
		// Reduce Set up - Checking if all the reduce folders exist
		for (int j = 0; j < cp.getNumReducers(); j++) {
			theDir = new File(Utils.getReducerFolderName(j, job.getJobName()));
			if (!theDir.exists()) {
				System.out.println("[INFO] Creating directory: " + Utils.getReducerFolderName(j, job.getJobName()));
				theDir.mkdir();
			}
		}
		
		// Create reduce output directory if doesn't exist
		theDir = new File(Utils.getFinalAnswersDir());
		if (!theDir.exists()) {
			System.out.println("[INFO] Creating directory: " + Utils.getFinalAnswersDir());
			theDir.mkdir();
		}
		
		Thread[] t_array = new Thread[(int) numChunks];
		int i = 0;
		
		// Mapping
		while (!chunkWorkerMap.isEmpty() && !chunkQueue.isEmpty()) {
			synchronized (OBJ_LOCK) {
				if (!freeWorkers.isEmpty() && !chunkQueue.isEmpty()) {
					ChunkObject chunkJob = null;
					int newWorker = 0;
					chunkJob = chunkQueue.remove();
					MapTask task = new MapTask(chunkJob, job.getFileInputFormatClass(), job.getMapperClass(), cp, job.getJobName());
					Message mapMessage = new Message(MessageType.START_MAP, task);
					newWorker = freeWorkers.remove();
					busyWorkerMap.put(newWorker, chunkJob);
					t_array[i] = new Thread(new ServiceMapThread(mapMessage, newWorker, allWorkers.get(newWorker)));
					t_array[i].start();
					i++;
				}
			}
		}

		System.out.println("[INFO] Done Map. Starting reduce tasks...");
		reduceDoneMessages = new ConcurrentLinkedQueue<MessageType>();
		
		while (true) {
			int temp;
			synchronized (JobThread.OBJ_LOCK) {
				temp = JobThread.mapsDone;
			}
			if (temp == numChunks) {
				synchronized (JobThread.OBJ_LOCK) {
					JobThread.mapsDone = 0;
				}
				System.out.println("[INFO] Sending reduce commands to " + cp.getNumReducers() + " reducers");
				for (int j = 0; j < cp.getNumReducers(); j++) {
					WorkerInfo info = allWorkers.get(j);
					ReduceTask task = new ReduceTask(info.getWorkerNum(), job.getReducerClass(), job.getMapperOutputKeyClass(), job.getMapperOutputValueClass(), Utils.getFinalAnswersDir(), job.getJobName());
					Message reduceMsg = new Message(MessageType.START_REDUCE, task);
					new Thread(new ServiceReduceThread(info, reduceMsg)).start();
				}
				break;
			}
		}
		
		while (true) {
			if (reduceDoneMessages.size() == cp.getNumReducers()) {
				Utils.removeDirectory(new File(Utils.getPartitionDirName(job.getJobName())));
				break;
			}
		}
		System.out.println("[INFO] Done " + job.getJobName() + " job");
	}	
}
