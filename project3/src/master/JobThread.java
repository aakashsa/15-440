package master;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import nodework.JobThreadSharedFields;

import lib.Job;
import lib.Partitioner;
import lib.Utils;

import communication.ChunkObject;
import communication.MapTask;
import communication.Message;
import communication.MessageType;
import communication.ReduceTask;
import communication.ServiceMapThread;
import communication.ServiceReduceThread;
import communication.WorkerInfo;

public class JobThread implements Runnable {

	// Chunk to Worker mapping for the Job
	private ConcurrentLinkedQueue<ChunkObject> chunkQueue;
	private ConcurrentHashMap<ChunkObject, Integer> chunkWorkerMap;
	private ConcurrentHashMap<ReduceTask, Integer> reduceWorkerMap;

	// Per Job Thread
	private ConcurrentLinkedQueue<MessageType> reduceDoneMessages;
	
	// Thread Queue For Kill Job 
	private ConcurrentLinkedQueue<Thread> threadQueue;
	
	private long numChunks = 0;
	
	private Job job;
	private String inputFile;
	
	private JobThreadSharedFields sharedData;
	
	public JobThread(String inputFile, Job job){
		this.inputFile = inputFile;	
		this.job = job;
		this.chunkQueue = new ConcurrentLinkedQueue<ChunkObject>();
		this.chunkWorkerMap = new ConcurrentHashMap<ChunkObject, Integer>();
		this.reduceWorkerMap = new ConcurrentHashMap<ReduceTask, Integer>();
		this.reduceDoneMessages = new ConcurrentLinkedQueue<MessageType>();
		System.out.println(" Reduce Worker Map in Constructor = " + reduceWorkerMap);
		this.sharedData = new JobThreadSharedFields(chunkQueue,chunkWorkerMap,reduceWorkerMap,reduceDoneMessages);
		this.threadQueue = new ConcurrentLinkedQueue<Thread>();
	}
	
	
	/**
	 * Cleans up performed - Kill Service Threads and 
	 * delete intermediate files Generated
	 * @return 
	 */
	public void jobCleanup(){
		while (!threadQueue.isEmpty()){
			threadQueue.remove().suspend();
		}
		for ( int worker : chunkWorkerMap.values()){
			if (worker>=0){
				HadoopMaster.freeWorkers.add(worker);
			}
		}
		for ( int worker : reduceWorkerMap.values()){
			if (worker>=0){
				HadoopMaster.freeWorkers.add(worker);
			}	
		}
		Utils.removeDirectory(new File(Utils.getPartitionDirName(job.getJobName())));
		Utils.removeDirectory(new File(Utils.getWorkerOutputFilesDirName(job.getJobName())));
	}
	
