package master;

import java.io.File;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lib.ConstantsParser;
import lib.InsertionSortRecords;
import lib.Job;
import lib.Partitioner;
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

	private final Object MAPCOUNTER_LOCK;
	
	// Chunk to Worker mapping for the Job
	public static ConcurrentLinkedQueue<ChunkObject> chunkQueue;
	public static ConcurrentHashMap<ChunkObject, Integer> chunkWorkerMap;
	
	// Per Job Thread
	public static ConcurrentLinkedQueue<MessageType> reduceDoneMessages;
	
	private int fileSizeRead = 0;
	private int mapsDone = 0;
	private long numChunks = 0;
	
	private String jobConfigDir;
	private String inputFile;
	private String configFile;
	
	public int getMapCounter (){
		return mapsDone;
	}
	public void setMapCounter(int set){
		this.mapsDone = set;
	}
	public void incrementMapCounter(){
		this.mapsDone++;
	}
	
	
	public Object getMapCounterLock(){
		return MAPCOUNTER_LOCK;
	}
	
	public long getNumChunks(){
		return numChunks;
	}
	
	public JobThread(String inputFile, String configFile, String jobConfigDir){
		this.inputFile = inputFile;	
		this.jobConfigDir = jobConfigDir;
		this.configFile = configFile;
		this.MAPCOUNTER_LOCK = new Object();
		chunkQueue = new ConcurrentLinkedQueue<ChunkObject>();
		chunkWorkerMap = new ConcurrentHashMap<ChunkObject, Integer>();
	}
	
	@Override
	public void run() {
		mapsDone = 0;
		// Initialize status data structures


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

		long round = (fileSize % HadoopMaster.recordSize);
		long numRecordsInFile = (fileSize / HadoopMaster.recordSize);
		if (round != 0)
			numRecordsInFile++;
		long numRecordsPerChunk = HadoopMaster.chunkSize / HadoopMaster.recordSize;

		System.out.println(" num of Records in File = " + numRecordsInFile);
		System.out.println(" num of Records per Chunk = " + numRecordsPerChunk);

		numChunks = fileSize / (numRecordsPerChunk * HadoopMaster.recordSize);
		round = fileSize % (numRecordsPerChunk * HadoopMaster.recordSize);

		if (round != 0)
			numChunks++;
		System.out.println(" num of Chunks = " + numChunks);

	

		// Add all chunks to chunk queue, and assign to null workers initially
		for (int i = 0; i < numChunks; i++) {
			ChunkObject chunKey = new ChunkObject(i, i * numRecordsPerChunk,
					numRecordsPerChunk, (int) HadoopMaster.recordSize, inputFile);
			chunkQueue.add(chunKey);
			chunkWorkerMap.put(chunKey, -1);
		}

		// Map setup. Check if the partition folder exists
		File theDir = new File(Utils.getPartitionDirName(job.getJobName()));
		if (!theDir.exists()) {
			System.out.println("[INFO] Creating directory: " + Utils.getPartitionDirName(job.getJobName()));
			theDir.mkdir();
		}
		
		// Check if temporary worker output folder exists
		theDir = new File(Utils.getWorkerOutputFilesDirName(job.getJobName()));
		if (!theDir.exists()) {
			System.out.println("[INFO] Creating directory: " + Utils.getWorkerOutputFilesDirName(job.getJobName()));
			theDir.mkdir();
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
			synchronized (HadoopMaster.QUEUE_LOCK) {
				if (!HadoopMaster.freeWorkers.isEmpty() && !chunkQueue.isEmpty()) {
					ChunkObject chunkJob = null;
					int newWorker = 0;
					chunkJob = chunkQueue.remove();
					MapTask task = new MapTask(chunkJob, job.getFileInputFormatClass(), job.getMapperClass(),HadoopMaster.cp, job.getJobName(), HadoopMaster.allWorkers.get(newWorker));
					Message mapMessage = new Message(MessageType.START_MAP, task);
					newWorker = HadoopMaster.freeWorkers.remove();
					HadoopMaster.busyWorkerMap.put(newWorker, chunkJob);
					t_array[i] = new Thread(new ServiceMapThread(mapMessage, newWorker, HadoopMaster.allWorkers.get(newWorker), this));
					t_array[i].start();
					i++;
				}
			}
		}

		System.out.println("[INFO] Done Map. Starting data partitioning...");
		
		Partitioner.partitionMapOutputData(HadoopMaster.cp, job.getJobName());
		
		System.out.println("[INFO] Done partitioning. Starting reduce tasks...");
		reduceDoneMessages = new ConcurrentLinkedQueue<MessageType>();
		
		synchronized (this.MAPCOUNTER_LOCK) {
			try {
				System.out.println(" Waiting In Job Thread for reduce Command!!");
				this.MAPCOUNTER_LOCK.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Notified counter = " + this.mapsDone);
			System.out.println("[INFO] Sending reduce commands to " + HadoopMaster.numReducers + " reducers");
			int j = 0;
			int newReducer = 0;
			while( j < HadoopMaster.numReducers) {
				synchronized (HadoopMaster.QUEUE_LOCK) {
					if (HadoopMaster.freeWorkers.size()>0){
						newReducer = HadoopMaster.freeWorkers.remove();	
						WorkerInfo info = HadoopMaster.allWorkers.get(newReducer);
						ReduceTask task = new ReduceTask(j, job.getReducerClass(), job.getMapperOutputKeyClass(), job.getMapperOutputValueClass(), Utils.getFinalAnswersDir(), job.getJobName(), HadoopMaster.cp.getMapperOutputSize());
						Message reduceMsg = new Message(MessageType.START_REDUCE, task);
						new Thread(new ServiceReduceThread(info, reduceMsg)).start();
						j++;
					}
				}
			}
		}	
		
		while (true) {
			if (reduceDoneMessages.size() == HadoopMaster.numReducers) {
				Utils.removeDirectory(new File(Utils.getPartitionDirName(job.getJobName())));
				break;
			}
		}
		System.out.println("[INFO] Done " + job.getJobName() + " job");
	}	
}