	@Override
	public void run() {

		// Get jobs and do sanity checks

		System.out.println("[INFO] Starting job " + job.getJobName());
		
		// Get input file name and size
		System.out.println("   Input file fath = " + inputFile);
		File f = new File(inputFile);
		if (f == null || !f.exists()) {
			System.out.println("[ERROR] Input file not found");
		} else {
			int fileSize = (int) f.length();
			System.out.println("   Input file size = " + fileSize);
	
			long round = (fileSize % this.job.getRecordSize());
			long numRecordsInFile = (fileSize / this.job.getRecordSize());
			if (round != 0)
				numRecordsInFile++;
			long numRecordsPerChunk = this.job.getChunkSize() / this.job.getRecordSize();
	
			System.out.println("   Num of records in input file = " + numRecordsInFile);
			System.out.println("   Num of records per chunk = " + numRecordsPerChunk);
	
			numChunks = fileSize / (numRecordsPerChunk * this.job.getRecordSize());
			round = fileSize % (numRecordsPerChunk * this.job.getRecordSize());
	
			if (round != 0)
				numChunks++;
			System.out.println("   Num of chunks = " + numChunks);
	
			// Add all chunks to chunk queue, and assign to null workers initially
			for (int i = 0; i < numChunks; i++) {
				ChunkObject chunKey = new ChunkObject(i, i * numRecordsPerChunk,
						numRecordsPerChunk, this.job.getRecordSize(), inputFile);
				chunkQueue.add(chunKey);
				chunkWorkerMap.put(chunKey, -1);
			}
			System.out.println("   Job Name = " + job.getJobName());
			
			// Map setup. Check if the partition folder exists
			File theDir = new File(Utils.getPartitionDirName(job.getJobName()));
			if (!theDir.exists()) {
				theDir.mkdir();
			}
			
			// Check if temporary worker output folder exists
			theDir = new File(Utils.getWorkerOutputFilesDirName(job.getJobName()));
			if (!theDir.exists()) {
				theDir.mkdir();
			}
					
			// Create reduce output directory if doesn't exist
			theDir = new File(Utils.getFinalAnswersDir( job.getJobName()));
			if (!theDir.exists()) {
				theDir.mkdir();
			}
			
			// Mapping
			int newWorker = 0;
	
			// Initially sending Chunks to workers
			while (!chunkWorkerMap.isEmpty() && !chunkQueue.isEmpty()) {
				synchronized (HadoopMaster.QUEUE_LOCK) {
					// If there are free workers and chunks to be done, then send them
					if (!HadoopMaster.freeWorkers.isEmpty() && !chunkQueue.isEmpty()) {
						ChunkObject chunkJob = chunkQueue.remove();
						newWorker = HadoopMaster.freeWorkers.remove();
						MapTask task = new MapTask(chunkJob, job.getFileInputFormatClass(), job.getMapperClass(), job.getMapperOutputRecordSize(), job.getJobName(), HadoopMaster.allWorkers.get(newWorker));
						Message mapMessage = new Message(MessageType.START_MAP, task);
						HadoopMaster.busyWorkerMap.put(newWorker, chunkJob);
						Thread t  = new Thread(new ServiceMapThread(mapMessage, newWorker, HadoopMaster.allWorkers.get(newWorker), this, sharedData));
						threadQueue.add(t);
						t.start();
					}
				}
			}
			// Checking if any of the workers died with a chunk and then we resend it to another worker
			while (true){
				synchronized (sharedData.getMapCounterLock()) {
					if (sharedData.getMapCounter() == numChunks){
						System.out.println("[INFO] All maps done.");
						break;
					}	
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// Fault Tolerance Resending Failed Tasks
				synchronized (HadoopMaster.QUEUE_LOCK) {
					if (!HadoopMaster.busyWorkerMap.isEmpty() &&  !chunkWorkerMap.isEmpty()) {
						Set<ChunkObject> chunkJob = chunkWorkerMap.keySet();
						for (ChunkObject chunk : chunkJob){
							if (!HadoopMaster.freeWorkers.isEmpty()){
								newWorker = HadoopMaster.freeWorkers.remove();
								MapTask task = new MapTask(chunk, job.getFileInputFormatClass(), job.getMapperClass(),job.getMapperOutputRecordSize(), job.getJobName(), HadoopMaster.allWorkers.get(newWorker));
								HadoopMaster.busyWorkerMap.put(newWorker, chunk);
								Message mapMessage = new Message(MessageType.START_MAP, task);
								Thread t = new Thread(new ServiceMapThread(mapMessage, newWorker, HadoopMaster.allWorkers.get(newWorker), this,sharedData));
								threadQueue.add(t);
								t.start();
							}
							else
								break;
						}
					}
				}
			}
			
			// Done with Maps. Start partitioning 
			System.out.println("[INFO] Done Map. Starting data partitioning...");		
			Partitioner.partitionMapOutputData(this.job.getNumReducers(), job.getJobName());
			System.out.println("[INFO] Done partitioning. Starting reduce tasks...");

			// Done partitioning. Partitioner named reduce input files as reducer_i, where
			// i increases from 0 until num_reducers - 1
			// Start sending reduce commands			
			System.out.println("[INFO] Sending reduce commands to " + this.job.getNumReducers() + " reducers");
			int j = 0;
			int newReducer = 0;
			while( j < this.job.getNumReducers()) {
				synchronized (HadoopMaster.QUEUE_LOCK) {
					if (HadoopMaster.freeWorkers.size() > 0){
						newReducer = HadoopMaster.freeWorkers.remove();	
						WorkerInfo info = HadoopMaster.allWorkers.get(newReducer);
						ReduceTask task = new ReduceTask(newReducer, job.getReducerClass(), 
								job.getMapperOutputKeyClass(), job.getMapperOutputValueClass(), 
								Utils.getFinalAnswersDir(job.getJobName()), job.getJobName(), 
								this.job.getMapperOutputRecordSize(), j);
						reduceWorkerMap.put(task,newReducer);
						Message reduceMsg = new Message(MessageType.START_REDUCE, task);
						Thread t = new Thread(new ServiceReduceThread(info, reduceMsg,sharedData));
						threadQueue.add(t);
						t.start();
						j++;
					}
				}
			}
			
			// All reduce commands sent. Wait for all reduce tasks to finish
			while (true) {
				try {
					System.out.println(" Waiting for Reducers.. ");
					Thread.sleep(180000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// If all Reduces have been Done
				if (reduceDoneMessages.size() >= this.job.getNumReducers()) {
					break;
				}
				synchronized (HadoopMaster.QUEUE_LOCK) {
					if (!reduceWorkerMap.isEmpty() &&  !(reduceDoneMessages.size() >= this.job.getNumReducers())) {
						Set<ReduceTask> reduceTasks = reduceWorkerMap.keySet();						
						for (ReduceTask task : reduceTasks){
							// Resend the reduce task that hasnt been completed in 3 minutes
							if (!HadoopMaster.freeWorkers.isEmpty()){
								System.out.println(" Waiting for Reducers.. ");
								newReducer = HadoopMaster.freeWorkers.remove();
								WorkerInfo info = HadoopMaster.allWorkers.get(newReducer);
								ReduceTask new_task = new ReduceTask(newReducer, job.getReducerClass(), 
										job.getMapperOutputKeyClass(), job.getMapperOutputValueClass(), 
										Utils.getFinalAnswersDir(job.getJobName()), job.getJobName(), 
										this.job.getMapperOutputRecordSize(), task.reducerInputFileNumber);
								System.out.println("[Info] Resending Reducer Task #" + task.reducerInputFileNumber);
								reduceWorkerMap.put(new_task,newReducer);
								Message reduceMsg = new Message(MessageType.START_REDUCE, new_task);
								Thread t = new Thread(new ServiceReduceThread(info, reduceMsg,sharedData));					
								threadQueue.add(t);
								t.start();
							}
							else{
								//No worker Empty Try Again later
								break;
							}
						}
					}
				}
			}
			Utils.removeDirectory(new File(Utils.getPartitionDirName(job.getJobName())));
			Utils.removeDirectory(new File(Utils.getWorkerOutputFilesDirName(job.getJobName())));				
			System.out.println("[INFO] Done " + job.getJobName() + " job");
			int id = Integer.parseInt((job.getJobName().split("_"))[1]);
			HadoopMaster.jobMap.remove(id);
		}
	}
}
